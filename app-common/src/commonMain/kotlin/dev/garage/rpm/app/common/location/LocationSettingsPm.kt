package dev.garage.rpm.app.common.location

import com.badoo.reaktive.maybe.doOnBeforeSubscribe
import com.badoo.reaktive.maybe.doOnBeforeSuccess
import com.badoo.reaktive.observable.flatMapMaybe
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.action
import dev.garage.rpm.location.locationSettingsControl
import dev.garage.rpm.state

class LocationSettingsPm : PresentationModel() {

    val status = state("Initial")
    val locationSettingsControl = locationSettingsControl()
    val checkLocationSettings = action<Unit> {
        flatMapMaybe {
            locationSettingsControl.checkAndEnable()
                .doOnBeforeSubscribe {
                    status.accept("Checking")
                }
                .doOnBeforeSuccess {
                    status.accept(it.name)
                }
        }
    }
}