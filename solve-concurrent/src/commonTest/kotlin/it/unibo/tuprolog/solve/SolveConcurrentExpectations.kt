package it.unibo.tuprolog.solve

import kotlin.jvm.JvmField

object SolveConcurrentExpectations {
    @JvmField
    val expectations =
        Expectations(
            concurrentShouldWork = true,
        )
}
