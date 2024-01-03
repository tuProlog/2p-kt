package it.unibo.tuprolog.core.integrationtests

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Block
import it.unibo.tuprolog.core.Empty
import it.unibo.tuprolog.core.EmptyBlock
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertAllVsAll
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertNoEqualities
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.Test

class TestEmptyBlock {
    private val correctAtom = "{}"
    private val notCorrectAtom = "{ }"

    private val heterogeneousCreatedInstances =
        listOf(
            EmptyBlock(),
            Empty.block(),
            Block.empty(),
            Block.of(),
            Atom.of(correctAtom),
            Struct.of(correctAtom),
        )

    @Test
    fun variousCreationMethodsCreateCorrectlyEmptyBlock() {
        heterogeneousCreatedInstances.forEach(TermTypeAssertionUtils::assertIsEmptyBlock)
    }

    @Test
    fun equality() {
        assertAllVsAll(heterogeneousCreatedInstances, ::assertEqualities)

        val notEmptyBlockAtom = Atom.of(notCorrectAtom)
        heterogeneousCreatedInstances.forEach { correct -> assertNoEqualities(notEmptyBlockAtom, correct) }
    }
}
