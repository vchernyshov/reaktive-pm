package dev.garage.rpm.app.main.ui.country

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.garage.rpm.bindTo
import dev.garage.rpm.app.App
import dev.garage.rpm.app.R
import dev.garage.rpm.app.common.ui.country.ChooseCountryPm
import dev.garage.rpm.app.main.ui.base.Screen
import dev.garage.rpm.app.common.ui.country.ChooseCountryPm.Mode
import dev.garage.rpm.app.main.extensions.hideKeyboard
import dev.garage.rpm.app.main.extensions.showKeyboard
import dev.garage.rpm.app.main.extensions.visible
import dev.garage.rpm.bindings.clicks
import dev.garage.rpm.widget.bindTo

class ChooseCountryScreen : Screen<ChooseCountryPm>() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbarTitle: TextView
    private lateinit var searchQueryEdit: EditText
    private lateinit var searchButton: View
    private lateinit var clearButton: View
    private lateinit var navButton: View

    private val countriesAdapter = CountriesAdapter(null) { country ->
        presentationModel.countryClicks.consumer.onNext(country)
    }

    override val screenLayout = R.layout.screen_choose_country

    override fun providePresentationModel() =
        ChooseCountryPm(App.component.phoneUtil)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerView)
        toolbarTitle = view.findViewById(R.id.toolbarTitle)
        searchQueryEdit = view.findViewById(R.id.searchQueryEdit)
        searchButton = view.findViewById(R.id.searchButton)
        clearButton = view.findViewById(R.id.clearButton)
        navButton = view.findViewById(R.id.navButton)
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
