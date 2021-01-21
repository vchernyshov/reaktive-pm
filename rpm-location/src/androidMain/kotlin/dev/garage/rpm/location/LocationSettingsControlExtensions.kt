package dev.garage.rpm.location

import androidx.fragment.app.FragmentManager
import dev.garage.rpm.bindTo

fun LocationSettingsControl.bindTo(fragmentManager: FragmentManager) {

    this.check.bindTo {
        val currentFragment = fragmentManager.findFragmentByTag(ResolverFragment.LOCATION_RESOLVER_TAG)
        val resolverFragment = if (currentFragment != null) {
            currentFragment as ResolverFragment
        } else {
            ResolverFragment().apply {
                fragmentManager
                    .beginTransaction()
                    .add(this, ResolverFragment.LOCATION_RESOLVER_TAG)
                    .commitNow()
            }
        }

        resolverFragment.checkLocationSettings {
            this.result.consumer.onNext(it)
        }
    }
}