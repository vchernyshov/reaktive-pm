package dev.garage.rpm.lpc.controls

import com.badoo.reaktive.observable.subscribe
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.State
import dev.garage.rpm.lpc.handler.BaseLoadingAndPagingControlHandler
import dev.garage.rpm.state

@Suppress("LongParameterList")
abstract class BaseLoadingAndPagingControl internal constructor(
    private val errorConsumer: ((Throwable) -> Unit)?,
    private val errorTransform: ((Throwable) -> Any)?,
    private val refreshErrorConsumer: ((Throwable) -> Unit)?,
    private val refreshErrorTransform: ((Throwable) -> Any)?,
    private val loadingConsumer: ((Boolean) -> Unit)?,
    private val isLoadingConsumer: ((Boolean) -> Unit)?,
    private val isRefreshingConsumer: ((Boolean) -> Unit)?,
    private val refreshEnabledConsumer: ((Boolean) -> Unit)?,
    private val contentVisibleConsumer: ((Boolean) -> Unit)?,
    private val errorVisibleConsumer: ((Boolean) -> Unit)?,
    private val emptyVisibleConsumer: ((Boolean) -> Unit)?
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
