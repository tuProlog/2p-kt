@file:OptIn(ExperimentalJsExport::class)

package it.unibo.tuprolog.solve.libs.oop

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/** A host class reachable on every platform: by reflection on the JVM, by its exported members on JS. */
@JsExport
class Counter {
    var count: Int = 0

    fun increment(by: Int): Int {
        count += by
        return count
    }
}
