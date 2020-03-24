object Deps {

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

    val appCompat = AndroidLibrary(
        name = "androidx.appcompat:appcompat:${Versions.appCompat}"
    )

    val material = AndroidLibrary(
        name = "com.google.android.material:material:${Versions.material}"
    )
}

object Versions {

    val reaktive = "1.1.12"
    val appCompat = "1.2.0-alpha03"
    val material = "1.2.0-alpha05"
}