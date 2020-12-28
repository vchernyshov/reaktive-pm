/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.garage.rpm.map.google

expect class GoogleMapController(
    onCameraScrollStateChanged: ((scrolling: Boolean, isUserGesture: Boolean) -> Unit)?,
    onMarkerClickEvent: ((Any?) -> Unit)?
) : GoogleMapControllerHandler
