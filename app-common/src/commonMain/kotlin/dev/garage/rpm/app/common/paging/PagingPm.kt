/*
 * Copyright (c) 2019 MobileUp
 */

package dev.garage.rpm.app.common.paging

import com.badoo.reaktive.observable.doOnBeforeNext
import com.badoo.reaktive.observable.filter
import com.badoo.reaktive.observable.take
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.map
import com.badoo.reaktive.single.singleFromFunction
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.action
import dev.garage.rpm.delay
import dev.garage.rpm.pc.controls.pagingControl
import dev.garage.rpm.pc.paging.Paging
import dev.garage.rpm.state

class PagingPm : PresentationModel() {

    private val totalCount: Int = 100

    val mode = state(Mode.NORMAL)

    val pagingControl = pagingControl<Item>(
        limit = 20,
        pageSource = { _, limit, offset, lastPage ->
            loadPage(limit = limit, last = lastPage?.lastItem)
                .map {
                    PageInfo(
                        items = it.list,
                        isEndReached = (offset + it.list.size) == it.totalCount
                    )
                }
        },
        errorTransform = { it.message ?: "Unknown error" }
    )

    private val onCreate = action<Unit> {
        lifecycleObservable
            .filter { it == Lifecycle.CREATED }
            .take(1)
            .doOnBeforeNext { pagingControl.forceLoadAction.accept(Unit) }
    }

    private fun loadPage(limit: Int, last: Item?): Single<ItemsPage> {
        return singleFromFunction { testData(limit, last) }.delay(3000)
    }

    private fun testData(limit: Int, last: Item?): ItemsPage = when (mode.value) {
        Mode.ERROR -> throw NullPointerException()
        Mode.EMPTY_DATA -> ItemsPage(listOf(), totalCount)
        Mode.NORMAL -> {
            ItemsPage(List(limit) { index ->
                Item(number = (last?.number ?: 0) + index + 1)
            }.filter { it.number <= totalCount }, totalCount)
        }
    }

    enum class Mode { NORMAL, ERROR, EMPTY_DATA }
}

data class Item(val number: Int)

data class ItemsPage(val list: List<Item>, val totalCount: Int)

class PageInfo(
    override val items: List<Item>,
    override val isEndReached: Boolean
) : Paging.Page<Item>