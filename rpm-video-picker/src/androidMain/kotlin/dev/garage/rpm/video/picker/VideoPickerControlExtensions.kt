package dev.garage.rpm.video.picker

import android.content.Context
import androidx.fragment.app.FragmentManager
import dev.garage.rpm.bindTo
import dev.garage.rpm.permissions.bindTo

fun VideoPickerControl.bindTo(context: Context, fragmentManager: FragmentManager) {

    this.galleryPermissionControl.bindTo(context, fragmentManager)

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
