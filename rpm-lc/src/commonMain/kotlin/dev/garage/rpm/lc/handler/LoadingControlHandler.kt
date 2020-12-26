package dev.garage.rpm.lc.handler

import dev.garage.rpm.base.lpc.handler.BaseLoadingAndPagingControlHandler
import dev.garage.rpm.lc.controls.DataConsumer
import dev.garage.rpm.lc.controls.DataTransform
import dev.garage.rpm.lc.loading.LoadingImpl

interface LoadingControlHandler<T> : BaseLoadingAndPagingControlHandler {

    val loading: LoadingImpl<T>

    fun addDataHandler(dataConsumer: DataConsumer<T>?, dataTransform: DataTransform<T>?)
}
