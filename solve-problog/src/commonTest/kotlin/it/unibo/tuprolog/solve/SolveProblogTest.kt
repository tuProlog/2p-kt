package it.unibo.tuprolog.solve

import kotlin.jvm.JvmField

object SolveProblogTest {
    @JvmField
    val expectations =
        Expectations(
            prologShouldWork = true,
            problogShouldWork = true,
        )
}
