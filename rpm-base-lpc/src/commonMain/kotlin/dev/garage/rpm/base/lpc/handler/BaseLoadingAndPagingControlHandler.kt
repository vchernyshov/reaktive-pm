package dev.garage.rpm.base.lpc.handler

import dev.garage.rpm.base.lpc.controls.*

interface BaseLoadingAndPagingControlHandler {

    fun addErrorHandler(
        errorConsumer: ErrorConsumer?,
        errorTransform: ErrorTransform?
    )

    fun addRefreshErrorHandler(
        refreshErrorConsumer: RefreshErrorConsumer?,
        refreshErrorTransform: RefreshErrorTransform?
    )

    fun addLoadingHandler(loadingConsumer: LoadingConsumer?)

    fun addIsLoadingHandler(isLoadingConsumer: IsLoadingConsumer?)

    fun addIsRefreshingHandler(isRefreshingConsumer: IsRefreshingConsumer?)

    fun addRefreshEnabledHandler(refreshEnabledConsumer: RefreshEnabledConsumer?)

    fun addContentVisibleHandler(contentVisibleConsumer: ContentVisibleConsumer?)

    fun addErrorVisibleHandler(errorVisibleConsumer: ErrorVisibleConsumer?)

    fun addEmptyVisibleHandler(emptyVisibleConsumer: EmptyVisibleConsumer?)
}
