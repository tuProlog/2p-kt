package it.unibo.tuprolog.core.integrationtests

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertAllVsAll
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertNoEqualities
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils.assertIsTruth
import kotlin.test.assertTrue

private class FailCreationCase(
    val id: String,
    val create: () -> Term,
)

class FailSpec :
    FunSpec({
        val correctAtom = "fail"

        val notCorrectAtom = "false"

        val creationCases =
            listOf(
                FailCreationCase("truth-constant") { Truth.FAIL },
                FailCreationCase("atom-of") { Atom.of(correctAtom) },
                FailCreationCase("struct-of") { Struct.of(correctAtom) },
            )

        withTests(nameFn = { "creates-fail/${it.id}" }, creationCases) { case ->
            val created = case.create()
            assertIsTruth(created)
            assertTrue(created.isFail)
        }

        test("equality") {
            assertAllVsAll(creationCases.map { it.create() }, ::assertEqualities)
        }

        withTests(nameFn = { "not-equal-to-other-atom/${it.id}" }, creationCases) { case ->
            val notFailAtom = Atom.of(notCorrectAtom)
            assertNoEqualities(notFailAtom, case.create())
        }
    })
