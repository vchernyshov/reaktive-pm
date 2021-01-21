object Deps {

    object Libs {

        object MultiPlatform {

            val kotlinStdLib = MultiPlatformLibrary(
                android = Android.kotlinStdLib.name,
                common = "org.jetbrains.kotlin:kotlin-stdlib-common:${Versions.kotlin}"
            )

            val reaktive = MultiPlatformLibrary(
                common = "com.badoo.reaktive:reaktive:${Versions.reaktive}",
                iosX64 = "com.badoo.reaktive:reaktive-iossim:${Versions.reaktive}",
                iosArm64 = "com.badoo.reaktive:reaktive-ios64:${Versions.reaktive}"
            )

            val reaktiveUtils = MultiPlatformLibrary(
                common = "com.badoo.reaktive:utils:${Versions.reaktive}",
                iosX64 = "com.badoo.reaktive:utils-iossim:${Versions.reaktive}",
                iosArm64 = "com.badoo.reaktive:utils-ios64:${Versions.reaktive}"
            )

            val reaktiveAnnotations = MultiPlatformLibrary(
                common = "com.badoo.reaktive:reaktive-annotations:${Versions.reaktive}",
                iosX64 = "com.badoo.reaktive:reaktive-annotations-iossim:${Versions.reaktive}",
                iosArm64 = "com.badoo.reaktive:reaktive-annotations-ios64:${Versions.reaktive}"
            )

            val rpm = MultiPlatformModule(
                name = ":rpm",
                exported = true
            )

            val rpmPermissions = MultiPlatformModule(
                name = ":rpm-permissions",
                exported = true
            )

            val rpmLocation = MultiPlatformModule(
                name = ":rpm-location",
                exported = true
            )

            val appCommon = MultiPlatformModule(
                name = ":app-common"
            )
        }

        object Android {
            val kotlinStdLib = AndroidLibrary(
                name = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
            )

            val appCompat = AndroidLibrary(
                name = "androidx.appcompat:appcompat:${Versions.appCompat}"
            )

            val material = AndroidLibrary(
                name = "com.google.android.material:material:${Versions.material}"
            )

            val coreKtx = AndroidLibrary(
                name = "androidx.core:core-ktx:${Versions.coreKtx}"
            )

            val constraint = AndroidLibrary(
                name = "androidx.constraintlayout:constraintlayout:${Versions.constraint}"
            )

            val timber = AndroidLibrary(
                name = "com.jakewharton.timber:timber:${Versions.timber}"
            )

            val libphonenumber = AndroidLibrary(
                name = "com.googlecode.libphonenumber:libphonenumber:${Versions.libphonenumber}"
            )

            val location = AndroidLibrary (
                name = "com.google.android.gms:play-services-location:${Versions.location}"
            )
        }
    }
}

object Versions {

    val kotlin = "1.3.71"
    val reaktive = "1.1.12"
    val appCompat = "1.2.0-alpha03"
    val material = "1.2.0-alpha05"
    val coreKtx = "1.2.0"
    val constraint = "1.1.3"
    val timber = "4.7.1"
    val libphonenumber = "8.11.1"
    val rpm = "0.2.1-beta"
    val location = "17.0.0"

    object Android {

        val targetSdk = 29
        val minSdk = 26
        val compileSdk = 29
    }
}