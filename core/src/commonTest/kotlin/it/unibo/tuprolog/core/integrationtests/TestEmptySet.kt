package it.unibo.tuprolog.core.integrationtests

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.testutils.EqualityUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.Test
import it.unibo.tuprolog.core.Set.Companion as LogicSet

class TestEmptySet {

    private val correctAtom = "{}"
    private val notCorrectAtom = "{ }"

    private val heterogeneousCreatedEmptySetInstances = listOf(
            EmptySet(),
            Empty.set(),
            LogicSet.empty(),
            setOf(),
            LogicSet.of(),
            Atom.of(correctAtom),
            atomOf(correctAtom),
            Struct.of(correctAtom),
            structOf(correctAtom)
    )

    @Test
    fun variousCreationMethodsCreateCorrectlyEmptySet() {
        heterogeneousCreatedEmptySetInstances.forEach(TermTypeAssertionUtils::assertIsEmptySet)
    }

    @Test
    fun equality() {
        EqualityUtils.assertAllVsAllEqualities(heterogeneousCreatedEmptySetInstances)

        val notEmptySetAtom = Atom.of(notCorrectAtom)
        heterogeneousCreatedEmptySetInstances.forEach { correct ->
            EqualityUtils.assertNoEqualities(notEmptySetAtom, correct)
        }
    }
}
