package dev.garage.rpm.lpc.controls

import com.badoo.reaktive.observable.doOnBeforeNext
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.observable.subscribe
import com.badoo.reaktive.single.Single
import dev.garage.rpm.*
import dev.garage.rpm.lpc.handler.PagingControlHandler
import dev.garage.rpm.lpc.paging.*

const val DEFAULT_START_PAGE_VALUE = 1
const val DEFAULT_LIMIT_VALUE = 10

@Suppress("LongParameterList")
class PagingControl<T> internal constructor(
    private val dataConsumer: ((List<T>) -> Unit)?,
    private val dataTransform: ((List<T>) -> Any)?,
    private val pageLoadingVisibleConsumer: ((Boolean) -> Unit)?,
    private val pageErrorVisibleConsumer: ((Boolean) -> Unit)?,
    private val pageErrorConsumer: ((Throwable) -> Unit)?,
    private val pageInActionConsumer: ((Boolean) -> Unit)?,
    private val isEndReachedConsumer: ((Boolean) -> Unit)?,
    private val scrollToTopConsumer: ((Unit) -> Unit)?,
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
    page: Int,
    limit: Int,
    pageSource: ((page: Int, limit: Int, offset: Int, lastPage: Paging.Page<T>?) -> Single<Paging.Page<T>>)
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
), PagingControlHandler<T> {
    override val errorChanges = state { paging.errorChanges() }
    override val refreshErrorChanges = state { paging.refreshErrorChanges() }

    override val loadingChanges = state { paging.loadingChanges() }

    override val isLoading = state { paging.isLoading() }

    override val isRefreshing = state { paging.isRefreshing() }
    override val refreshEnabled = state { paging.refreshEnabled() }

    override val contentViewVisible = state { paging.contentVisible() }
    override val emptyViewVisible = state { paging.emptyVisible() }
    override val errorViewVisible = state { paging.errorVisible() }

    val contentChanges = state { paging.contentChanges() }
    val transformedData = state<Any?>(null)

    val pageInAction = state { paging.pageInAction() }
    val pageLoadingVisible = state { paging.pageLoadingVisible() }
    val pageErrorVisible = state { paging.pagingErrorVisible() }
    val pageError = state { paging.pagingErrorChanges() }

    val isEndReached = state { paging.isEndReached() }

    val scrollToTop = command<Unit>()

    val loadAction = action<Unit> {
        this.map { Paging.Action.REFRESH }
            .doOnBeforeNext { paging.actions.accept(it) }
    }

    val forceLoadAction = action<Unit> {
        this.map { Paging.Action.FORCE_REFRESH }
            .doOnBeforeNext { paging.actions.accept(it) }
    }

    val loadNextPageAction = action<Unit> {
        this.map { Paging.Action.LOAD_NEXT_PAGE }
            .doOnBeforeNext { paging.actions.accept(it) }
    }

    val retryLoadNextPageAction = action<Unit> {
        this.map { Paging.Action.RETRY_LOAD_NEXT_PAGE }
            .doOnBeforeNext { paging.actions.accept(it) }
    }

    private val paging = PagingImpl(page, limit, pageSource = pageSource)

    override fun onCreate() {
        super.onCreate()
        addDataHandler(dataConsumer, dataTransform)
        addPageLoadingVisibleHandler(pageLoadingVisibleConsumer)
        addPageErrorVisibleHandler(pageErrorVisibleConsumer)
        addPageErrorHandler(pageErrorConsumer)
        addPageInActionHandler(pageInActionConsumer)
        addIsEndReachedHandler(isEndReachedConsumer)
        addScrollToTopHandler(scrollToTopConsumer)
    }

    override fun addDataHandler(
        dataConsumer: ((List<T>) -> Unit)?,
        dataTransform: ((List<T>) -> Any)?
    ) {
        if (dataConsumer != null || dataTransform != null) {
            contentChanges.observable.subscribe { data ->
                dataConsumer?.let { it.invoke(data) }
                dataTransform?.let { transformedData.accept(it.invoke(data)) }
            }.untilDestroy()
        }
    }

    override fun addPageLoadingVisibleHandler(pageLoadingVisibleConsumer: ((Boolean) -> Unit)?) {
        pageLoadingVisibleConsumer?.let { consumer ->
            pageLoadingVisible.observable.subscribe {
                consumer.invoke(it)
            }.untilDestroy()
        }
    }

    override fun addPageErrorVisibleHandler(pageErrorVisibleConsumer: ((Boolean) -> Unit)?) {
        pageErrorVisibleConsumer?.let { consumer ->
            pageErrorVisible.observable.subscribe {
                consumer.invoke(it)
            }.untilDestroy()
        }
    }

    override fun addPageErrorHandler(pageErrorConsumer: ((Throwable) -> Unit)?) {
        pageErrorConsumer?.let { consumer ->
            pageError.observable.subscribe {
                consumer.invoke(it)
            }.untilDestroy()
        }
    }

    override fun addPageInActionHandler(pageInActionConsumer: ((Boolean) -> Unit)?) {
        pageInActionConsumer?.let { consumer ->
            pageInAction.observable.subscribe {
                consumer.invoke(it)
            }.untilDestroy()
        }
    }

    override fun addIsEndReachedHandler(isEndReachedConsumer: ((Boolean) -> Unit)?) {
        isEndReachedConsumer?.let { consumer ->
            isEndReached.observable.subscribe {
                consumer.invoke(it)
            }.untilDestroy()
        }
    }

    override fun addScrollToTopHandler(scrollToTopConsumer: ((Unit) -> Unit)?) {
        paging.scrollToTop().subscribe {
            scrollToTopConsumer?.let { it.invoke(Unit) }
            scrollToTop.accept(Unit)
        }.untilDestroy()
    }

    internal fun checkDataTransformAvailable() = dataTransform != null
}

@Suppress("LongParameterList")
fun <T> PresentationModel.pagingControl(
    dataConsumer: ((List<T>) -> Unit)? = null,
    dataTransform: ((List<T>) -> Any)? = null,
    pageLoadingVisibleConsumer: ((Boolean) -> Unit)? = null,
    pageErrorVisibleConsumer: ((Boolean) -> Unit)? = null,
    pageErrorConsumer: ((Throwable) -> Unit)? = null,
    pageInActionConsumer: ((Boolean) -> Unit)? = null,
    isEndReachedConsumer: ((Boolean) -> Unit)? = null,
    scrollToTopConsumer: ((Unit) -> Unit)? = null,
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
    page: Int = DEFAULT_START_PAGE_VALUE,
    limit: Int = DEFAULT_LIMIT_VALUE,
    pageSource: ((page: Int, limit: Int, offset: Int, lastPage: Paging.Page<T>?) -> Single<Paging.Page<T>>)
): PagingControl<T> {
    return PagingControl(
        dataConsumer,
        dataTransform,
        pageLoadingVisibleConsumer,
        pageErrorVisibleConsumer,
        pageErrorConsumer,
        pageInActionConsumer,
        isEndReachedConsumer,
        scrollToTopConsumer,
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
        page,
        limit,
        pageSource
    ).apply {
        attachToParent(this@pagingControl)
    }
}
