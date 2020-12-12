package dev.garage.rpm.file.picker

import androidx.fragment.app.FragmentManager
import dev.garage.rpm.bindTo

fun FilePickerControl.bindTo(fragmentManager: FragmentManager) {

    this.request.bindTo {
        val currentFragment =
            fragmentManager.findFragmentByTag(ResolverFragment.FILE_PICKER_RESOLVER_TAG)
        val resolverFragment = if (currentFragment != null) {
            currentFragment as ResolverFragment
        } else {
            ResolverFragment().apply {
                fragmentManager
                    .beginTransaction()
                    .add(this, ResolverFragment.FILE_PICKER_RESOLVER_TAG)
                    .commitNow()
            }
        }

        resolverFragment.pickFile { this.result.consumer.onNext(it) }
    }
}
