package it.unibo.tuprolog.solve

import kotlin.jvm.JvmField

object SolveProblogTest {
    @JvmField
    val expectations =
        Expectations(
            classicShouldWork = true,
            prologShouldWork = true,
            problogShouldWork = true,
        )
}
