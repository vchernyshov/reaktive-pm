package dev.garage.rpm.lpc.handler

interface LoadingControlHandler<T> : BaseLoadingAndPagingControlHandler {

    fun addDataHandler(dataConsumer: ((T) -> Unit)?, dataTransform: ((T) -> Any)?)
}
