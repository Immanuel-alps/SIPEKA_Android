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
        // Pertemuan 9 – Fitur 2: MPAndroidChart diambil dari JitPack
        // Sumber: https://github.com/PhilJay/MPAndroidChart#gradle-setup
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "SIPEKA"
include(":app")
