package it.unibo.tuprolog.core.integrationtests

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertAllVsAllEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertNoEqualities
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.Test
import it.unibo.tuprolog.core.List as LogicList

class TestEmptyList {

    private val correctAtom = "[]"
    private val notCorrectAtom = "[ ]"

    private val heterogeneousCreatedInstances = listOf(
            EmptyList(),
            Empty.list(),
            LogicList.empty(),
            lstOf(),
            LogicList.of(),
            Atom.of(correctAtom),
            atomOf(correctAtom),
            Struct.of(correctAtom),
            structOf(correctAtom))

    @Test
    fun variousCreationMethodsCreateCorrectlyEmptyList() {
        heterogeneousCreatedInstances.forEach(TermTypeAssertionUtils::assertIsEmptyList)
    }

    @Test
    fun equality() {
        assertAllVsAllEqualities(heterogeneousCreatedInstances)

        val notEmptyListAtom = Atom.of(notCorrectAtom)
        heterogeneousCreatedInstances.forEach { correct -> assertNoEqualities(notEmptyListAtom, correct) }
    }
}
