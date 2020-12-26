package dev.garage.rpm.lc.controls

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.doOnBeforeNext
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.observable.subscribe
import com.badoo.reaktive.single.Single
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.accept
import dev.garage.rpm.action
import dev.garage.rpm.base.lpc.controls.*
import dev.garage.rpm.lc.handler.LoadingControlHandler
import dev.garage.rpm.lc.loading.*
import dev.garage.rpm.lc.settings.LoadingControlSettings
import dev.garage.rpm.state

typealias DataConsumer<T> = (T) -> Unit
typealias DataTransform<T> = (T) -> Any

@Suppress("LongParameterList")
class LoadingControl<T> internal constructor(
    loadingControlSettings: LoadingControlSettings<T>,
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
    loadingControlSettings,
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

    override val loading: LoadingImpl<T> = LoadingImpl(sourceData)

    override val errorChangesObservable = loading.errorChanges()

    override val refreshErrorChangesObservable: Observable<Throwable> =
        loading.refreshErrorChanges()

    override val loadingChangesObservable: Observable<Boolean> = loading.loadingChanges()

    override val isLoadingObservable: Observable<Boolean> = loading.isLoading()

    override val isRefreshingObservable: Observable<Boolean> = loading.isRefreshing()

    override val refreshEnabledObservable: Observable<Boolean> = loading.refreshEnabled()

    override val contentViewVisibleObservable: Observable<Boolean> = loading.contentVisible()

    override val emptyViewVisibleObservable: Observable<Boolean> = loading.emptyVisible()

    override val errorViewVisibleObservable: Observable<Boolean> = loading.errorVisible()

    internal val contentChanges =
        state(diffStrategy = loadingControlSettings.contentChangesDiffStrategy) { loading.contentChanges() }
    internal val transformedData =
        state(null, diffStrategy = loadingControlSettings.transformedDataDiffStrategy)

    val loadAction = action<Unit> {
        this.map { Loading.Action.REFRESH }
            .doOnBeforeNext { loading.actions.accept(it) }
    }

    val forceLoadAction = action<Unit> {
        this.map { Loading.Action.FORCE_REFRESH }
            .doOnBeforeNext { loading.actions.accept(it) }
    }

    override fun onCreate() {
        super.onCreate()
        addDataHandler(dataConsumer, dataTransform)
    }

    override fun addDataHandler(dataConsumer: DataConsumer<T>?, dataTransform: DataTransform<T>?) {
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
    loadingControlSettings: LoadingControlSettings<T> = LoadingControlSettings(),
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
        loadingControlSettings,
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
