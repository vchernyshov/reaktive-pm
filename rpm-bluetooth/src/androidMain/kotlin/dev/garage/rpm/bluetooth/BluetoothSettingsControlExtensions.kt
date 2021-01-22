package dev.garage.rpm.bluetooth

import androidx.fragment.app.FragmentManager
import dev.garage.rpm.bindTo

fun BluetoothSettingsControl.bindTo(fragmentManager: FragmentManager) {

    this.check.bindTo {
        val currentFragment = fragmentManager.findFragmentByTag(ResolverFragment.BLUETOOTH_RESOLVER_TAG)
        val resolverFragment = if (currentFragment != null) {
            currentFragment as ResolverFragment
        } else {
            ResolverFragment().apply {
                fragmentManager
                    .beginTransaction()
                    .add(this, ResolverFragment.BLUETOOTH_RESOLVER_TAG)
                    .commitNow()
            }
        }

        resolverFragment.checkBluetoothSettings {
            this.result.consumer.onNext(it)
        }
    }
}