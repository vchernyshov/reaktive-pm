package dev.garage.rpm.pc.handler

import dev.garage.rpm.base.lpc.handler.BaseLoadingAndPagingControlHandler
import dev.garage.rpm.pc.controls.*

interface PagingControlHandler<T> : BaseLoadingAndPagingControlHandler {

    fun addDataHandler(dataConsumer: DataConsumer<T>?, dataTransform: DataTransform<T>?)

    fun addPageLoadingVisibleHandler(pageLoadingVisibleConsumer: PageLoadingVisibleConsumer?)

    fun addPageErrorVisibleHandler(pageErrorVisibleConsumer: PageErrorVisibleConsumer?)

    fun addPageErrorHandler(pageErrorConsumer: PageErrorConsumer?)

    fun addPageInActionHandler(pageInActionConsumer: PageInActionConsumer?)

    fun addIsEndReachedHandler(isEndReachedConsumer: IsEndReachedConsumer?)

    fun addScrollToTopHandler(scrollToTopConsumer: ScrollToTopConsumer?)
}
