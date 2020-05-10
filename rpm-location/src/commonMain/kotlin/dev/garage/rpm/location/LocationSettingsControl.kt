package dev.garage.rpm.location

import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.observable.doOnBeforeSubscribe
import com.badoo.reaktive.observable.firstOrComplete
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.action
import dev.garage.rpm.command

class LocationSettingsControl internal constructor() : PresentationModel() {

    internal val check = command<Unit>()
    internal val result = action<LocationSettingsResult>()

    fun checkAndEnable(): Maybe<LocationSettingsResult> =
        result.observable()
            .doOnBeforeSubscribe { check.accept(Unit) }
            .firstOrComplete()
}

fun PresentationModel.locationSettingsControl(): LocationSettingsControl =
    LocationSettingsControl().apply {
        attachToParent(this@locationSettingsControl)
    }