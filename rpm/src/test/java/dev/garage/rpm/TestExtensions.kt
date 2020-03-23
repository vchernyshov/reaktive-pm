package dev.garage.rpm

import com.badoo.reaktive.test.base.assertNotError
import com.badoo.reaktive.test.base.assertSubscribed
import com.badoo.reaktive.test.observable.TestObservableObserver
import com.badoo.reaktive.test.observable.assertNotComplete
import com.badoo.reaktive.test.observable.assertValues
import kotlin.test.assertEquals
import kotlin.test.fail

//fun equals(o1: Any?, o2: Any?): Boolean { // NOPMD
//    return o1 === o2 || (o1 != null && o1 == o2)
//}

fun valueAndClass(o: Any?): String? {
    return if (o != null) {
        "$o (class: ${o.javaClass.simpleName} )"
    } else "null"
}

fun <T> TestObservableObserver<T>.assertOnlyValues(vararg expectedValues: T) {
    assertSubscribed()
    assertValues(listOf(*expectedValues))
    assertNotError()
    assertNotComplete()
}

fun <T> TestObservableObserver<T>.assertValuesCount(expectedCount: Int) {
    assertEquals(
        expectedCount,
        values.size,
        "Value counts differ; expected: $expectedCount but was: ${values.size}"
    )
}

//fun <T> TestObservableObserver<T>.assertValueAt(index: Int, value: T) {
//    val s = values.size
//    if (s == 0) {
//        throw fail("No values")
//    }
//
//    if (index >= s) {
//        throw fail("Invalid index: $index")
//    }
//
//    val v = values[index]
//    if (!equals(value, v)) {
//        throw fail("expected: ${valueAndClass(value)} but was: ${valueAndClass(v)}")
//    }
//}

fun <T> TestObservableObserver<T>.assertValueAt(index: Int, valuePredicate: (T) -> Boolean) {
    val s = values.size
    if (s == 0) {
        throw fail("No values")
    }

    if (index >= values.size) {
        throw fail("Invalid index: $index")
    }

    var found = false

    try {
        if (valuePredicate(values[index])) {
            found = true
        }
    } catch (ex: Exception) {
        throw ex
    }

    if (!found) {
        throw fail("Value not present")
    }
}