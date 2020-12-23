package dev.garage.rpm.file.picker

import android.content.Context
import androidx.fragment.app.FragmentManager
import dev.garage.rpm.bindTo
import dev.garage.rpm.permissions.bindTo

fun FilePickerControl.bindTo(context: Context, fragmentManager: FragmentManager) {

    this.storagePermissionControl.bindTo(context, fragmentManager)

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
