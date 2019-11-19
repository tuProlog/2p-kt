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

class TestTrue {

    private val correctAtom = "true"
    private val notCorrectAtom = "true "

    private val heterogeneousCreatedInstances = listOf(
        Truth.`true`(),
        Truth.of(true),
        Atom.of(correctAtom),
        Struct.of(correctAtom)
    )

    @Test
    fun variousCreationMethodsCreateCorrectlyTrue() {
        heterogeneousCreatedInstances.forEach {
            assertIsTruth(it)
            assertTrue(it.isTrue)
        }
    }

    @Test
    fun equality() {
        assertAllVsAll(heterogeneousCreatedInstances, ::assertEqualities)

        val notTrueAtom = Atom.of(notCorrectAtom)
        heterogeneousCreatedInstances.forEach { correct -> assertNoEqualities(notTrueAtom, correct) }
    }
}