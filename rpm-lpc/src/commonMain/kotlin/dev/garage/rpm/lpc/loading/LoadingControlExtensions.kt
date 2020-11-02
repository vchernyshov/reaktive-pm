package dev.garage.rpm.lpc.loading

import dev.garage.rpm.bindTo
import dev.garage.rpm.lpc.controls.LoadingControl
import dev.garage.rpm.lpc.exception.NoTransformedDataException
import dev.garage.rpm.lpc.utils.bindTo
import dev.garage.rpm.util.ConsumerWrapper

@Suppress("LongParameterList")
fun <T> LoadingControl<T>.bindTo(
    dataWrapper: ConsumerWrapper<T>? = null,
    transformedDataWrapper: ConsumerWrapper<Any?>? = null,
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

    bindTo(
        errorWrapper,
        transformedErrorWrapper,
        refreshErrorWrapper,
        transformedRefreshErrorWrapper,
        loadingChangeWrapper,
        isLoadingWrapper,
        contentVisibleWrapper,
        errorVisibleWrapper,
        emptyVisibleWrapper,
        isRefreshingWrapper,
        refreshEnabledWrapper
    )

    dataWrapper?.let {
        contentChanges.bindTo(it)
    }

    transformedDataWrapper?.let {
        if (checkDataTransformAvailable()) {
            transformedData.bindTo(it)
        } else {
            throw NoTransformedDataException()
        }
    }
}
