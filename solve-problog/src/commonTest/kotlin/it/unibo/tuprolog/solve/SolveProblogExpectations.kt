package it.unibo.tuprolog.solve

import kotlin.jvm.JvmField

object SolveProblogExpectations {
    @JvmField
    val expectations =
        Expectations(
            prologShouldWork = true,
            problogShouldWork = true,
        )
}
