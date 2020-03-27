plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
}

android {
    compileSdkVersion(Versions.Android.compileSdk)

    defaultConfig {
        applicationId = "dev.garage.rpm.app"
        minSdkVersion(Versions.Android.minSdk)
        targetSdkVersion(Versions.Android.targetSdk)
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        getByName("debug") {
            isDebuggable = true
            applicationIdSuffix = ".debug"
        }
    }
}

dependencies {
    implementation(Deps.Libs.MultiPlatform.kotlinStdLib.common!!)
    implementation(Deps.Libs.MultiPlatform.reaktive.common!!)
    implementation(Deps.Libs.MultiPlatform.reaktiveUtils.common!!)

    implementation(project(Deps.Libs.MultiPlatform.rpm.name))
    implementation(project(Deps.Libs.MultiPlatform.appCommon.name))

    implementation(Deps.Libs.Android.appCompat.name)
    implementation(Deps.Libs.Android.material.name)
    implementation(Deps.Libs.Android.coreKtx.name)
    implementation(Deps.Libs.Android.constraint.name)
    implementation(Deps.Libs.Android.timber.name)
}