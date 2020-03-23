package dev.garage.rpm.app.main.ui.country

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import dev.garage.rpm.bindTo
import dev.garage.rpm.bindings.clicks
import dev.garage.rpm.app.App
import dev.garage.rpm.app.R
import dev.garage.rpm.app.main.extensions.hideKeyboard
import dev.garage.rpm.app.main.extensions.showKeyboard
import dev.garage.rpm.app.main.extensions.visible
import dev.garage.rpm.app.main.ui.base.Screen
import dev.garage.rpm.app.main.ui.country.ChooseCountryPm.Mode
import dev.garage.rpm.widget.bindTo
import kotlinx.android.synthetic.main.screen_choose_country.*

class ChooseCountryScreen : Screen<ChooseCountryPm>() {

    private val countriesAdapter = CountriesAdapter(null) { country ->
        presentationModel.countryClicks.consumer.onNext(country)
    }

    override val screenLayout = R.layout.screen_choose_country

    override fun providePresentationModel() = ChooseCountryPm(App.component.phoneUtil)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = countriesAdapter
        }
    }

    override fun onBindPresentationModel(pm: ChooseCountryPm) {
        super.onBindPresentationModel(pm)

        pm.mode.bindTo {
            if (it == Mode.SEARCH_OPENED) {
                toolbarTitle.visible(false)
                searchQueryEdit.visible(true)
                searchQueryEdit.showKeyboard()
                searchButton.visible(false)
                clearButton.visible(true)
            } else {
                toolbarTitle.visible(true)
                searchQueryEdit.visible(false)
                searchQueryEdit.hideKeyboard()
                searchButton.visible(true)
                clearButton.visible(false)
            }
        }

        pm.searchQueryInput.bindTo(searchQueryEdit)
        pm.countries.bindTo { countriesAdapter.setData(it) }

        searchButton.clicks().bindTo(pm.openSearchClicks)
        clearButton.clicks().bindTo(pm.clearClicks)
        navButton.clicks().bindTo(pm.backAction)
    }
}
