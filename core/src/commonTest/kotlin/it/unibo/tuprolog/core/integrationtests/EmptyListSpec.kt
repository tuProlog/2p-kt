package it.unibo.tuprolog.core.integrationtests

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Empty
import it.unibo.tuprolog.core.EmptyList
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertAllVsAll
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertNoEqualities
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import it.unibo.tuprolog.core.List as LogicList

private class EmptyListCreationCase(
    val id: String,
    val create: () -> Term,
)

class EmptyListSpec :
    FunSpec({
        val correctAtom = "[]"

        val notCorrectAtom = "[ ]"

        val creationCases =
            listOf(
                EmptyListCreationCase("constructor") { EmptyList() },
                EmptyListCreationCase("empty-list") { Empty.list() },
                EmptyListCreationCase("logic-list-empty") { LogicList.empty() },
                EmptyListCreationCase("logic-list-of") { LogicList.of() },
                EmptyListCreationCase("atom-of") { Atom.of(correctAtom) },
                EmptyListCreationCase("struct-of") { Struct.of(correctAtom) },
            )

        withTests(nameFn = { "creates-empty-list/${it.id}" }, creationCases) { case ->
            TermTypeAssertionUtils.assertIsEmptyList(case.create())
        }

        test("equality") {
            assertAllVsAll(creationCases.map { it.create() }, ::assertEqualities)
        }

        withTests(nameFn = { "not-equal-to-other-atom/${it.id}" }, creationCases) { case ->
            val notEmptyListAtom = Atom.of(notCorrectAtom)
            assertNoEqualities(notEmptyListAtom, case.create())
        }
    })
