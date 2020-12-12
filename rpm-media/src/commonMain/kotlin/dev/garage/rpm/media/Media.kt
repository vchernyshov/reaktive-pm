/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.garage.rpm.media

import dev.garage.rpm.bitmap.Bitmap

data class Media(
    val name: String,
    val path: String,
    val preview: Bitmap,
    val type: MediaType
)
