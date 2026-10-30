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

private class TrueCreationCase(
    val id: String,
    val create: () -> Term,
)

class TrueSpec :
    FunSpec({
        val correctAtom = "true"

        val notCorrectAtom = "true "

        val creationCases =
            listOf(
                TrueCreationCase("truth-constant") { Truth.TRUE },
                TrueCreationCase("truth-of") { Truth.of(true) },
                TrueCreationCase("atom-of") { Atom.of(correctAtom) },
                TrueCreationCase("struct-of") { Struct.of(correctAtom) },
            )

        withTests(nameFn = { "creates-true/${it.id}" }, creationCases) { case ->
            val created = case.create()
            assertIsTruth(created)
            assertTrue(created.isTrue)
        }

        test("equality") {
            assertAllVsAll(creationCases.map { it.create() }, ::assertEqualities)
        }

        withTests(nameFn = { "not-equal-to-other-atom/${it.id}" }, creationCases) { case ->
            val notTrueAtom = Atom.of(notCorrectAtom)
            assertNoEqualities(notTrueAtom, case.create())
        }
    })
