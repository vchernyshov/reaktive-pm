package dev.garage.rpm.pc.controls

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.doOnBeforeNext
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.observable.subscribe
import com.badoo.reaktive.single.Single
import dev.garage.rpm.*
import dev.garage.rpm.base.lpc.controls.*
import dev.garage.rpm.pc.handler.PagingControlHandler
import dev.garage.rpm.pc.paging.*
import dev.garage.rpm.pc.settings.PagingControlSettings

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
    pagingControlSettings: PagingControlSettings<T>,
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
    pagingControlSettings,
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

    override val paging = PagingImpl(page, limit, pageSource = pageSource)

    override val errorChangesObservable = paging.errorChanges()

    override val refreshErrorChangesObservable: Observable<Throwable> =
        paging.refreshErrorChanges()

    override val loadingChangesObservable: Observable<Boolean> = paging.loadingChanges()

    override val isLoadingObservable: Observable<Boolean> = paging.isLoading()

    override val isRefreshingObservable: Observable<Boolean> = paging.isRefreshing()

    override val refreshEnabledObservable: Observable<Boolean> = paging.refreshEnabled()

    override val contentViewVisibleObservable: Observable<Boolean> = paging.contentVisible()

    override val emptyViewVisibleObservable: Observable<Boolean> = paging.emptyVisible()

    override val errorViewVisibleObservable: Observable<Boolean> = paging.errorVisible()

    internal val contentChanges =
        state(diffStrategy = pagingControlSettings.contentChangesDiffStrategy) { paging.contentChanges() }
    internal val transformedData =
        state(null, diffStrategy = pagingControlSettings.transformedDataDiffStrategy)

    internal val pageInAction =
        state(diffStrategy = pagingControlSettings.pageInActionDiffStrategy) { paging.pageInAction() }
    internal val pageLoadingVisible =
        state(diffStrategy = pagingControlSettings.pageLoadingVisibleDiffStrategy) { paging.pageLoadingVisible() }
    internal val pageErrorVisible =
        state(diffStrategy = pagingControlSettings.pageErrorVisibleDiffStrategy) { paging.pagingErrorVisible() }
    internal val pageError =
        state(diffStrategy = pagingControlSettings.pageErrorDiffStrategy) { paging.pagingErrorChanges() }

    internal val isEndReached =
        state(diffStrategy = pagingControlSettings.isEndReachedDiffStrategy) { paging.isEndReached() }

    internal val scrollToTop = command<Unit>()

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
        dataConsumer: DataConsumer<T>?,
        dataTransform: DataTransform<T>?
    ) {
        if (dataConsumer != null || dataTransform != null) {
            contentChanges.observable.subscribe { data ->
                dataConsumer?.let { it.invoke(data) }
                dataTransform?.let { transformedData.accept(it.invoke(data)) }
            }.untilDestroy()
        }
    }

    override fun addPageLoadingVisibleHandler(pageLoadingVisibleConsumer: PageLoadingVisibleConsumer?) {
        pageLoadingVisibleConsumer?.let { consumer ->
            pageLoadingVisible.observable.subscribe {
                consumer.invoke(it)
            }.untilDestroy()
        }
    }

    override fun addPageErrorVisibleHandler(pageErrorVisibleConsumer: PageErrorVisibleConsumer?) {
        pageErrorVisibleConsumer?.let { consumer ->
            pageErrorVisible.observable.subscribe {
                consumer.invoke(it)
            }.untilDestroy()
        }
    }

    override fun addPageErrorHandler(pageErrorConsumer: PageErrorConsumer?) {
        pageErrorConsumer?.let { consumer ->
            pageError.observable.subscribe {
                consumer.invoke(it)
            }.untilDestroy()
        }
    }

    override fun addPageInActionHandler(pageInActionConsumer: PageInActionConsumer?) {
        pageInActionConsumer?.let { consumer ->
            pageInAction.observable.subscribe {
                consumer.invoke(it)
            }.untilDestroy()
        }
    }

    override fun addIsEndReachedHandler(isEndReachedConsumer: IsEndReachedConsumer?) {
        isEndReachedConsumer?.let { consumer ->
            isEndReached.observable.subscribe {
                consumer.invoke(it)
            }.untilDestroy()
        }
    }

    override fun addScrollToTopHandler(scrollToTopConsumer: ScrollToTopConsumer?) {
        paging.scrollToTop().subscribe {
            scrollToTopConsumer?.let { it.invoke(Unit) }
            scrollToTop.accept(Unit)
        }.untilDestroy()
    }

    internal fun checkDataTransformAvailable() = dataTransform != null
}

@Suppress("LongParameterList")
fun <T> PresentationModel.pagingControl(
    pagingControlSettings: PagingControlSettings<T> = PagingControlSettings(),
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
        pagingControlSettings,
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
