plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "de.impulse.spieleabend"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "de.impulse.spieleabend"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
 }

dependencies {
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
}
