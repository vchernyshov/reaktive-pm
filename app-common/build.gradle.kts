plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("dev.icerock.mobile.multiplatform")
}

android {
    compileSdkVersion(Versions.Android.compileSdk)

    defaultConfig {
        minSdkVersion(Versions.Android.minSdk)
        targetSdkVersion(Versions.Android.targetSdk)
    }
}

setupFramework(
    exports = listOf(
        Deps.Libs.MultiPlatform.reaktive,
        Deps.Libs.MultiPlatform.reaktiveUtils,
        Deps.Libs.MultiPlatform.rpm,
        Deps.Libs.MultiPlatform.rpmPermissions
    )
)

dependencies {
    mppModule(Deps.Libs.MultiPlatform.rpm)
    mppModule(Deps.Libs.MultiPlatform.rpmPermissions)

    mppLibrary(Deps.Libs.MultiPlatform.kotlinStdLib)
    mppLibrary(Deps.Libs.MultiPlatform.reaktive)
    mppLibrary(Deps.Libs.MultiPlatform.reaktiveUtils)
    mppLibrary(Deps.Libs.MultiPlatform.reaktiveAnnotations)

    androidLibrary(Deps.Libs.Android.appCompat)
}