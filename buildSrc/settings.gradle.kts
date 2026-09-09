import java.io.File
import java.util.Locale

rootProject.name = "impulse-build-logic"

// Keep build logic caches outside the cloud directory, just like the main build.
val mainProjectDir = settingsDir.parentFile
val workspaceId = mainProjectDir.absolutePath.lowercase(Locale.ROOT).hashCode().toUInt().toString(16)
val stateRoot = if (File.separatorChar == '\\') {
    mainProjectDir.toPath().root.toFile().resolve("GradleWorkspaces")
} else {
    File(System.getProperty("user.home"), ".cache/GradleWorkspaces")
}
gradle.startParameter.projectCacheDir =
    stateRoot.resolve("${mainProjectDir.name}-$workspaceId/build-logic/project-cache")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}
