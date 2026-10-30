package it.unibo.tuprolog.unify

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.testutils.UnificatorUtils
import it.unibo.tuprolog.unify.testutils.UnificatorUtils.assertMatchCorrect
import it.unibo.tuprolog.unify.testutils.UnificatorUtils.assertMguCorrect
import it.unibo.tuprolog.unify.testutils.UnificatorUtils.assertUnifiedTermCorrect
import it.unibo.tuprolog.unify.testutils.UnificatorUtils.memberClause
import it.unibo.tuprolog.unify.testutils.UnificatorUtils.negativeMemberPatterns
import it.unibo.tuprolog.unify.testutils.UnificatorUtils.positiveMemberPatterns
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import it.unibo.tuprolog.core.List.Companion as LogicList

/**
 * Test class for [AbstractUnificator]
 *
 * @author Enrico
 */
class AbstractUnificatorSpec :
    FunSpec({
        val myStrategyConstructor: (Substitution) -> Unificator = {
            object : AbstractUnificator(it) {
                override fun checkTermsEquality(
                    first: Term,
                    second: Term,
                ): Boolean = first == second
            }
        }

        val myStrategy = myStrategyConstructor(Substitution.empty())

        test("context-property-works-as-expected") {
            val mySubstitution = Substitution.of("A", Atom.of("a"))

            assertEquals(mySubstitution, myStrategyConstructor(mySubstitution).context)
        }

        val examples = UnificatorUtils.successfulUnifications + UnificatorUtils.failedUnifications

        for (occurCheck in listOf(true, false)) {
            val flag = if (occurCheck) "with-occur-check" else "without-occur-check"

            withTests(nameFn = { "mgu/$flag/${it.id}" }, examples) { case ->
                assertMguCorrect(case) { term1, term2 -> myStrategy.mgu(term1, term2, occurCheck) }
            }

            withTests(nameFn = { "match/$flag/${it.id}" }, examples) { case ->
                assertMatchCorrect(case) { term1, term2 -> myStrategy.match(term1, term2, occurCheck) }
            }

            withTests(nameFn = { "unify/$flag/${it.id}" }, examples) { case ->
                assertUnifiedTermCorrect(case) { term1, term2 -> myStrategy.unify(term1, term2, occurCheck) }
            }
        }

        withTests(nameFn = { "mgu/with-occur-check/${it.id}" }, UnificatorUtils.occurCheckFailedUnifications) { case ->
            assertMguCorrect(case) { term1, term2 -> myStrategy.mgu(term1, term2, true) }
        }

        withTests(
            nameFn = { "match/with-occur-check/${it.id}" },
            UnificatorUtils.occurCheckFailedUnifications,
        ) { case ->
            assertMatchCorrect(case) { term1, term2 -> myStrategy.match(term1, term2, true) }
        }

        withTests(
            nameFn = { "unify/with-occur-check/${it.id}" },
            UnificatorUtils.occurCheckFailedUnifications,
        ) { case ->
            assertUnifiedTermCorrect(case) { term1, term2 -> myStrategy.unify(term1, term2, true) }
        }

        test("occur-check-disabled-testing") {
            val xVar = Var.of("X")
            val structWithX = Struct.of("f", xVar)
            val case =
                UnificatorUtils.UnificationCase(
                    "occur-check-disabled",
                    xVar eq structWithX,
                    Substitution.of(xVar, structWithX),
                    true,
                    structWithX,
                )

            assertMguCorrect(case) { term1, term2 -> myStrategy.mgu(term1, term2, false) }
            assertMatchCorrect(case) { term1, term2 -> myStrategy.match(term1, term2, false) }
            assertUnifiedTermCorrect(case) { term1, term2 -> myStrategy.unify(term1, term2, false) }
        }

        val sequences = UnificatorUtils.successSequenceOfUnification + UnificatorUtils.failSequenceOfUnification

        withTests(nameFn = { "mgu/${it.id}" }, sequences) { case ->
            val context = case.contextBeforeLast(myStrategyConstructor)
            assertMguCorrect(case.last) { t1, t2 -> myStrategyConstructor(context).mgu(t1, t2) }
        }

        withTests(nameFn = { "match/${it.id}" }, sequences) { case ->
            val context = case.contextBeforeLast(myStrategyConstructor)
            assertMatchCorrect(case.last) { t1, t2 -> myStrategyConstructor(context).match(t1, t2) }
        }

        withTests(nameFn = { "unify/${it.id}" }, sequences) { case ->
            val context = case.contextBeforeLast(myStrategyConstructor)
            assertUnifiedTermCorrect(case.last) { t1, t2 -> myStrategyConstructor(context).unify(t1, t2) }
        }

        withTests(nameFn = { "positive-corner-cases/${it.id}" }, positiveMemberPatterns) { case ->
            assertTrue { myStrategyConstructor(Substitution.empty()).match(case.pattern, memberClause) }
        }

        withTests(nameFn = { "negative-corner-cases/${it.id}" }, negativeMemberPatterns) { case ->
            assertFalse { myStrategyConstructor(Substitution.empty()).match(case.pattern, memberClause) }
        }

        test("unification-works-for-big-lists") {
            val n = 10_000
            val ints = LogicList.of((0..n).map { Integer.of(it) })
            val intsAndVars = LogicList.of((0..n).map { if (it % 100 == 0) Var.anonymous() else Integer.of(it) })
            val atoms = LogicList.of((0..n).map { Atom.of("a$it") })
            val atomsAndVars = LogicList.of((0..n).map { if (it % 100 == 0) Var.anonymous() else Atom.of("a$it") })

            with(myStrategyConstructor(Substitution.empty())) {
                assertFalse { match(ints, atoms, true) }
                assertFalse { match(ints, atoms, false) }
                assertTrue { match(ints, ints, true) }
                assertTrue { match(ints, ints, false) }
                assertTrue { match(atoms, atoms, true) }
                assertTrue { match(atoms, atoms, false) }
                assertTrue { match(ints, intsAndVars, true) }
                assertTrue { match(ints, intsAndVars, false) }
                assertTrue { match(atoms, atomsAndVars, true) }
                assertTrue { match(atoms, atomsAndVars, false) }
            }
        }
    })
