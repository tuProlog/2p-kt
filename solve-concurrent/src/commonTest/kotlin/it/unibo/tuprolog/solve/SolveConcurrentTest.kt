package it.unibo.tuprolog.solve

import kotlin.jvm.JvmField

object SolveConcurrentTest {
    @JvmField
    val expectations =
        Expectations(
            concurrentShouldWork = true,
        )
}
