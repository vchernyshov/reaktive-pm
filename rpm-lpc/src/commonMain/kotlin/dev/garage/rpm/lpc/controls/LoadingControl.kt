package dev.garage.rpm.lpc.controls

import com.badoo.reaktive.observable.doOnBeforeNext
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.observable.subscribe
import com.badoo.reaktive.single.Single
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.accept
import dev.garage.rpm.action
import dev.garage.rpm.lpc.handler.LoadingControlHandler
import dev.garage.rpm.lpc.loading.Loading
import dev.garage.rpm.lpc.loading.LoadingImpl
import dev.garage.rpm.lpc.loading.contentChanges
import dev.garage.rpm.lpc.loading.contentVisible
import dev.garage.rpm.lpc.loading.emptyVisible
import dev.garage.rpm.lpc.loading.errorChanges
import dev.garage.rpm.lpc.loading.errorVisible
import dev.garage.rpm.lpc.loading.isLoading
import dev.garage.rpm.lpc.loading.isRefreshing
import dev.garage.rpm.lpc.loading.loadingChanges
import dev.garage.rpm.lpc.loading.refreshEnabled
import dev.garage.rpm.lpc.loading.refreshErrorChanges
import dev.garage.rpm.state

@Suppress("LongParameterList")
class LoadingControl<T> internal constructor(
    private val dataConsumer: ((T) -> Unit)?,
    private val dataTransform: ((T) -> Any)?,
    errorConsumer: ((Throwable) -> Unit)?,
    errorTransform: ((Throwable) -> Any)?,
    refreshErrorConsumer: ((Throwable) -> Unit)?,
    refreshErrorTransform: ((Throwable) -> Any)?,
    loadingConsumer: ((Boolean) -> Unit)?,
    isLoadingConsumer: ((Boolean) -> Unit)?,
    isRefreshingConsumer: ((Boolean) -> Unit)?,
    refreshEnabledConsumer: ((Boolean) -> Unit)?,
    contentVisibleConsumer: ((Boolean) -> Unit)?,
    errorVisibleConsumer: ((Boolean) -> Unit)?,
    emptyVisibleConsumer: ((Boolean) -> Unit)?,
    sourceData: Single<T>
) : BaseLoadingAndPagingControl(
    errorConsumer,
    errorTransform,
    refreshErrorConsumer,
    refreshErrorTransform,
    loadingConsumer,
    isLoadingConsumer,
    isRefreshingConsumer,
    refreshEnabledConsumer,
    contentVisibleConsumer,
    errorVisibleConsumer,
    emptyVisibleConsumer
), LoadingControlHandler<T> {
    override val errorChanges = state { loading.errorChanges() }
    override val refreshErrorChanges = state { loading.refreshErrorChanges() }

    override val loadingChanges = state { loading.loadingChanges() }

    override val isLoading = state { loading.isLoading() }

    override val isRefreshing = state { loading.isRefreshing() }
    override val refreshEnabled = state { loading.refreshEnabled() }

    override val contentViewVisible = state { loading.contentVisible() }
    override val emptyViewVisible = state { loading.emptyVisible() }
    override val errorViewVisible = state { loading.errorVisible() }

    val contentChanges = state { loading.contentChanges() }
    val transformedData = state<Any?>(null)

    val loadAction = action<Unit> {
        this.map { Loading.Action.REFRESH }
            .doOnBeforeNext { loading.actions.accept(it) }
    }

    val forceLoadAction = action<Unit> {
        this.map { Loading.Action.FORCE_REFRESH }
            .doOnBeforeNext { loading.actions.accept(it) }
    }

    private val loading =
        LoadingImpl(source = sourceData)

    override fun onCreate() {
        super.onCreate()
        addDataHandler(dataConsumer, dataTransform)
    }

    override fun addDataHandler(dataConsumer: ((T) -> Unit)?, dataTransform: ((T) -> Any)?) {
        if (dataConsumer != null || dataTransform != null) {
            contentChanges.observable.subscribe { data ->
                dataConsumer?.let { it.invoke(data) }
                dataTransform?.let { transformedData.accept(it.invoke(data)) }
            }.untilDestroy()
        }
    }

    internal fun checkDataTransformAvailable() = dataTransform != null
}

@Suppress("LongParameterList")
fun <T> PresentationModel.loadingControl(
    dataConsumer: ((T) -> Unit)? = null,
    dataTransform: ((T) -> Any)? = null,
    errorConsumer: ((Throwable) -> Unit)? = null,
    errorTransform: ((Throwable) -> Any)? = null,
    refreshErrorConsumer: ((Throwable) -> Unit)? = null,
    refreshErrorTransform: ((Throwable) -> Any)? = null,
    loadingConsumer: ((Boolean) -> Unit)? = null,
    isLoadingConsumer: ((Boolean) -> Unit)? = null,
    isRefreshingConsumer: ((Boolean) -> Unit)? = null,
    refreshEnabledConsumer: ((Boolean) -> Unit)? = null,
    contentVisibleConsumer: ((Boolean) -> Unit)? = null,
    errorVisibleConsumer: ((Boolean) -> Unit)? = null,
    emptyVisibleConsumer: ((Boolean) -> Unit)? = null,
    sourceData: Single<T>
): LoadingControl<T> {
    return LoadingControl(
        dataConsumer,
        dataTransform,
        errorConsumer,
        errorTransform,
        refreshErrorConsumer,
        refreshErrorTransform,
        loadingConsumer,
        isLoadingConsumer,
        isRefreshingConsumer,
        refreshEnabledConsumer,
        contentVisibleConsumer,
        errorVisibleConsumer,
        emptyVisibleConsumer,
        sourceData
    ).apply {
        attachToParent(this@loadingControl)
    }
}
