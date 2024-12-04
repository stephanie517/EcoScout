// build.gradle.kts (Project-level)
plugins {
    id("com.android.application") version "8.6.0" apply false
    alias(libs.plugins.google.gms.google.services) apply false
}

buildscript {
    dependencies {
        // Ensure you have this classpath
        classpath ("com.google.gms:google-services:4.3.15")// or the latest version
    }
}
