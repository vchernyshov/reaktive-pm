package dev.garage.rpm.app.common.bluetooth

import com.badoo.reaktive.maybe.doOnBeforeSubscribe
import com.badoo.reaktive.maybe.doOnBeforeSuccess
import com.badoo.reaktive.observable.flatMapMaybe
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.action
import dev.garage.rpm.bluetooth.bluetoothSettingsControl
import dev.garage.rpm.state

class BluetoothSettingsPm : PresentationModel() {

    val status = state("Initial")
    val bluetoothSettingsControl = bluetoothSettingsControl()
    val checkBluetoothSetting = action<Unit> {
        flatMapMaybe {
            bluetoothSettingsControl.checkAndEnable()
                .doOnBeforeSubscribe {
                    status.accept("Checking")
                }
                .doOnBeforeSuccess {
                    status.accept(it.name)
                }
        }
    }
}