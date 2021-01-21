package dev.garage.rpm.app

import android.app.Application
import android.os.StrictMode
import dev.garage.rpm.BuildConfig
import dev.garage.rpm.app.main.MainComponent
import timber.log.Timber

class App : Application() {

    companion object {
        lateinit var component: MainComponent
            private set
    }

    override fun onCreate() {
        super.onCreate()
        component = MainComponent(this)
        enableStrictMode()
        initLogger()
    }

    private fun enableStrictMode() {
        val builder: StrictMode.VmPolicy.Builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
    }


    private fun initLogger() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
