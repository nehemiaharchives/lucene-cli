@file:OptIn(kotlin.experimental.ExperimentalNativeApi::class)

package org.gnit.lucenekmp.cli

import kotlin.native.Platform

internal actual fun availableProcessorCount(): Int = Platform.getAvailableProcessors()
