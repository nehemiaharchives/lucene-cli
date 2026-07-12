package org.gnit.lucenekmp.cli

import com.github.ajalt.clikt.core.CliktError
import com.github.ajalt.clikt.core.CoreCliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM
import org.gnit.lucenekmp.store.FSDirectory

data class LcConfig(
    val indexPath: String,
    val analyzer: String = "EnglishAnalyzer",
)

fun readConfig(fileSystem: FileSystem, configPath: String): LcConfig {
    val root = Json.parseToJsonElement(fileSystem.read(configPath.toPath()) { readUtf8() }).jsonObject
    return LcConfig(
        indexPath = root["indexPath"]?.jsonPrimitive?.content
            ?: throw CliktError("config must contain indexPath"),
        analyzer = root["analyzer"]?.jsonPrimitive?.content ?: "EnglishAnalyzer",
    )
}

class Lc(
    private val fileSystem: FileSystem = FileSystem.SYSTEM,
) : CoreCliktCommand() {
    private val configPath by option("-c", "--config", help = "Path to lucene-cli.json")
        .default("lucene-cli.json")

    init {
        val config = { readConfig(fileSystem, configPath) }
        subcommands(Index(config), Search(config))
    }

    override fun run() = Unit
}

class Index(private val config: () -> LcConfig) : CoreCliktCommand() {
    init {
        subcommands(IndexCreate(config), IndexAdd(config), IndexUpdate(), IndexDelete())
    }

    override fun run() = Unit
}

class IndexCreate(private val config: () -> LcConfig) : CoreCliktCommand(name = "create") {
    override fun run() {
        val current = config()
        FSDirectory.open(current.indexPath.toPath()).use { directory ->
            LuceneCliEngine(directory, current.analyzer.toAnalyzer()).create()
        }
        echo("Created index at ${current.indexPath}")
    }
}

class IndexAdd(private val config: () -> LcConfig) : CoreCliktCommand(name = "add") {
    private val id by option("--id", help = "Unique document identifier").required()
    private val body by option("--body", help = "English document text").required()

    override fun run() {
        val current = config()
        FSDirectory.open(current.indexPath.toPath()).use { directory ->
            LuceneCliEngine(directory, current.analyzer.toAnalyzer()).add(EnglishDocument(id, body))
        }
        echo("Added document $id")
    }
}

class IndexUpdate : CoreCliktCommand(name = "update") {
    override fun run(): Nothing = throw CliktError("index update is not implemented yet")
}

class IndexDelete : CoreCliktCommand(name = "delete") {
    override fun run(): Nothing = throw CliktError("index delete is not implemented yet")
}

class Search(private val config: () -> LcConfig) : CoreCliktCommand() {
    private val query by option("-q", "--query", help = "English query text").required()

    override fun run() {
        val current = config()
        val results = FSDirectory.open(current.indexPath.toPath()).use { directory ->
            LuceneCliEngine(directory, current.analyzer.toAnalyzer()).search(query)
        }
        results.forEach { result ->
            echo("id=${result.id}\tscore=${result.score}\tbody=${result.body}")
        }
    }
}

fun main(args: Array<String>) {
    Lc().main(args)
}
