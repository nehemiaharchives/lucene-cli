@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

group = "org.gnit"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    @Suppress("DEPRECATION")
    val nativeTargets = listOf(
        macosX64(),
        macosArm64(),
        linuxX64(),
        linuxArm64(),
        mingwX64(),
    )
    nativeTargets.forEach { target ->
        target.binaries.executable {
            baseName = "lc"
            entryPoint = "org.gnit.lucenekmp.cli.main"
        }
    }
    jvm()

    dependencies {
        implementation(libs.lucene.kmp.core)
        implementation(libs.lucene.kmp.analysis.common)
        implementation(libs.lucene.kmp.analysis.morfologik)
        implementation(libs.lucene.kmp.analysis.smartcn)
        implementation(libs.lucene.kmp.analysis.nori)
        implementation(libs.lucene.kmp.analysis.kuromoji)
        implementation(libs.lucene.kmp.analysis.extra)
        implementation(libs.lucene.kmp.queryparser)
        implementation(libs.clikt)
        implementation(libs.okio)
        implementation(libs.kotlinx.serialization.json)
        implementation(libs.kotlin.logging)

        testImplementation(libs.kotlin.test)
        testImplementation(libs.okio.fakefs)
    }
}
