package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.impl.IndicatorImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.IndicatorUtils

/** Tests for the [Indicator] companion object's factories */
internal class IndicatorSpec :
    FunSpec({
        withTests(nameFn = { "of-terms/${it.id}" }, IndicatorUtils.mixedIndicatorCases) { case ->
            assertEqualities(IndicatorImpl(case.name, case.arity), Indicator.of(case.name, case.arity))
        }

        withTests(
            nameFn = { "of-name-and-arity/${it.id}" },
            IndicatorUtils.mixedIndicatorCases.filter { it.name.isAtom && it.arity.isInteger },
        ) { case ->
            val name = case.name.castToAtom().value
            val arity =
                case.arity
                    .castToInteger()
                    .intValue
                    .toInt()
            assertEqualities(IndicatorImpl(case.name, case.arity), Indicator.of(name, arity))
        }
    })
