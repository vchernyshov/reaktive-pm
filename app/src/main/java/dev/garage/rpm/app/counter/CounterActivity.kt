package dev.garage.rpm.app.counter

import android.os.Bundle
import dev.garage.rpm.app.R
import dev.garage.rpm.base.PmActivity

import dev.garage.rpm.bindTo
import dev.garage.rpm.bindings.clicks
import kotlinx.android.synthetic.main.activity_counter.*

class CounterActivity : PmActivity<CounterPm>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_counter)
    }

    override fun providePresentationModel() = CounterPm()

    override fun onBindPresentationModel(pm: CounterPm) {

        pm.count.bindTo { counterText.text = it.toString() }
        pm.minusButtonEnabled.bindTo(minusButton::setEnabled)
        pm.plusButtonEnabled.bindTo(plusButton::setEnabled)

        minusButton.clicks().bindTo(pm.minusButtonClicks)
        plusButton.clicks().bindTo(pm.plusButtonClicks)
    }
}