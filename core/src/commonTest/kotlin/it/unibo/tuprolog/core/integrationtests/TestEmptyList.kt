package it.unibo.tuprolog.core.integrationtests

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.testutils.EqualityUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.Test
import it.unibo.tuprolog.core.List as LogicList

class TestEmptyList {

    private val correctAtom = "[]"
    private val notCorrectAtom = "[ ]"

    private val heterogeneousCreatedEmptyListInstances = listOf(
            EmptyList(),
            Empty.list(),
            LogicList.empty(),
            lstOf(),
            LogicList.of(),
            Atom.of(correctAtom),
            atomOf(correctAtom),
            Struct.of(correctAtom),
            structOf(correctAtom)
    )

    @Test
    fun variousCreationMethodsCreateCorrectlyEmptyList() {
        heterogeneousCreatedEmptyListInstances.forEach(TermTypeAssertionUtils::assertIsEmptyList)
    }

    @Test
    fun equality() {
        EqualityUtils.assertAllVsAllEqualities(heterogeneousCreatedEmptyListInstances)

        val notEmptyListAtom = Atom.of(notCorrectAtom)
        heterogeneousCreatedEmptyListInstances.forEach { correct ->
            EqualityUtils.assertNoEqualities(notEmptyListAtom, correct)
        }
    }
}
