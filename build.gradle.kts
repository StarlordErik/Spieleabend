import java.io.File
import java.util.Locale

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
        classpath(libs.kotlin.gradle.plugin)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kover) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.room) apply false
}

fun localGradleStateDir(projectDir: File): File {
    val baseDir = if (File.separatorChar == '\\') {
        projectDir.toPath().root?.toFile()?.resolve("GradleWorkspaces")
    } else {
        null
    } ?: System.getenv("LOCALAPPDATA")
        ?.takeIf(String::isNotBlank)
        ?.let(::File)
        ?.resolve("GradleWorkspaces")
        ?: File(System.getProperty("user.home"), ".cache").resolve("GradleWorkspaces")
    val workspaceId = projectDir.absolutePath.lowercase(Locale.ROOT).hashCode().toUInt().toString(16)

    return baseDir.resolve("${projectDir.name}-$workspaceId")
}

val localBuildRoot = localGradleStateDir(rootProject.projectDir).resolve("build")

allprojects {
    val projectBuildDir = if (path == ":") {
        "root"
    } else {
        path.removePrefix(":").replace(':', '/')
    }

    layout.buildDirectory.set(localBuildRoot.resolve(projectBuildDir))
}

apply(from = "gradle/rohdaten-db.gradle.kts")
