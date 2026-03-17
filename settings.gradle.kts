rootProject.name = "Kmp"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

// settings.gradle.kts

include(":composeApp")
include(":shared")

// Core Modules
include(":core:network")
include(":core:database")
include(":core:utils")
include(":core:common-ui")

// Feature Modules
include(":feature:home")
include(":feature:appointments")
include(":feature:pharmacy")
include(":feature:records")
include(":feature:family")
include(":feature:profile")
include(":feature:settings")