package dev.garage.rpm.lc.settings

import dev.garage.rpm.DiffByEquals
import dev.garage.rpm.DiffStrategy
import dev.garage.rpm.base.lpc.settings.BaseLoadingAndPagingControlSettings

open class LoadingControlSettings<T>(
    val contentChangesDiffStrategy: DiffStrategy<T>? = DiffByEquals as DiffStrategy<T>,
    val transformedDataDiffStrategy: DiffStrategy<Any?>? = DiffByEquals as DiffStrategy<Any?>,
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
