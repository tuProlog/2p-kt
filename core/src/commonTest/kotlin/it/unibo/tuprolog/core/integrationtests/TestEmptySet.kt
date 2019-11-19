package it.unibo.tuprolog.core.integrationtests

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Empty
import it.unibo.tuprolog.core.EmptySet
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertAllVsAll
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertNoEqualities
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.Test
import it.unibo.tuprolog.core.Set.Companion as LogicSet

class TestEmptySet {

    private val correctAtom = "{}"
    private val notCorrectAtom = "{ }"

    private val heterogeneousCreatedInstances = listOf(
        EmptySet(),
        Empty.set(),
        LogicSet.empty(),
        LogicSet.of(),
        Atom.of(correctAtom),
        Struct.of(correctAtom)
    )

    @Test
    fun variousCreationMethodsCreateCorrectlyEmptySet() {
        heterogeneousCreatedInstances.forEach(TermTypeAssertionUtils::assertIsEmptySet)
    }

    @Test
    fun equality() {
        assertAllVsAll(heterogeneousCreatedInstances, ::assertEqualities)

        val notEmptySetAtom = Atom.of(notCorrectAtom)
        heterogeneousCreatedInstances.forEach { correct -> assertNoEqualities(notEmptySetAtom, correct) }
    }
}
