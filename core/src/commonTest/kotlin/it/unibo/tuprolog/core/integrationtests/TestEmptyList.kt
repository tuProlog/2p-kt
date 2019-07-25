package it.unibo.tuprolog.core.integrationtests

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Empty
import it.unibo.tuprolog.core.EmptyList
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertAllVsAll
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
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
            LogicList.of(),
            Atom.of(correctAtom),
            Struct.of(correctAtom))

    @Test
    fun variousCreationMethodsCreateCorrectlyEmptyList() {
        heterogeneousCreatedInstances.forEach(TermTypeAssertionUtils::assertIsEmptyList)
    }

    @Test
    fun equality() {
        assertAllVsAll(heterogeneousCreatedInstances, ::assertEqualities)

        val notEmptyListAtom = Atom.of(notCorrectAtom)
        heterogeneousCreatedInstances.forEach { correct -> assertNoEqualities(notEmptyListAtom, correct) }
    }
}
