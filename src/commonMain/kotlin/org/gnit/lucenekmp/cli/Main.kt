package org.gnit.lucenekmp.cli

import com.github.ajalt.clikt.core.CoreCliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.option
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.SYSTEM
import org.gnit.lucenekmp.analysis.Analyzer
import org.gnit.lucenekmp.analysis.en.EnglishAnalyzer
import org.gnit.lucenekmp.analysis.en.ct.BibleEnglishAnalyzer
import org.gnit.lucenekmp.analysis.standard.StandardAnalyzer
import org.gnit.lucenekmp.document.Document
import org.gnit.lucenekmp.document.Field
import org.gnit.lucenekmp.document.StringField
import org.gnit.lucenekmp.index.IndexWriter
import org.gnit.lucenekmp.index.IndexWriterConfig
import org.gnit.lucenekmp.index.StandardDirectoryReader
import org.gnit.lucenekmp.queryparser.classic.QueryParser
import org.gnit.lucenekmp.search.IndexSearcher
import org.gnit.lucenekmp.store.FSDirectory

data class LcConfig(
    val indexPath: String,
    val analyzer: String = StandardAnalyzer::class.simpleName!!,
    //TODO add more config entries such as language, languageAnalyzer, IndexWriterConfig entries etc
    val fieldConfigs: List<FieldConfig>
)

data class FieldConfig(
    val fieldType: String,
    val name: String,
    val store: String
)

fun String.toAnalyzer(): Analyzer
    = when(this){
        // analysis-common
        "EnglishAnalyzer" -> EnglishAnalyzer()
        "BibleEnglishAnalyzer" -> BibleEnglishAnalyzer()
        //TODO add all common analyzers

        // analysis-morfologik
        // TODO add all analyzers from all analysis-x modules


    else -> throw RuntimeException("$this is not supported lucene-kmp Analyzer")
}

fun String.toField(/* TODO add more args if needed */): Field {
    return TODO("refactor this to properly set field")
}

object LcConfigReader {
    lateinit var fileSystem: FileSystem
    lateinit var config: LcConfig

    fun init(fileSystemToSet: FileSystem){
        fileSystem = fileSystemToSet
    }

    fun read(configPath: String?){
        val path: Path = (configPath?: "lucene-cli.json").toPath() // ot lucene-cli.yml ?
        val configString = fileSystem.read(path) {
            readUtf8()
        }
        config = Json.decodeFromString<LcConfig>(configString)
    }
}

class Lc(val fileSystem: FileSystem = FileSystem.SYSTEM): CoreCliktCommand() {

    val configPath: String? by option("-c", "--config", help = "path of lucene-cli.json")

    init {
        subcommands(Index(), Search())
    }

    override fun run() {
        echo("lucene cli")
    }
}

class Index: CoreCliktCommand(){

    init {
        subcommands(IndexCreate(), IndexAdd(), IndexUpdate(), IndexDelete())
    }

    override fun run() {
        TODO("Not yet implemented")
    }
}

class IndexCreate: CoreCliktCommand(name = "create"){
    override fun run() {

        val config: LcConfig = LcConfigReader.config

        val directory = FSDirectory.open(path = config.indexPath.toPath())
        val analyzer = config.analyzer.toAnalyzer()
        IndexWriter(directory, IndexWriterConfig(analyzer)).use { writer ->
            val doc = Document().apply {
                config.fieldConfigs.forEach { fc ->
                    add(fc.fieldType.toField(/* TODO add more args if needed */))

                    // the above line should be similar to following operation by automatically find values from dir and config entries and need to follow well thought default conventions for human users and ai agents have easy UX
                    //add(StringField("id", "note-1", Field.Store.YES))
                    //add(TextField("body", "lucene-kmp brings Lucene-style full-text search to Android and iOS.", Field.Store.YES))
                }
            }
        }

        TODO("Not yet implemented")
    }
}

class IndexAdd: CoreCliktCommand(name = "add"){
    override fun run() {
        TODO("Not yet implemented")
    }
}

class IndexUpdate: CoreCliktCommand(name = "update"){
    override fun run() {
        TODO("Not yet implemented")
    }
}

class IndexDelete: CoreCliktCommand(name = "delete"){
    override fun run() {
        TODO("Not yet implemented")
    }
}

class Search: CoreCliktCommand(){
    override fun run() {
        val config: LcConfig = LcConfigReader.config

        val directory = FSDirectory.open(path = config.indexPath.toPath())
        val analyzer = config.analyzer.toAnalyzer()

        val reader = StandardDirectoryReader.open(directory, null, null)
        val searcher = IndexSearcher(reader)
        val query = QueryParser("body", analyzer).parse("full-text search")!!
        val hits = searcher.search(query, 10)
        val storedFields = searcher.storedFields()
        for (scoreDoc in hits.scoreDocs) {
            val doc = storedFields.document(scoreDoc.doc)
            println("id=${doc.get("id")} score=${scoreDoc.score}")
        }

        TODO("adjust above code so that the search result to be something meaningful and useful for both human users and ai agents")
    }
}

fun main(args: Array<String>){
    Lc().main(args)
}
