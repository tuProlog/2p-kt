package it.unibo.tuprolog.unify.testutils

import io.kotest.core.factory.TestFactory
import io.kotest.core.spec.style.funSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.Unificator
import it.unibo.tuprolog.unify.eq
import it.unibo.tuprolog.unify.testutils.UnificatorUtils.assertMatchCorrect
import it.unibo.tuprolog.unify.testutils.UnificatorUtils.assertMguCorrect
import it.unibo.tuprolog.unify.testutils.UnificatorUtils.assertUnifiedTermCorrect
import it.unibo.tuprolog.unify.testutils.UnificatorUtils.memberClause
import it.unibo.tuprolog.unify.testutils.UnificatorUtils.negativeMemberPatterns
import it.unibo.tuprolog.unify.testutils.UnificatorUtils.positiveMemberPatterns
import it.unibo.tuprolog.core.List.Companion as LogicList

/**
 * The behaviour every [Unificator] built by [factory] (out of a context) must exhibit, as test leaves named
 * `<name>/...`. The [disabled] case ids (as in [UnificatorUtils]) are skipped, and must all exist.
 */
internal fun unificatorContract(
    name: String,
    factory: (Substitution) -> Unificator,
    disabled: Set<String> = emptySet(),
): TestFactory =
    funSpec {
        val examples = UnificatorUtils.successfulUnifications + UnificatorUtils.failedUnifications
        val sequences = UnificatorUtils.successSequenceOfUnification + UnificatorUtils.failSequenceOfUnification
        val known = (examples + UnificatorUtils.occurCheckFailedUnifications).map { it.id } + sequences.map { it.id }
        require(known.containsAll(disabled)) { "Unknown disabled cases: ${disabled - known.toSet()}" }

        val enabledExamples = examples.filter { it.id !in disabled }
        val enabledSequences = sequences.filter { it.id !in disabled }
        val occurCheckExamples = UnificatorUtils.occurCheckFailedUnifications.filter { it.id !in disabled }
        val unificator = factory(Substitution.empty())

        test("$name/context-property-works-as-expected") {
            val context = Substitution.of("A", Atom.of("a"))
            factory(context).context shouldBe context
        }

        for (occurCheck in listOf(true, false)) {
            val flag = if (occurCheck) "with-occur-check" else "without-occur-check"

            withTests(nameFn = { "$name/mgu/$flag/${it.id}" }, enabledExamples) { case ->
                assertMguCorrect(case) { term1, term2 -> unificator.mgu(term1, term2, occurCheck) }
            }

            withTests(nameFn = { "$name/match/$flag/${it.id}" }, enabledExamples) { case ->
                assertMatchCorrect(case) { term1, term2 -> unificator.match(term1, term2, occurCheck) }
            }

            withTests(nameFn = { "$name/unify/$flag/${it.id}" }, enabledExamples) { case ->
                assertUnifiedTermCorrect(case) { term1, term2 -> unificator.unify(term1, term2, occurCheck) }
            }
        }

        withTests(nameFn = { "$name/mgu/occur-check-failure/${it.id}" }, occurCheckExamples) { case ->
            assertMguCorrect(case) { term1, term2 -> unificator.mgu(term1, term2, true) }
        }

        withTests(nameFn = { "$name/match/occur-check-failure/${it.id}" }, occurCheckExamples) { case ->
            assertMatchCorrect(case) { term1, term2 -> unificator.match(term1, term2, true) }
        }

        withTests(nameFn = { "$name/unify/occur-check-failure/${it.id}" }, occurCheckExamples) { case ->
            assertUnifiedTermCorrect(case) { term1, term2 -> unificator.unify(term1, term2, true) }
        }

        test("$name/occur-check-disabled-binds-variable-to-term-containing-it") {
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

            assertMguCorrect(case) { term1, term2 -> unificator.mgu(term1, term2, false) }
            assertMatchCorrect(case) { term1, term2 -> unificator.match(term1, term2, false) }
            assertUnifiedTermCorrect(case) { term1, term2 -> unificator.unify(term1, term2, false) }
        }

        withTests(nameFn = { "$name/mgu-in-context/${it.id}" }, enabledSequences) { case ->
            val context = case.contextBeforeLast(factory)
            assertMguCorrect(case.last) { t1, t2 -> factory(context).mgu(t1, t2) }
        }

        withTests(nameFn = { "$name/match-in-context/${it.id}" }, enabledSequences) { case ->
            val context = case.contextBeforeLast(factory)
            assertMatchCorrect(case.last) { t1, t2 -> factory(context).match(t1, t2) }
        }

        withTests(nameFn = { "$name/unify-in-context/${it.id}" }, enabledSequences) { case ->
            val context = case.contextBeforeLast(factory)
            assertUnifiedTermCorrect(case.last) { t1, t2 -> factory(context).unify(t1, t2) }
        }

        withTests(nameFn = { "$name/positive-corner-cases/${it.id}" }, positiveMemberPatterns) { case ->
            unificator.match(case.pattern, memberClause).shouldBeTrue()
        }

        withTests(nameFn = { "$name/negative-corner-cases/${it.id}" }, negativeMemberPatterns) { case ->
            unificator.match(case.pattern, memberClause).shouldBeFalse()
        }

        test("$name/unification-works-for-big-lists") {
            val n = 10_000
            val ints = LogicList.of((0..n).map { Integer.of(it) })
            val intsAndVars = LogicList.of((0..n).map { if (it % 100 == 0) Var.anonymous() else Integer.of(it) })
            val atoms = LogicList.of((0..n).map { Atom.of("a$it") })
            val atomsAndVars = LogicList.of((0..n).map { if (it % 100 == 0) Var.anonymous() else Atom.of("a$it") })

            with(unificator) {
                match(ints, atoms, true).shouldBeFalse()
                match(ints, atoms, false).shouldBeFalse()
                match(ints, ints, true).shouldBeTrue()
                match(ints, ints, false).shouldBeTrue()
                match(atoms, atoms, true).shouldBeTrue()
                match(atoms, atoms, false).shouldBeTrue()
                match(ints, intsAndVars, true).shouldBeTrue()
                match(ints, intsAndVars, false).shouldBeTrue()
                match(atoms, atomsAndVars, true).shouldBeTrue()
                match(atoms, atomsAndVars, false).shouldBeTrue()
            }
        }
    }
