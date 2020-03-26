package dev.garage.rpm.app.common.ui.country

import com.badoo.reaktive.observable.debounce
import com.badoo.reaktive.observable.doOnBeforeNext
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.scheduler.computationScheduler
import dev.garage.rpm.accept
import dev.garage.rpm.action
import dev.garage.rpm.app.common.main.AppNavigationMessage.CountryChosen
import dev.garage.rpm.app.common.ui.base.ScreenPresentationModel
import dev.garage.rpm.app.common.ui.country.ChooseCountryPm.Mode.SEARCH_CLOSED
import dev.garage.rpm.app.common.ui.country.ChooseCountryPm.Mode.SEARCH_OPENED
import dev.garage.rpm.app.common.main.util.Country
import dev.garage.rpm.app.common.main.util.PhoneUtil
import dev.garage.rpm.state
import dev.garage.rpm.widget.inputControl


class ChooseCountryPm(private val phoneUtil: PhoneUtil) : ScreenPresentationModel() {

    enum class Mode { SEARCH_OPENED, SEARCH_CLOSED }

    val searchQueryInput = inputControl()
    val mode = state(SEARCH_CLOSED)

    val countries = state<List<Country>> {
        searchQueryInput.text.observable
            .debounce(100, computationScheduler)
            .map { query ->
                val regex = "${query.toLowerCase()}.*".toRegex()
                phoneUtil.countries()
                    .filter { it.name.toLowerCase().matches(regex) }
                    .sortedWith(Comparator { c1, c2 ->
                        compareValues(c1.name.toLowerCase(), c2.name.toLowerCase())
                    })
            }
    }

    override val backAction = action<Unit> {
        this.doOnBeforeNext {
            if (mode.value == SEARCH_OPENED) {
                mode.accept(SEARCH_CLOSED)
            } else {
                super.backAction.accept(Unit)
            }
        }
    }

    val clearClicks = action<Unit> {
        this.doOnBeforeNext {
            if (searchQueryInput.text.value.isEmpty()) {
                mode.accept(SEARCH_CLOSED)
            } else {
                searchQueryInput.text.accept("")
            }
        }
    }

    val openSearchClicks = action<Unit> {
        this.map { SEARCH_OPENED }
            .doOnBeforeNext(mode.consumer()::accept)
    }

    val countryClicks = action<Country> {
        this.doOnBeforeNext {
            sendMessage(CountryChosen(it))
        }
    }
}