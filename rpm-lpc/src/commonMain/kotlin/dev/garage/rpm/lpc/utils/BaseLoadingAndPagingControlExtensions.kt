package dev.garage.rpm.lpc.utils

import dev.garage.rpm.bindTo
import dev.garage.rpm.lpc.controls.BaseLoadingAndPagingControl
import dev.garage.rpm.lpc.exception.NoTransformedErrorException
import dev.garage.rpm.lpc.exception.NoTransformedRefreshErrorException
import dev.garage.rpm.util.ConsumerWrapper

@Suppress("LongParameterList")
fun BaseLoadingAndPagingControl.bindTo(
    errorWrapper: ConsumerWrapper<Throwable>? = null,
    transformedErrorWrapper: ConsumerWrapper<Any?>? = null,
    refreshErrorWrapper: ConsumerWrapper<Throwable>? = null,
    transformedRefreshErrorWrapper: ConsumerWrapper<Any?>? = null,
    loadingChangeWrapper: ConsumerWrapper<Boolean>? = null,
    isLoadingWrapper: ConsumerWrapper<Boolean>? = null,
    contentVisibleWrapper: ConsumerWrapper<Boolean>? = null,
    errorVisibleWrapper: ConsumerWrapper<Boolean>? = null,
    emptyVisibleWrapper: ConsumerWrapper<Boolean>? = null,
    isRefreshingWrapper: ConsumerWrapper<Boolean>? = null,
    refreshEnabledWrapper: ConsumerWrapper<Boolean>? = null
) {

    contentVisibleWrapper?.let {
        contentViewVisible.bindTo(it)
    }

    errorVisibleWrapper?.let {
        errorViewVisible.bindTo(it)
    }

    emptyVisibleWrapper?.let {
        emptyViewVisible.bindTo(it)
    }

    isLoadingWrapper?.let {
        isLoading.bindTo(it)
    }

    isRefreshingWrapper?.let {
        isRefreshing.bindTo(it)
    }

    refreshEnabledWrapper?.let {
        refreshEnabled.bindTo(it)
    }

    errorWrapper?.let {
        errorChanges.bindTo(it)
    }

    transformedErrorWrapper?.let {
        if (checkErrorTransformAvailable()) {
            transformedError.bindTo(it)
        } else {
            throw NoTransformedErrorException()
        }
    }

    refreshErrorWrapper?.let {
        refreshErrorChanges.bindTo(it)
    }

    transformedRefreshErrorWrapper?.let {
        if (checkRefreshErrorTransformAvailable()) {
            transformedRefreshError.bindTo(it)
        } else {
            throw NoTransformedRefreshErrorException()
        }
    }

    loadingChangeWrapper?.let {
        loadingChanges.bindTo(it)
    }
}
