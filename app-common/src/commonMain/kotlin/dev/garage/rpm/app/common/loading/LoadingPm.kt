/*
 * Copyright (c) 2019 MobileUp
 */

package dev.garage.rpm.app.common.loading

import com.badoo.reaktive.observable.doOnBeforeNext
import com.badoo.reaktive.observable.filter
import com.badoo.reaktive.observable.take
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.map
import com.badoo.reaktive.single.singleFromFunction
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.action
import dev.garage.rpm.base.lpc.Emptyable
import dev.garage.rpm.delay
import dev.garage.rpm.lc.controls.loadingControl
import kotlin.random.Random

private const val CONTENT_STRING =
    "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."

class LoadingPm : PresentationModel() {

    private val random = Random(Int.MAX_VALUE)

    val loadingControl = loadingControl(sourceData = loadData().map { ContentString(it) })

    private val onCreate = action<Unit> {
        lifecycleObservable
            .filter { it == Lifecycle.CREATED }
            .take(1)
            .doOnBeforeNext { loadingControl.forceLoadAction.accept(Unit) }
    }

    private fun loadData(): Single<String> {
        return singleFromFunction { testData() }.delay(3000)
    }

    private fun testData(): String = when (random.nextInt(3)) {
        0 -> CONTENT_STRING
        1 -> ""
        else -> throw NullPointerException()
    }

    data class ContentString(val text: String) : Emptyable {
        override fun isEmpty(): Boolean {
            return text.isEmpty()
        }
    }
}
