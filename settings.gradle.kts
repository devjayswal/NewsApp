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
include(":core:network")         // core modules  including so that we can use it anywhere
include(":core:database")    // core modules  including so that we can use it anywhere
include(":core:utils")   // core modules  including so that we can use it anywhere
include(":core:common-ui")   // core modules  including so that we can use it anywhere

// Feature Modules
include(":feature:home")            // including feature  so that we can use it too inside project
include(":feature:appointments")            // including feature  so that we can use it too inside project
include(":feature:pharmacy")            // including feature  so that we can use it too inside project
include(":feature:records")         // including feature  so that we can use it too inside project
include(":feature:family")  // including feature  so that we can use it too inside project
include(":feature:profile") // including feature  so that we can use it too inside project
include(":feature:settings")    // including feature  so that we can use it too inside project