package it.unibo.tuprolog.core.integrationtests

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Empty
import it.unibo.tuprolog.core.EmptyList
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertAllVsAll
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertNoEqualities
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import it.unibo.tuprolog.core.List as LogicList

class EmptyListSpec :
    FunSpec({
        val correctAtom = "[]"

        val notCorrectAtom = "[ ]"

        val heterogeneousCreatedInstances =
            listOf(
                EmptyList(),
                Empty.list(),
                LogicList.empty(),
                LogicList.of(),
                Atom.of(correctAtom),
                Struct.of(correctAtom),
            )

        test("various-creation-methods-create-correctly-empty-list") {
            heterogeneousCreatedInstances.forEach(TermTypeAssertionUtils::assertIsEmptyList)
        }

        test("equality") {
            assertAllVsAll(heterogeneousCreatedInstances, ::assertEqualities)

            val notEmptyListAtom = Atom.of(notCorrectAtom)
            heterogeneousCreatedInstances.forEach { correct -> assertNoEqualities(notEmptyListAtom, correct) }
        }
    })
