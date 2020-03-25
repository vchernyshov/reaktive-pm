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
    implementation(Deps.Libs.MultiPlatform.kotlinStdLib.android!!)
    implementation(Deps.Libs.MultiPlatform.reaktive.android!!)
    implementation(Deps.Libs.MultiPlatform.reaktiveUtils.android!!)

    implementation(project(":rpm"))
    implementation(project(":app-common"))

    implementation(Deps.Libs.Android.appCompat.name)
    implementation(Deps.Libs.Android.material.name)
    implementation(Deps.Libs.Android.coreKtx.name)
    implementation(Deps.Libs.Android.constraint.name)
    implementation(Deps.Libs.Android.timber.name)
}