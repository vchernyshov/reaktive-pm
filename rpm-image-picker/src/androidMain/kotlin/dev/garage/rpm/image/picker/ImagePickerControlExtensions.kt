package dev.garage.rpm.image.picker

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import dev.garage.rpm.bindTo

fun ImagePickerControl.bindTo(fragmentManager: FragmentManager) {

    this.request.bindTo { imagePickParams ->
        val currentFragment =
            fragmentManager.findFragmentByTag(ResolverFragment.IMAGE_PICKER_RESOLVER_TAG)
        val resolverFragment = if (currentFragment != null) {
            currentFragment as ResolverFragment
        } else {
            ResolverFragment().apply {
                if (imagePickParams is ImagePickParams.DimensionalImagePickParams) {
                    arguments = Bundle().apply {
                        putInt(ResolverFragment.ARG_IMG_MAX_WIDTH, imagePickParams.maxWidth)
                        putInt(
                            ResolverFragment.ARG_IMG_MAX_HEIGHT,
                            imagePickParams.maxHeight
                        )
                    }
                }
                fragmentManager
                    .beginTransaction()
                    .add(this, ResolverFragment.IMAGE_PICKER_RESOLVER_TAG)
                    .commitNow()
            }
        }

        when (imagePickParams.mediaSource) {
            MediaSource.CAMERA -> resolverFragment.pickCameraImage {
                this.result.consumer.onNext(it)
            }
            MediaSource.GALLERY -> resolverFragment.pickGalleryImage {
                this.result.consumer.onNext(it)
            }
        }
    }
}
