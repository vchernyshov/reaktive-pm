package dev.garage.rpm.pc.controls

import com.badoo.reaktive.observable.doOnBeforeNext
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.observable.subscribe
import com.badoo.reaktive.single.Single
import dev.garage.rpm.*
import dev.garage.rpm.base.lpc.controls.*
import dev.garage.rpm.pc.handler.PagingControlHandler
import dev.garage.rpm.pc.paging.*

typealias DataConsumer<T> = (List<T>) -> Unit
typealias DataTransform<T> = (List<T>) -> Any
typealias PageLoadingVisibleConsumer = (Boolean) -> Unit
typealias PageErrorVisibleConsumer = (Boolean) -> Unit
typealias PageErrorConsumer = (Throwable) -> Unit
typealias PageInActionConsumer = (Boolean) -> Unit
typealias IsEndReachedConsumer = (Boolean) -> Unit
typealias ScrollToTopConsumer = (Unit) -> Unit

const val DEFAULT_START_PAGE_VALUE = 1
const val DEFAULT_LIMIT_VALUE = 10

@Suppress("LongParameterList")
class PagingControl<T> internal constructor(
    private val dataConsumer: DataConsumer<T>?,
    private val dataTransform: DataTransform<T>?,
    private val pageLoadingVisibleConsumer: PageLoadingVisibleConsumer?,
    private val pageErrorVisibleConsumer: PageErrorVisibleConsumer?,
    private val pageErrorConsumer: PageErrorConsumer?,
    private val pageInActionConsumer: PageInActionConsumer?,
    private val isEndReachedConsumer: IsEndReachedConsumer?,
    private val scrollToTopConsumer: ScrollToTopConsumer?,
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
    dataConsumer: DataConsumer<T>? = null,
    dataTransform: DataTransform<T>? = null,
    pageLoadingVisibleConsumer: PageLoadingVisibleConsumer? = null,
    pageErrorVisibleConsumer: PageErrorVisibleConsumer? = null,
    pageErrorConsumer: PageErrorConsumer? = null,
    pageInActionConsumer: PageInActionConsumer? = null,
    isEndReachedConsumer: IsEndReachedConsumer? = null,
    scrollToTopConsumer: ScrollToTopConsumer? = null,
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
