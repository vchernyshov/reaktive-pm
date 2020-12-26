package dev.garage.rpm.base.lpc.settings

import dev.garage.rpm.DiffByEquals
import dev.garage.rpm.DiffStrategy

open class BaseLoadingAndPagingControlSettings(
    val errorChangesDiffStrategy: DiffStrategy<Throwable>? = DiffByEquals as DiffStrategy<Throwable>,
    val refreshErrorChangesDiffStrategy: DiffStrategy<Throwable>? = DiffByEquals as DiffStrategy<Throwable>,
    val loadingChangesDiffStrategy: DiffStrategy<Boolean>? = DiffByEquals as DiffStrategy<Boolean>,
    val isLoadingDiffStrategy: DiffStrategy<Boolean>? = DiffByEquals as DiffStrategy<Boolean>,
    val isRefreshingDiffStrategy: DiffStrategy<Boolean>? = DiffByEquals as DiffStrategy<Boolean>,
    val refreshEnabledDiffStrategy: DiffStrategy<Boolean>? = DiffByEquals as DiffStrategy<Boolean>,
    val contentViewVisibleDiffStrategy: DiffStrategy<Boolean>? = DiffByEquals as DiffStrategy<Boolean>,
    val emptyViewVisibleDiffStrategy: DiffStrategy<Boolean>? = DiffByEquals as DiffStrategy<Boolean>,
    val errorViewVisibleDiffStrategy: DiffStrategy<Boolean>? = DiffByEquals as DiffStrategy<Boolean>,
    val transformedErrorDiffStrategy: DiffStrategy<Any?>? = DiffByEquals as DiffStrategy<Any?>,
    val transformedRefreshErrorDiffStrategy: DiffStrategy<Any?>? = DiffByEquals as DiffStrategy<Any?>
)
