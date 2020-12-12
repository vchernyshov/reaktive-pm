package dev.garage.rpm.video.picker

import androidx.fragment.app.FragmentManager
import dev.garage.rpm.bindTo

fun VideoPickerControl.bindTo(fragmentManager: FragmentManager) {

    this.request.bindTo {
        val currentFragment =
            fragmentManager.findFragmentByTag(ResolverFragment.VIDEO_PICKER_RESOLVER_TAG)
        val resolverFragment = if (currentFragment != null) {
            currentFragment as ResolverFragment
        } else {
            ResolverFragment().apply {
                fragmentManager
                    .beginTransaction()
                    .add(this, ResolverFragment.VIDEO_PICKER_RESOLVER_TAG)
                    .commitNow()
            }
        }

        resolverFragment.pickVideo { this.result.consumer.onNext(it) }
    }
}
