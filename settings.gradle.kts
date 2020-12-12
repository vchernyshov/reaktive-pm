pluginManagement {
    repositories {
        jcenter()
        google()

        maven { url = uri("https://dl.bintray.com/kotlin/kotlin") }
        maven { url = uri("https://kotlin.bintray.com/kotlinx") }
        maven { url = uri("https://jetbrains.bintray.com/kotlin-native-dependencies") }
    }
}

enableFeaturePreview("GRADLE_METADATA")

include(":rpm")
include(":rpm-permissions")
include(":rpm-base-lpc")
include(":rpm-lc")
include(":rpm-pc")
include(":app-common")
include(":app-android")

rootProject.name = "reaktive-pm"