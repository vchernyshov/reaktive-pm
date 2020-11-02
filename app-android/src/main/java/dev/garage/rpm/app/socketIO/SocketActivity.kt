package dev.garage.rpm.app.socketIO

import android.os.Bundle
import dev.garage.rpm.app.common.socketIO.SocketPm
import dev.garage.rpm.app.databinding.ActivitySocketIoBinding
import dev.garage.rpm.base.PmActivity
import dev.garage.rpm.bindTo
import dev.garage.rpm.bindings.clicks

class SocketActivity : PmActivity<SocketPm>() {

    private lateinit var binding: ActivitySocketIoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySocketIoBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun providePresentationModel(): SocketPm = SocketPm()

    override fun onBindPresentationModel(pm: SocketPm) {
        with(binding) {
            pm.updateStatusCommand.bindTo { log.text = it }
            connect.clicks().bindTo(pm.connectAction)
            disconnect.clicks().bindTo(pm.disconnectAction)
            login.clicks().bindTo(pm.login)
        }
    }
}