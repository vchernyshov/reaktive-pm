package dev.garage.rpm.lc.controls

import com.badoo.reaktive.observable.doOnBeforeNext
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.observable.subscribe
import com.badoo.reaktive.single.Single
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.accept
import dev.garage.rpm.action
import dev.garage.rpm.base.lpc.controls.*
import dev.garage.rpm.lc.handler.LoadingControlHandler
import dev.garage.rpm.lc.loading.Loading
import dev.garage.rpm.lc.loading.LoadingImpl
import dev.garage.rpm.lc.loading.contentChanges
import dev.garage.rpm.lc.loading.contentVisible
import dev.garage.rpm.lc.loading.emptyVisible
import dev.garage.rpm.lc.loading.errorChanges
import dev.garage.rpm.lc.loading.errorVisible
import dev.garage.rpm.lc.loading.isLoading
import dev.garage.rpm.lc.loading.isRefreshing
import dev.garage.rpm.lc.loading.loadingChanges
import dev.garage.rpm.lc.loading.refreshEnabled
import dev.garage.rpm.lc.loading.refreshErrorChanges
import dev.garage.rpm.state

typealias DataConsumer<T> = (T) -> Unit
typealias DataTransform<T> = (T) -> Any

@Suppress("LongParameterList")
class LoadingControl<T> internal constructor(
    private val dataConsumer: DataConsumer<T>?,
    private val dataTransform: DataTransform<T>?,
    errorConsumer: ErrorConsumer?,
    errorTransform: ErrorTransform?,
    refreshErrorConsumer: RefreshErrorConsumer?,
    refreshErrorTransform: RefreshErrorTransform?,
    loadingConsumer: LoadingConsumer?,
    isLoadingConsumer: IsLoadingConsumer?,
    isRefreshingConsumer: IsRefreshingConsumer?,
    refreshEnabledConsumer: RefreshEnabledConsumer?,
    contentVisibleConsumer: ContentVisibleConsumer?,
    errorVisibleConsumer: ErrorVisibleConsumer?,
    emptyVisibleConsumer: EmptyVisibleConsumer?,
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
    dataConsumer: DataConsumer<T>? = null,
    dataTransform: DataTransform<T>? = null,
    errorConsumer: ErrorConsumer? = null,
    errorTransform: ErrorTransform? = null,
    refreshErrorConsumer: RefreshErrorConsumer? = null,
    refreshErrorTransform: RefreshErrorTransform? = null,
    loadingConsumer: LoadingConsumer? = null,
    isLoadingConsumer: IsLoadingConsumer? = null,
    isRefreshingConsumer: IsRefreshingConsumer? = null,
    refreshEnabledConsumer: RefreshEnabledConsumer? = null,
    contentVisibleConsumer: ContentVisibleConsumer? = null,
    errorVisibleConsumer: ErrorVisibleConsumer? = null,
    emptyVisibleConsumer: EmptyVisibleConsumer? = null,
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
