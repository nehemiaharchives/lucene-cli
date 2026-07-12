package org.gnit.lucenekmp.cli

internal actual fun availableProcessorCount(): Int = Runtime.getRuntime().availableProcessors()
