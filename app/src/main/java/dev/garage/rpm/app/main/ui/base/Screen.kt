package dev.garage.rpm.app.main.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import dev.garage.rpm.accept
import dev.garage.rpm.app.R
import dev.garage.rpm.base.PmFragment
import dev.garage.rpm.app.main.extensions.findScreen
import dev.garage.rpm.app.main.extensions.showDialog
import dev.garage.rpm.widget.bindTo

abstract class Screen<PM : ScreenPresentationModel> : PmFragment<PM>(), BackHandler {

    abstract val screenLayout: Int

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(screenLayout, container, false)
    }

    override fun onBindPresentationModel(pm: PM) {
        pm.errorDialog.bindTo { message, _ ->
            AlertDialog.Builder(context!!)
                .setMessage(message)
                .setPositiveButton(R.string.ok_button, null)
                .create()
        }
    }

    override fun handleBack(): Boolean {
        presentationModel.backAction.consumer.accept(Unit)
        return true
    }

    val progressConsumer: ((Boolean) -> Unit) = {
        if (it) {
            childFragmentManager.showDialog(ProgressDialog())
        } else {
            childFragmentManager
                .findScreen<ProgressDialog>()
                ?.dismiss()
        }
    }
}