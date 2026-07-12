package org.gnit.lucenekmp.cli

import com.github.ajalt.clikt.core.CliktError
import com.github.ajalt.clikt.core.CoreCliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.int
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okio.FileSystem
import okio.Path.Companion.toPath
import org.gnit.lucenekmp.store.FSDirectory
import kotlin.time.TimeSource

data class LcConfig(
    val indexPath: String,
    val analyzer: String = "EnglishAnalyzer",
    val extensions: Set<String> = setOf("txt", "md", "html", "htm", "json", "jsonl", "csv"),
    val fields: List<FieldConfig> = listOf(FieldConfig("body")),
)

data class FieldConfig(
    val name: String,
    val source: String = "content",
    val pattern: String? = null,
    val group: Int = 1,
    val matchAll: Boolean = false,
    val maxMatches: Int = 100,
    val boost: Float = 1f,
    val store: Boolean = true,
)

fun readConfig(fileSystem: FileSystem, configPath: String): LcConfig {
    val root = Json.parseToJsonElement(fileSystem.read(configPath.toPath()) { readUtf8() }).jsonObject
    val fields = root["fields"]?.jsonArray?.map { parseFieldConfig(it.jsonObject) }
        ?: listOf(FieldConfig("body"))
    return LcConfig(
        indexPath = root["indexPath"]?.jsonPrimitive?.content
            ?: throw CliktError("config must contain indexPath"),
        analyzer = root["analyzer"]?.jsonPrimitive?.content ?: "EnglishAnalyzer",
        extensions = root["extensions"]?.jsonArray?.map { it.jsonPrimitive.content.lowercase() }?.toSet()
            ?: LcConfig("").extensions,
        fields = fields,
    )
}

private fun parseFieldConfig(value: JsonObject): FieldConfig = FieldConfig(
    name = value.getValue("name").jsonPrimitive.content,
    source = value["source"]?.jsonPrimitive?.content ?: "content",
    pattern = value["pattern"]?.jsonPrimitive?.content,
    group = value["group"]?.jsonPrimitive?.intOrNull ?: 1,
    matchAll = value["matchAll"]?.jsonPrimitive?.booleanOrNull ?: false,
    maxMatches = value["maxMatches"]?.jsonPrimitive?.intOrNull ?: 100,
    boost = value["boost"]?.jsonPrimitive?.floatOrNull ?: 1f,
    store = value["store"]?.jsonPrimitive?.booleanOrNull ?: true,
)

class Lc(
    private val fileSystem: FileSystem = FileSystem.SYSTEM,
) : CoreCliktCommand() {
    private val configPath by option("-c", "--config", help = "Path to lucene-cli.json")
        .default("lucene-cli.json")

    init {
        val config = { readConfig(fileSystem, configPath) }
        subcommands(Index(config, fileSystem), Search(config))
    }

    override fun run() = Unit
}

class Index(
    private val config: () -> LcConfig,
    private val fileSystem: FileSystem,
) : CoreCliktCommand() {
    init {
        subcommands(IndexCreate(config, fileSystem), IndexAdd(config), IndexUpdate(), IndexDelete())
    }

    override fun run() = Unit
}

class IndexCreate(
    private val config: () -> LcConfig,
    private val fileSystem: FileSystem = FileSystem.SYSTEM,
) : CoreCliktCommand(name = "create") {
    private val source by argument(help = "Directory of documents to index").optional()
    private val workers by option(
        "--workers",
        help = "Concurrent extraction/indexing workers (default: available processors, capped at $MAX_DEFAULT_WORKERS)",
    ).int().default(defaultWorkerCount())

    override fun run() {
        if (workers <= 0) throw CliktError("--workers must be positive")

        val current = config()
        val sourcePath = source?.toPath()
        FSDirectory.open(current.indexPath.toPath()).use { directory ->
            val engine = LuceneCliEngine(directory, current.fields, current.analyzer.toAnalyzer())
            engine.create()
            if (sourcePath != null) {
                val started = TimeSource.Monotonic.markNow()
                val report = indexResourcesConcurrently(fileSystem, sourcePath, current, engine, workers)
                val elapsedMs = started.elapsedNow().inWholeMilliseconds
                echo(
                    "Indexed ${report.indexed} files from $sourcePath " +
                        "with $workers workers in ${elapsedMs}ms " +
                        "(skipped=${report.skipped}, errors=${report.errors})"
                )
                report.failures.forEach { failure ->
                    echo("ERROR ${failure.id}: ${failure.message}", err = true)
                }
                if (report.errors > 0) {
                    throw CliktError("Indexing completed with ${report.errors} errors")
                }
            }
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
            val fields = current.fields.associate { it.name to body }
            LuceneCliEngine(directory, current.fields, current.analyzer.toAnalyzer()).add(SearchDocument(id, fields))
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
    private val limit by option("-n", "--limit", help = "Maximum results").int().default(10)

    override fun run() {
        val current = config()
        val results = FSDirectory.open(current.indexPath.toPath()).use { directory ->
            LuceneCliEngine(directory, current.fields, current.analyzer.toAnalyzer()).search(query, limit)
        }
        results.forEach { result ->
            val fields = result.fields.entries.joinToString("\t") { (name, value) -> "$name=$value" }
            echo("id=${result.id}\tscore=${result.score}\t$fields")
        }
    }
}

fun main(args: Array<String>) {
    Lc().main(args)
}
