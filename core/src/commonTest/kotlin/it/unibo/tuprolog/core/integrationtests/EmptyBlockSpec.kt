package it.unibo.tuprolog.core.integrationtests

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Block
import it.unibo.tuprolog.core.Empty
import it.unibo.tuprolog.core.EmptyBlock
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertAllVsAll
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertNoEqualities
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils

private class EmptyBlockCreationCase(
    val id: String,
    val create: () -> Term,
)

class EmptyBlockSpec :
    FunSpec({
        val correctAtom = "{}"

        val notCorrectAtom = "{ }"

        val creationCases =
            listOf(
                EmptyBlockCreationCase("constructor") { EmptyBlock() },
                EmptyBlockCreationCase("empty-block") { Empty.block() },
                EmptyBlockCreationCase("block-empty") { Block.empty() },
                EmptyBlockCreationCase("block-of") { Block.of() },
                EmptyBlockCreationCase("atom-of") { Atom.of(correctAtom) },
                EmptyBlockCreationCase("struct-of") { Struct.of(correctAtom) },
            )

        withTests(nameFn = { "creates-empty-block/${it.id}" }, creationCases) { case ->
            TermTypeAssertionUtils.assertIsEmptyBlock(case.create())
        }

        test("equality") {
            assertAllVsAll(creationCases.map { it.create() }, ::assertEqualities)
        }

        withTests(nameFn = { "not-equal-to-other-atom/${it.id}" }, creationCases) { case ->
            val notEmptyBlockAtom = Atom.of(notCorrectAtom)
            assertNoEqualities(notEmptyBlockAtom, case.create())
        }
    })
