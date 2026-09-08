import java.io.File
import java.util.Locale

fun localGradleStateDir(projectDir: File, projectName: String): File {
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

    return baseDir.resolve("$projectName-$workspaceId")
}

rootProject.name = "Impulse"

// Keep project-local Gradle state out of cloud-synced workspaces to avoid file locks on Windows.
gradle.startParameter.projectCacheDir = localGradleStateDir(settingsDir, rootProject.name).resolve("project-cache")

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

include(":app")
