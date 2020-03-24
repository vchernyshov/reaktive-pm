object Deps {

    object Libs {

        object MultiPlatform {

            val kotlinStdLib = MultiPlatformLibrary(
                android = Android.kotlinStdLib.name,
                common = "org.jetbrains.kotlin:kotlin-stdlib-common:${Versions.kotlin}"
            )

            val reaktive = MultiPlatformLibrary(
                common = "com.badoo.reaktive:reaktive:${Versions.reaktive}",
                android = "com.badoo.reaktive:reaktive-android-debug${Versions.reaktive}",
                iosX64 = "com.badoo.reaktive:reaktive-iossim${Versions.reaktive}",
                iosArm64 = "com.badoo.reaktive:reaktive-ios64${Versions.reaktive}"
            )

            val reaktiveUtils = MultiPlatformLibrary(
                common = "com.badoo.reaktive:utils:${Versions.reaktive}",
                android = "com.badoo.reaktive:utils-android-debug${Versions.reaktive}",
                iosX64 = "com.badoo.reaktive:utils-iossim${Versions.reaktive}",
                iosArm64 = "com.badoo.reaktive:utils-ios64${Versions.reaktive}"
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
        }
    }
}

object Versions {

    val kotlin = "1.3.71"
    val reaktive = "1.1.12"
    val appCompat = "1.2.0-alpha03"
    val material = "1.2.0-alpha05"

    object Android {

        val targetSdk = 29
        val minSdk = 26
        val compileSdk = 29
    }
}