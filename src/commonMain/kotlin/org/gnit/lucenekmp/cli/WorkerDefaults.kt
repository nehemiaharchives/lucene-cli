package org.gnit.lucenekmp.cli

internal const val MAX_DEFAULT_WORKERS = 8

internal expect fun availableProcessorCount(): Int

fun defaultWorkerCount(): Int = availableProcessorCount().coerceAtLeast(1).coerceAtMost(MAX_DEFAULT_WORKERS)
