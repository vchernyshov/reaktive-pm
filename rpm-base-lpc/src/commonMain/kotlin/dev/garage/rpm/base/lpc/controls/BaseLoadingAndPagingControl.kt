package dev.garage.rpm.base.lpc.controls

import com.badoo.reaktive.observable.subscribe
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.State
import dev.garage.rpm.base.lpc.handler.BaseLoadingAndPagingControlHandler
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

    abstract val errorChanges: State<Throwable>
    abstract val refreshErrorChanges: State<Throwable>

    abstract val loadingChanges: State<Boolean>

    abstract val isLoading: State<Boolean>

    abstract val isRefreshing: State<Boolean>
    abstract val refreshEnabled: State<Boolean>

    abstract val contentViewVisible: State<Boolean>
    abstract val emptyViewVisible: State<Boolean>
    abstract val errorViewVisible: State<Boolean>

    val transformedError = state<Any?>(null)
    val transformedRefreshError = state<Any?>(null)

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
        errorConsumer: ((Throwable) -> Unit)?,
        errorTransform: ((Throwable) -> Any)?
    ) {
        if (errorConsumer != null || errorTransform != null) {
            errorChanges.observable.subscribe { throwable ->
                errorConsumer?.let { it.invoke(throwable) }
                errorTransform?.let { transformedError.accept(it.invoke(throwable)) }
            }.untilDestroy()
        }
    }

    override fun addRefreshErrorHandler(
        refreshErrorConsumer: ((Throwable) -> Unit)?,
        refreshErrorTransform: ((Throwable) -> Any)?
    ) {
        if (refreshErrorConsumer != null || refreshErrorTransform != null) {
            refreshErrorChanges.observable.subscribe { throwable ->
                refreshErrorConsumer?.let { it.invoke(throwable) }
                refreshErrorTransform?.let { transformedRefreshError.accept(it.invoke(throwable)) }
            }.untilDestroy()
        }
    }

    override fun addLoadingHandler(loadingConsumer: ((Boolean) -> Unit)?) {
        loadingConsumer?.let { consumer ->
            loadingChanges.observable.subscribe {
                consumer.invoke(it)
            }.untilDestroy()
        }
    }

    override fun addIsLoadingHandler(isLoadingConsumer: ((Boolean) -> Unit)?) {
        isLoadingConsumer?.let { consumer ->
            isLoading.observable.subscribe {
                consumer.invoke(it)
            }.untilDestroy()
        }
    }

    override fun addIsRefreshingHandler(isRefreshingConsumer: ((Boolean) -> Unit)?) {
        isRefreshingConsumer?.let { consumer ->
            isRefreshing.observable.subscribe {
                consumer.invoke(it)
            }.untilDestroy()
        }
    }

    override fun addRefreshEnabledHandler(refreshEnabledConsumer: ((Boolean) -> Unit)?) {
        refreshEnabledConsumer?.let { consumer ->
            refreshEnabled.observable.subscribe {
                consumer.invoke(it)
            }.untilDestroy()
        }
    }

    override fun addContentVisibleHandler(contentVisibleConsumer: ((Boolean) -> Unit)?) {
        contentVisibleConsumer?.let { consumer ->
            contentViewVisible.observable.subscribe {
                consumer.invoke(it)
            }.untilDestroy()
        }
    }

    override fun addErrorVisibleHandler(errorVisibleConsumer: ((Boolean) -> Unit)?) {
        errorVisibleConsumer?.let { consumer ->
            errorViewVisible.observable.subscribe {
                consumer.invoke(it)
            }.untilDestroy()
        }
    }

    override fun addEmptyVisibleHandler(emptyVisibleConsumer: ((Boolean) -> Unit)?) {
        emptyVisibleConsumer?.let { consumer ->
            emptyViewVisible.observable.subscribe {
                consumer.invoke(it)
            }.untilDestroy()
        }
    }

    internal fun checkErrorTransformAvailable() = errorTransform != null
    internal fun checkRefreshErrorTransformAvailable() = refreshErrorTransform != null
}
