package dev.garage.rpm.lpc.paging

import dev.garage.rpm.bindTo
import dev.garage.rpm.lpc.controls.PagingControl
import dev.garage.rpm.lpc.exception.NoTransformedDataException
import dev.garage.rpm.lpc.utils.bindTo
import dev.garage.rpm.util.ConsumerWrapper

@Suppress("LongParameterList")
fun <T> PagingControl<T>.bindTo(
    dataWrapper: ConsumerWrapper<List<T>>? = null,
    transformedDataWrapper: ConsumerWrapper<Any?>? = null,
    errorWrapper: ConsumerWrapper<Throwable>? = null,
    transformedErrorWrapper: ConsumerWrapper<Any?>? = null,
    refreshErrorWrapper: ConsumerWrapper<Throwable>? = null,
    transformedRefreshErrorWrapper: ConsumerWrapper<Any?>? = null,
    isLoadingWrapper: ConsumerWrapper<Boolean>? = null,
    loadingChangeWrapper: ConsumerWrapper<Boolean>? = null,
    contentVisibleWrapper: ConsumerWrapper<Boolean>? = null,
    errorVisibleWrapper: ConsumerWrapper<Boolean>? = null,
    emptyVisibleWrapper: ConsumerWrapper<Boolean>? = null,
    isRefreshingWrapper: ConsumerWrapper<Boolean>? = null,
    refreshEnabledWrapper: ConsumerWrapper<Boolean>? = null,
    pageInActionWrapper: ConsumerWrapper<Boolean>? = null,
    pageLoadingVisibleWrapper: ConsumerWrapper<Boolean>? = null,
    pageErrorVisibleWrapper: ConsumerWrapper<Boolean>? = null,
    pageErrorWrapper: ConsumerWrapper<Throwable>? = null,
    isEndReachedWrapper: ConsumerWrapper<Boolean>? = null,
    scrollToTopWrapper: ConsumerWrapper<Unit>? = null
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

    pageInActionWrapper?.let {
        pageInAction.bindTo(it)
    }

    pageLoadingVisibleWrapper?.let {
        pageLoadingVisible.bindTo(it)
    }

    pageErrorVisibleWrapper?.let {
        pageErrorVisible.bindTo(it)
    }

    pageErrorWrapper?.let {
        pageError.bindTo(it)
    }

    isEndReachedWrapper?.let {
        isEndReached.bindTo(it)
    }

    scrollToTopWrapper?.let {
        scrollToTop.bindTo(it)
    }
}
