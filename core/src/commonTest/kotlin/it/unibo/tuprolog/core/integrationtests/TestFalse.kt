package it.unibo.tuprolog.core.integrationtests

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertAllVsAll
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertNoEqualities
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils.assertIsTruth
import kotlin.test.Test
import kotlin.test.assertTrue

class TestFalse {
    private val correctAtom = "false"
    private val notCorrectAtom = "fail"

    private val heterogeneousCreatedInstances =
        listOf(
            Truth.FALSE,
            Truth.of(false),
            Atom.of(correctAtom),
            Struct.of(correctAtom),
        )

    @Test
    fun variousCreationMethodsCreateCorrectlyFail() {
        heterogeneousCreatedInstances.forEach {
            assertIsTruth(it)
            assertTrue(it.isFail)
        }
    }

    @Test
    fun equality() {
        assertAllVsAll(heterogeneousCreatedInstances, ::assertEqualities)

        val notFailAtom = Atom.of(notCorrectAtom)
        heterogeneousCreatedInstances.forEach { correct -> assertNoEqualities(notFailAtom, correct) }
    }
}
