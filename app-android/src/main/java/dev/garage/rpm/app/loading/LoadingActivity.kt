package dev.garage.rpm.app.loading

import android.os.Bundle
import dev.garage.rpm.app.common.loading.LoadingPm
import dev.garage.rpm.app.databinding.ActivityLoadingBinding
import dev.garage.rpm.app.utils.visibilityView
import dev.garage.rpm.base.PmActivity
import dev.garage.rpm.bindTo
import dev.garage.rpm.bindings.clicks
import dev.garage.rpm.lc.loading.bindTo
import dev.garage.rpm.util.ConsumerWrapper

class LoadingActivity : PmActivity<LoadingPm>() {

    private lateinit var binding: ActivityLoadingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun providePresentationModel(): LoadingPm = LoadingPm()

    override fun onBindPresentationModel(pm: LoadingPm) {
        with(binding) {
            pm.loadingControl.bindTo(
                contentVisibleWrapper = ConsumerWrapper(contentView.visibilityView()),
                emptyVisibleWrapper = ConsumerWrapper(empty.emptyView.visibilityView()),
                errorVisibleWrapper = ConsumerWrapper(error.errorView.visibilityView()),
                isLoadingWrapper = ConsumerWrapper(progress.progressBar.visibilityView()),
                dataWrapper = ConsumerWrapper(inner = {
                    contentView.text = it.text
                })
            )
            error.retryButton.clicks().bindTo(pm.loadingControl.loadAction)
            forceRefreshButton.clicks().bindTo(pm.loadingControl.forceLoadAction)
        }
    }
}
