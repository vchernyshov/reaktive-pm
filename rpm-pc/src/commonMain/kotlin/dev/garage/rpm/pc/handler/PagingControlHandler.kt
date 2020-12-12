package dev.garage.rpm.pc.handler

import dev.garage.rpm.base.lpc.handler.BaseLoadingAndPagingControlHandler

interface PagingControlHandler<T> : BaseLoadingAndPagingControlHandler {

    fun addDataHandler(dataConsumer: ((List<T>) -> Unit)?, dataTransform: ((List<T>) -> Any)?)

    fun addPageLoadingVisibleHandler(pageLoadingVisibleConsumer: ((Boolean) -> Unit)?)

    fun addPageErrorVisibleHandler(pageErrorVisibleConsumer: ((Boolean) -> Unit)?)

    fun addPageErrorHandler(pageErrorConsumer: ((Throwable) -> Unit)?)

    fun addPageInActionHandler(pageInActionConsumer: ((Boolean) -> Unit)?)

    fun addIsEndReachedHandler(isEndReachedConsumer: ((Boolean) -> Unit)?)

    fun addScrollToTopHandler(scrollToTopConsumer: ((Unit) -> Unit)?)
}
