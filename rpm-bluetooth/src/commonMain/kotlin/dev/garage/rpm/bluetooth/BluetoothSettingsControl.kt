package dev.garage.rpm.bluetooth

import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.observable.doOnBeforeSubscribe
import com.badoo.reaktive.observable.firstOrComplete
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.action
import dev.garage.rpm.command

class BluetoothSettingsControl internal constructor() : PresentationModel() {

    internal val check = command<Unit>()
    internal val result = action<BluetoothSettingsResult>()

    fun checkAndEnable(): Maybe<BluetoothSettingsResult> =
        result.observable()
            .doOnBeforeSubscribe { check.accept(Unit) }
            .firstOrComplete()
}

fun PresentationModel.bluetoothSettingsControl(): BluetoothSettingsControl =
    BluetoothSettingsControl().apply {
        attachToParent(this@bluetoothSettingsControl)
    }