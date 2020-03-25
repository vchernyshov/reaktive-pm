package dev.garage.rpm.app.main.ui.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.badoo.reaktive.observable.filter
import com.badoo.reaktive.observable.map
import dev.garage.rpm.app.App
import dev.garage.rpm.app.R
import dev.garage.rpm.app.common.ui.main.MainPm
import dev.garage.rpm.app.main.ui.base.Screen
import dev.garage.rpm.app.common.ui.main.MainPm.DialogResult.Cancel
import dev.garage.rpm.app.common.ui.main.MainPm.DialogResult.Ok
import dev.garage.rpm.bindTo
import dev.garage.rpm.bindings.itemClicks
import dev.garage.rpm.widget.bindTo

class MainScreen : Screen<MainPm>() {

    private lateinit var toolbar: Toolbar

    override val screenLayout = R.layout.screen_main

    override fun providePresentationModel() =
        MainPm(App.component.authModel)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar = view.findViewById(R.id.toolbar)
        toolbar.inflateMenu(R.menu.main)
    }

    override fun onBindPresentationModel(pm: MainPm) {
        super.onBindPresentationModel(pm)

        pm.logoutDialog.bindTo { _, dc ->
            AlertDialog.Builder(context!!)
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("ok") { _, _ -> dc.sendResult(Ok) }
                .setNegativeButton("cancel") { _, _ -> dc.sendResult(Cancel) }
                .create()
        }

        pm.inProgress.bindTo(progressConsumer)

        toolbar.itemClicks()
            .filter { it.itemId == R.id.logoutAction }
            .map { Unit }
            .bindTo(pm.logoutClicks)
    }

}