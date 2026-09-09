plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

layout.buildDirectory.set(requireNotNull(gradle.startParameter.projectCacheDir).parentFile.resolve("build"))

dependencies {
    implementation(libs.sqlite.jdbc)
}
