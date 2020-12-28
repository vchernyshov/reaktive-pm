/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.garage.rpm.map.google

import dev.garage.rpm.map.MapController

interface GoogleMapControllerHandler : MapController{

    fun readUiSettings(): UiSettings

    fun writeUiSettings(settings: UiSettings)
}
