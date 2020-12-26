package dev.garage.rpm.app.paging

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dev.garage.rpm.accept
import dev.garage.rpm.app.R
import dev.garage.rpm.app.common.paging.PagingPm
import dev.garage.rpm.app.databinding.ActivityPagingBinding
import dev.garage.rpm.app.utils.refreshes
import dev.garage.rpm.app.utils.visibilityView
import dev.garage.rpm.base.PmActivity
import dev.garage.rpm.bindTo
import dev.garage.rpm.bindings.clicks
import dev.garage.rpm.pc.paging.bindTo
import dev.garage.rpm.util.ConsumerWrapper
import kotlinx.android.synthetic.main.include_footer_paging.view.*

class PagingActivity : PmActivity<PagingPm>() {

    private lateinit var binding: ActivityPagingBinding

    private lateinit var itemsAdapter: ItemsAdapter

    private lateinit var scrollListener: EndlessRecyclerViewScrollListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPagingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val footer =
            LayoutInflater.from(this)
                .inflate(R.layout.include_footer_paging, binding.swipeRefreshLayout, false)

        itemsAdapter = ItemsAdapter(footer)

        scrollListener = EndlessRecyclerViewScrollListener(
            presentationModel.pagingControl.loadNextPageAction.consumer::accept
        )

        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = itemsAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            addOnScrollListener(scrollListener)
        }
    }

    override fun providePresentationModel(): PagingPm = PagingPm()

    override fun onBindPresentationModel(pm: PagingPm) {
        with(binding) {
            pm.pagingControl.bindTo(
                contentVisibleWrapper = ConsumerWrapper(recyclerView.visibilityView()),
                emptyVisibleWrapper = ConsumerWrapper(empty.emptyView.visibilityView()),
                errorVisibleWrapper = ConsumerWrapper(error.errorView.visibilityView()),
                dataWrapper = ConsumerWrapper(inner = {
                    itemsAdapter.submitList(it)
                }),
                isLoadingWrapper = ConsumerWrapper(progress.progressBar.visibilityView()),
                isRefreshingWrapper = ConsumerWrapper(inner = {
                    swipeRefreshLayout.isRefreshing = it
                }),
                refreshEnabledWrapper = ConsumerWrapper(inner = {
                    swipeRefreshLayout.isEnabled = it
                }),
                pageInActionWrapper = ConsumerWrapper(inner = {
                    scrollListener.setLoadMore(!it)
                }),
                pageLoadingVisibleWrapper = ConsumerWrapper(itemsAdapter.footerView.progressBar.visibilityView()),
                pageErrorVisibleWrapper = ConsumerWrapper(inner = {
                    if (it) {
                        itemsAdapter.footerView.pageLoadingErrorText.visibility = View.VISIBLE
                        itemsAdapter.footerView.retryButton.visibility = View.VISIBLE
                    } else {
                        itemsAdapter.footerView.pageLoadingErrorText.visibility = View.GONE
                        itemsAdapter.footerView.retryButton.visibility = View.GONE
                    }
                }),
                transformedErrorWrapper = ConsumerWrapper(inner = {
                    val message = it as String
                    Toast.makeText(this@PagingActivity, message, Toast.LENGTH_SHORT)
                        .show()
                }),
                isEndReachedWrapper = ConsumerWrapper(inner = {
                    if (it) {
                        recyclerView.removeOnScrollListener(scrollListener)
                    }
                }),
                scrollToTopWrapper = ConsumerWrapper(inner = {
                    recyclerView.smoothScrollToPosition(0)
                })
            )
            swipeRefreshLayout.refreshes().bindTo(pm.pagingControl.loadAction)
            error.retryButton.clicks().bindTo(pm.pagingControl.forceLoadAction)
            itemsAdapter.footerView.retryButton.clicks()
                .bindTo(pm.pagingControl.retryLoadNextPageAction)
        }
    }
}
