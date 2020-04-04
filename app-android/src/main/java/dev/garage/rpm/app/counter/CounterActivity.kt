package dev.garage.rpm.app.counter

import android.os.Bundle
import android.view.View
import android.widget.TextView
import dev.garage.rpm.app.R
import dev.garage.rpm.app.common.counter.CounterPm
import dev.garage.rpm.base.PmActivity
import dev.garage.rpm.bindTo
import dev.garage.rpm.bindings.clicks

class CounterActivity : PmActivity<CounterPm>() {

    private lateinit var counterText: TextView
    private lateinit var minusButton: View
    private lateinit var plusButton: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_counter)

        counterText = findViewById(R.id.counterText)
        minusButton = findViewById(R.id.minusButton)
        plusButton = findViewById(R.id.plusButton)
    }

    override fun providePresentationModel() =
        CounterPm()

    override fun onBindPresentationModel(pm: CounterPm) {

        pm.count.bindTo(counterText::setText)
        pm.minusButtonEnabled.bindTo(minusButton::setEnabled)
        pm.plusButtonEnabled.bindTo(plusButton::setEnabled)

        minusButton.clicks().bindTo(pm.minusButtonClicks)
        plusButton.clicks().bindTo(pm.plusButtonClicks)
    }
}