package it.unibo.tuprolog.solve

import kotlin.jvm.JvmField

object SolveClassicTest {
    @JvmField
    val expectations =
        Expectations(
            prologShouldWork = true,
            classicShouldWork = true,
        )
}
