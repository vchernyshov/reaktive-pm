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

dependencies {
    mppLibrary(Deps.Libs.MultiPlatform.kotlinStdLib)
    mppLibrary(Deps.Libs.MultiPlatform.reaktive)
    mppLibrary(Deps.Libs.MultiPlatform.reaktiveUtils)
    mppLibrary(Deps.Libs.MultiPlatform.reaktiveAnnotations)

    androidLibrary(Deps.Libs.Android.appCompat)
    androidLibrary(Deps.Libs.Android.material)

    "androidTestImplementation"("org.jetbrains.kotlin:kotlin-test-junit:${Versions.kotlin}")
    "androidTestImplementation"("com.badoo.reaktive:reaktive-testing:1.1.12")
    "androidTestImplementation"("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
    "androidTestImplementation"("org.mockito:mockito-inline:2.8.47")
}