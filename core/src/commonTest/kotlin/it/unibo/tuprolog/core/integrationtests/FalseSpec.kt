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

class FalseSpec :
    FunSpec({
        val correctAtom = "false"

        val notCorrectAtom = "fail"

        val heterogeneousCreatedInstances =
            listOf(
                Truth.FALSE,
                Truth.of(false),
                Atom.of(correctAtom),
                Struct.of(correctAtom),
            )

        test("various-creation-methods-create-correctly-fail") {
            heterogeneousCreatedInstances.forEach {
                assertIsTruth(it)
                assertTrue(it.isFail)
            }
        }

        test("equality") {
            assertAllVsAll(heterogeneousCreatedInstances, ::assertEqualities)

            val notFailAtom = Atom.of(notCorrectAtom)
            heterogeneousCreatedInstances.forEach { correct -> assertNoEqualities(notFailAtom, correct) }
        }
    })
