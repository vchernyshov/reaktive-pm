package dev.garage.rpm.lpc.handler

interface BaseLoadingAndPagingControlHandler {

    fun addErrorHandler(
        errorConsumer: ((Throwable) -> Unit)?,
        errorTransform: ((Throwable) -> Any)?
    )

    fun addRefreshErrorHandler(
        refreshErrorConsumer: ((Throwable) -> Unit)?,
        refreshErrorTransform: ((Throwable) -> Any)?
    )

    fun addLoadingHandler(loadingConsumer: ((Boolean) -> Unit)?)

    fun addIsLoadingHandler(isLoadingConsumer: ((Boolean) -> Unit)?)

    fun addIsRefreshingHandler(isRefreshingConsumer: ((Boolean) -> Unit)?)

    fun addRefreshEnabledHandler(refreshEnabledConsumer: ((Boolean) -> Unit)?)

    fun addContentVisibleHandler(contentVisibleConsumer: ((Boolean) -> Unit)?)

    fun addErrorVisibleHandler(errorVisibleConsumer: ((Boolean) -> Unit)?)

    fun addEmptyVisibleHandler(emptyVisibleConsumer: ((Boolean) -> Unit)?)
}
