/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.garage.rpm.bitmap

actual class Bitmap {

    actual fun toByteArray(): ByteArray {
        return ByteArray(0)
    }

    actual fun toBase64(): String {
        return ""
    }

    actual fun toBase64WithCompress(maxSize: Int): String {
        return ""
    }
}
