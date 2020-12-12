package dev.garage.rpm.lc.handler

import dev.garage.rpm.base.lpc.handler.BaseLoadingAndPagingControlHandler

interface LoadingControlHandler<T> : BaseLoadingAndPagingControlHandler {

    fun addDataHandler(dataConsumer: ((T) -> Unit)?, dataTransform: ((T) -> Any)?)
}
