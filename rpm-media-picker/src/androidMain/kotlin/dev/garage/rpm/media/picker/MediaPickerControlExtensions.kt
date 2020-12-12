package dev.garage.rpm.media.picker

import androidx.fragment.app.FragmentManager
import dev.garage.rpm.bindTo

fun MediaPickerControl.bindTo(fragmentManager: FragmentManager) {

    this.request.bindTo {
        val currentFragment =
            fragmentManager.findFragmentByTag(ResolverFragment.MEDIA_PICKER_RESOLVER_TAG)
        val resolverFragment = if (currentFragment != null) {
            currentFragment as ResolverFragment
        } else {
            ResolverFragment().apply {
                fragmentManager
                    .beginTransaction()
                    .add(this, ResolverFragment.MEDIA_PICKER_RESOLVER_TAG)
                    .commitNow()
            }
        }

        resolverFragment.pickMedia { this.result.consumer.onNext(it) }
    }
}
