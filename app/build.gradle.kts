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
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            isDebuggable = true
            applicationIdSuffix = ".debug"
        }
    }
}

dependencies {

    implementation(project(":rpm"))

    implementation(Deps.Libs.MultiPlatform.kotlinStdLib.android!!)
    implementation(Deps.Libs.MultiPlatform.reaktive.android!!)
    implementation(Deps.Libs.MultiPlatform.reaktiveUtils.android!!)

    implementation(Deps.Libs.Android.appCompat.name)
    implementation(Deps.Libs.Android.material.name)
    implementation(Deps.Libs.Android.coreKtx.name)
    implementation(Deps.Libs.Android.constraint.name)
    implementation(Deps.Libs.Android.timber.name)
    implementation (Deps.Libs.Android.libphonenumber.name)
}