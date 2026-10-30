package it.unibo.tuprolog.core.integrationtests

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Block
import it.unibo.tuprolog.core.Empty
import it.unibo.tuprolog.core.EmptyBlock
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertAllVsAll
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertNoEqualities
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils

class EmptyBlockSpec :
    FunSpec({
        val correctAtom = "{}"

        val notCorrectAtom = "{ }"

        val heterogeneousCreatedInstances =
            listOf(
                EmptyBlock(),
                Empty.block(),
                Block.empty(),
                Block.of(),
                Atom.of(correctAtom),
                Struct.of(correctAtom),
            )

        test("various-creation-methods-create-correctly-empty-block") {
            heterogeneousCreatedInstances.forEach(TermTypeAssertionUtils::assertIsEmptyBlock)
        }

        test("equality") {
            assertAllVsAll(heterogeneousCreatedInstances, ::assertEqualities)

            val notEmptyBlockAtom = Atom.of(notCorrectAtom)
            heterogeneousCreatedInstances.forEach { correct -> assertNoEqualities(notEmptyBlockAtom, correct) }
        }
    })
