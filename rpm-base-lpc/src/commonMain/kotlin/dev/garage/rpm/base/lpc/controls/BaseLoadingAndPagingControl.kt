package dev.garage.rpm.base.lpc.controls

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.subscribe
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.State
import dev.garage.rpm.base.lpc.handler.BaseLoadingAndPagingControlHandler
import dev.garage.rpm.base.lpc.settings.BaseLoadingAndPagingControlSettings
import dev.garage.rpm.state

typealias ErrorConsumer = (Throwable) -> Unit
typealias ErrorTransform = (Throwable) -> Any
typealias RefreshErrorConsumer = (Throwable) -> Unit
typealias RefreshErrorTransform = (Throwable) -> Any
typealias LoadingConsumer = (Boolean) -> Unit
typealias IsLoadingConsumer = (Boolean) -> Unit
typealias IsRefreshingConsumer = (Boolean) -> Unit
typealias RefreshEnabledConsumer = (Boolean) -> Unit
typealias ContentVisibleConsumer = (Boolean) -> Unit
typealias ErrorVisibleConsumer = (Boolean) -> Unit
typealias EmptyVisibleConsumer = (Boolean) -> Unit

@Suppress("LongParameterList")
abstract class BaseLoadingAndPagingControl constructor(
    baseLoadingAndPagingControlSettings: BaseLoadingAndPagingControlSettings,
    private val errorConsumer: ErrorConsumer?,
    private val errorTransform: ErrorTransform?,
    private val refreshErrorConsumer: RefreshErrorConsumer?,
    private val refreshErrorTransform: RefreshErrorTransform?,
    private val loadingConsumer: LoadingConsumer?,
    private val isLoadingConsumer: IsLoadingConsumer?,
    private val isRefreshingConsumer: IsRefreshingConsumer?,
    private val refreshEnabledConsumer: RefreshEnabledConsumer?,
    private val contentVisibleConsumer: ContentVisibleConsumer?,
    private val errorVisibleConsumer: ErrorVisibleConsumer?,
    private val emptyVisibleConsumer: EmptyVisibleConsumer?
) : PresentationModel(), BaseLoadingAndPagingControlHandler {

    protected abstract val errorChangesObservable: Observable<Throwable>
    internal val errorChanges =
        state(diffStrategy = baseLoadingAndPagingControlSettings.errorChangesDiffStrategy) { errorChangesObservable }

    protected abstract val refreshErrorChangesObservable: Observable<Throwable>
    internal val refreshErrorChanges: State<Throwable> =
        state(diffStrategy = baseLoadingAndPagingControlSettings.refreshErrorChangesDiffStrategy) { refreshErrorChangesObservable }

    protected abstract val loadingChangesObservable: Observable<Boolean>
    internal val loadingChanges: State<Boolean> =
        state(diffStrategy = baseLoadingAndPagingControlSettings.loadingChangesDiffStrategy) { loadingChangesObservable }

    protected abstract val isLoadingObservable: Observable<Boolean>
    internal val isLoading: State<Boolean> =
        state(diffStrategy = baseLoadingAndPagingControlSettings.isLoadingDiffStrategy) { isLoadingObservable }

    protected abstract val isRefreshingObservable: Observable<Boolean>
    internal val isRefreshing: State<Boolean> =
        state(diffStrategy = baseLoadingAndPagingControlSettings.isRefreshingDiffStrategy) { isRefreshingObservable }

    protected abstract val refreshEnabledObservable: Observable<Boolean>
    internal val refreshEnabled: State<Boolean> =
        state(diffStrategy = baseLoadingAndPagingControlSettings.refreshEnabledDiffStrategy) { refreshEnabledObservable }

    protected abstract val contentViewVisibleObservable: Observable<Boolean>
    internal val contentViewVisible: State<Boolean> =
        state(diffStrategy = baseLoadingAndPagingControlSettings.contentViewVisibleDiffStrategy) { contentViewVisibleObservable }

    protected abstract val emptyViewVisibleObservable: Observable<Boolean>
    internal val emptyViewVisible: State<Boolean> =
        state(diffStrategy = baseLoadingAndPagingControlSettings.emptyViewVisibleDiffStrategy) { emptyViewVisibleObservable }

    protected abstract val errorViewVisibleObservable: Observable<Boolean>
    internal val errorViewVisible: State<Boolean> =
        state(diffStrategy = baseLoadingAndPagingControlSettings.errorViewVisibleDiffStrategy) { errorViewVisibleObservable }

    internal val transformedError = state<Any?>(
        null,
        diffStrategy = baseLoadingAndPagingControlSettings.transformedErrorDiffStrategy
    )
    internal val transformedRefreshError = state<Any?>(
        null,
        diffStrategy = baseLoadingAndPagingControlSettings.transformedRefreshErrorDiffStrategy
    )

    override fun onCreate() {
        super.onCreate()
        addErrorHandler(errorConsumer, errorTransform)
        addRefreshErrorHandler(refreshErrorConsumer, refreshErrorTransform)
        addLoadingHandler(loadingConsumer)
        addIsLoadingHandler(isLoadingConsumer)
        addIsRefreshingHandler(isRefreshingConsumer)
        addRefreshEnabledHandler(refreshEnabledConsumer)
        addContentVisibleHandler(contentVisibleConsumer)
        addErrorVisibleHandler(errorVisibleConsumer)
        addEmptyVisibleHandler(emptyVisibleConsumer)
    }

    override fun addErrorHandler(
        errorConsumer: ErrorConsumer?,
        errorTransform: ErrorTransform?
    ) {
        if (errorConsumer != null || errorTransform != null) {
            errorChanges.observable.subscribe { throwable ->
                errorConsumer?.let { it.invoke(throwable) }
                errorTransform?.let { transformedError.accept(it.invoke(throwable)) }
            }.untilDestroy()
        }
    }

    override fun addRefreshErrorHandler(
        refreshErrorConsumer: RefreshErrorConsumer?,
        refreshErrorTransform: RefreshErrorTransform?
    ) {
        if (refreshErrorConsumer != null || refreshErrorTransform != null) {
            refreshErrorChanges.observable.subscribe { throwable ->
                refreshErrorConsumer?.let { it.invoke(throwable) }
                refreshErrorTransform?.let { transformedRefreshError.accept(it.invoke(throwable)) }
            }.untilDestroy()
        }
    }

    override fun addLoadingHandler(loadingConsumer: LoadingConsumer?) {
        loadingConsumer?.let { consumer ->
            loadingChanges.observable.subscribe {
                consumer.invoke(it)
            }.untilDestroy()
        }
    }

    override fun addIsLoadingHandler(isLoadingConsumer: IsLoadingConsumer?) {
        isLoadingConsumer?.let { consumer ->
            isLoading.observable.subscribe {
                consumer.invoke(it)
            }.untilDestroy()
        }
    }

    override fun addIsRefreshingHandler(isRefreshingConsumer: IsRefreshingConsumer?) {
        isRefreshingConsumer?.let { consumer ->
            isRefreshing.observable.subscribe {
                consumer.invoke(it)
            }.untilDestroy()
        }
    }

    override fun addRefreshEnabledHandler(refreshEnabledConsumer: RefreshEnabledConsumer?) {
        refreshEnabledConsumer?.let { consumer ->
            refreshEnabled.observable.subscribe {
                consumer.invoke(it)
            }.untilDestroy()
        }
    }

    override fun addContentVisibleHandler(contentVisibleConsumer: ContentVisibleConsumer?) {
        contentVisibleConsumer?.let { consumer ->
            contentViewVisible.observable.subscribe {
                consumer.invoke(it)
            }.untilDestroy()
        }
    }

    override fun addErrorVisibleHandler(errorVisibleConsumer: ErrorVisibleConsumer?) {
        errorVisibleConsumer?.let { consumer ->
            errorViewVisible.observable.subscribe {
                consumer.invoke(it)
            }.untilDestroy()
        }
    }

    override fun addEmptyVisibleHandler(emptyVisibleConsumer: EmptyVisibleConsumer?) {
        emptyVisibleConsumer?.let { consumer ->
            emptyViewVisible.observable.subscribe {
                consumer.invoke(it)
            }.untilDestroy()
        }
    }

    internal fun checkErrorTransformAvailable() = errorTransform != null
    internal fun checkRefreshErrorTransformAvailable() = refreshErrorTransform != null
}
