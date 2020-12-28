package dev.garage.rpm.map.data

import dev.garage.rpm.map.LatLng
import dev.icerock.moko.resources.ImageResource

data class MarkerData(
    val image: ImageResource,
    val latLng: LatLng,
    val rotation: Float,
    val tag: Any
)
