package org.gnit.lucenekmp.cli

import okio.FileSystem
import okio.Path

fun readResourceDocuments(
    fileSystem: FileSystem,
    root: Path,
    config: LcConfig,
): Sequence<SearchDocument> {
    val extractors = config.fields.map { FieldExtractor(it) }
    return eligibleResourcePaths(fileSystem, root, config)
        .asSequence()
        .mapNotNull { path ->
            val content = runCatching { fileSystem.read(path) { readUtf8() } }.getOrNull()
                ?: return@mapNotNull null
            extractDocument(root, path, content, extractors)
        }
}

fun indexResourcesConcurrently(
    fileSystem: FileSystem,
    root: Path,
    config: LcConfig,
    engine: LuceneCliEngine,
    workers: Int,
): IndexingReport {
    val extractors = config.fields.map { FieldExtractor(it) }
    val sources = eligibleResourcePaths(fileSystem, root, config).map { path ->
        val id = path.relativeTo(root).toString()
        DocumentSource(id) {
            val content = fileSystem.read(path) { readUtf8() }
            extractDocument(root, path, content, extractors)
        }
    }
    return engine.addAllConcurrent(sources, workers)
}

private fun eligibleResourcePaths(
    fileSystem: FileSystem,
    root: Path,
    config: LcConfig,
): List<Path> = fileSystem.listRecursively(root)
    .filter { fileSystem.metadata(it).isRegularFile }
    .filter { it.name.substringAfterLast('.', "").lowercase() in config.extensions }
    .toList()
    .sortedBy(Path::toString)

private fun extractDocument(
    root: Path,
    path: Path,
    content: String,
    extractors: List<FieldExtractor>,
): SearchDocument? {
    val id = path.relativeTo(root).toString()
    val fields = extractors.associate { extractor ->
        extractor.field.name to extractor.extract(id, content)
    }
    return SearchDocument(id, fields).takeIf { fields.values.any(String::isNotBlank) }
}

internal fun extractField(field: FieldConfig, path: String, content: String): String {
    return FieldExtractor(field).extract(path, content)
}

private class FieldExtractor(val field: FieldConfig) {
    private val regex = field.pattern?.let { Regex("(?is)$it") }

    fun extract(path: String, content: String): String {
        val source = if (field.source == "path") path else content
        val matches = regex?.findAll(source) ?: return source
        val values = matches.mapNotNull { match -> match.groupValues.getOrNull(field.group) }
        return if (field.matchAll) {
            values.take(field.maxMatches).joinToString(" ")
        } else {
            values.firstOrNull().orEmpty()
        }
    }
}
