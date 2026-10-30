package it.unibo.tuprolog.core.integrationtests

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertAllVsAll
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertNoEqualities
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils.assertIsTruth
import kotlin.test.assertTrue

class TrueSpec :
    FunSpec({
        val correctAtom = "true"

        val notCorrectAtom = "true "

        val heterogeneousCreatedInstances =
            listOf(
                Truth.TRUE,
                Truth.of(true),
                Atom.of(correctAtom),
                Struct.of(correctAtom),
            )

        test("various-creation-methods-create-correctly-true") {
            heterogeneousCreatedInstances.forEach {
                assertIsTruth(it)
                assertTrue(it.isTrue)
            }
        }

        test("equality") {
            assertAllVsAll(heterogeneousCreatedInstances, ::assertEqualities)

            val notTrueAtom = Atom.of(notCorrectAtom)
            heterogeneousCreatedInstances.forEach { correct -> assertNoEqualities(notTrueAtom, correct) }
        }
    })
