package dev.garage.rpm.lc.handler

import dev.garage.rpm.base.lpc.handler.BaseLoadingAndPagingControlHandler
import dev.garage.rpm.lc.controls.DataConsumer
import dev.garage.rpm.lc.controls.DataTransform
import dev.garage.rpm.lc.controls.ScrollToTopConsumer

interface LoadingControlHandler<T> : BaseLoadingAndPagingControlHandler {

    fun addDataHandler(dataConsumer: DataConsumer<T>?, dataTransform: DataTransform<T>?)

    fun <V> addScrollToTopHandler(scrollToTopConsumer: ScrollToTopConsumer?)
}
