package dev.garage.rpm.pc.settings

import dev.garage.rpm.DiffByEquals
import dev.garage.rpm.DiffStrategy
import dev.garage.rpm.base.lpc.settings.BaseLoadingAndPagingControlSettings

open class PagingControlSettings<T>(
    val contentChangesDiffStrategy: DiffStrategy<List<T>>? = DiffByEquals as DiffStrategy<List<T>>,
    val transformedDataDiffStrategy: DiffStrategy<Any?>? = DiffByEquals as DiffStrategy<Any?>,
    val pageInActionDiffStrategy: DiffStrategy<Boolean>? = DiffByEquals as DiffStrategy<Boolean>,
    val pageLoadingVisibleDiffStrategy: DiffStrategy<Boolean>? = DiffByEquals as DiffStrategy<Boolean>,
    val pageErrorVisibleDiffStrategy: DiffStrategy<Boolean>? = DiffByEquals as DiffStrategy<Boolean>,
    val pageErrorDiffStrategy: DiffStrategy<Throwable>? = DiffByEquals as DiffStrategy<Throwable>,
    val isEndReachedDiffStrategy: DiffStrategy<Boolean>? = DiffByEquals as DiffStrategy<Boolean>,
    errorChangesDiffStrategy: DiffStrategy<Throwable>? = DiffByEquals as DiffStrategy<Throwable>,
    refreshErrorChangesDiffStrategy: DiffStrategy<Throwable>? = DiffByEquals as DiffStrategy<Throwable>,
    loadingChangesDiffStrategy: DiffStrategy<Boolean>? = DiffByEquals as DiffStrategy<Boolean>,
    isLoadingDiffStrategy: DiffStrategy<Boolean>? = DiffByEquals as DiffStrategy<Boolean>,
    isRefreshingDiffStrategy: DiffStrategy<Boolean>? = DiffByEquals as DiffStrategy<Boolean>,
    refreshEnabledDiffStrategy: DiffStrategy<Boolean>? = DiffByEquals as DiffStrategy<Boolean>,
    contentViewVisibleDiffStrategy: DiffStrategy<Boolean>? = DiffByEquals as DiffStrategy<Boolean>,
    emptyViewVisibleDiffStrategy: DiffStrategy<Boolean>? = DiffByEquals as DiffStrategy<Boolean>,
    errorViewVisibleDiffStrategy: DiffStrategy<Boolean>? = DiffByEquals as DiffStrategy<Boolean>,
    transformedErrorDiffStrategy: DiffStrategy<Any?>? = DiffByEquals as DiffStrategy<Any?>,
    transformedRefreshErrorDiffStrategy: DiffStrategy<Any?>? = DiffByEquals as DiffStrategy<Any?>
) : BaseLoadingAndPagingControlSettings(
    errorChangesDiffStrategy = errorChangesDiffStrategy,
    refreshErrorChangesDiffStrategy = refreshErrorChangesDiffStrategy,
    loadingChangesDiffStrategy = loadingChangesDiffStrategy,
    isLoadingDiffStrategy = isLoadingDiffStrategy,
    isRefreshingDiffStrategy = isRefreshingDiffStrategy,
    refreshEnabledDiffStrategy = refreshEnabledDiffStrategy,
    contentViewVisibleDiffStrategy = contentViewVisibleDiffStrategy,
    emptyViewVisibleDiffStrategy = emptyViewVisibleDiffStrategy,
    errorViewVisibleDiffStrategy = errorViewVisibleDiffStrategy,
    transformedErrorDiffStrategy = transformedErrorDiffStrategy,
    transformedRefreshErrorDiffStrategy = transformedRefreshErrorDiffStrategy
)
