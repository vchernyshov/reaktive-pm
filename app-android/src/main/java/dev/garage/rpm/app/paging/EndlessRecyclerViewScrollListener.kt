/*
 * Copyright (c) 2019 MobileUp
 */

package dev.garage.rpm.app.paging

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

const val DEFAULT_DIFFERENCE = 5

class EndlessRecyclerViewScrollListener(
    private val onLoadMore: (Unit) -> Unit,
    private val difference: Int = DEFAULT_DIFFERENCE
) : RecyclerView.OnScrollListener() {

    private var loadMore = true

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val limit = recyclerView.layoutManager!!.itemCount
        val lastVisibleItem =
            (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()

        if (dy > 0 && limit - lastVisibleItem < difference && loadMore) {
            loadMore = false
            onLoadMore.invoke(Unit)
        }
    }

    fun setLoadMore(isLoadMore: Boolean) {
        loadMore = isLoadMore
    }
}
