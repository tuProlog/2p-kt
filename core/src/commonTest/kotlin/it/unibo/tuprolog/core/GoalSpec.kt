package it.unibo.tuprolog.core

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.impl.GoalImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.GoalUtils

/**
 * Test class for [Goal] companion object
 *
 * @author Enrico
 */
class GoalSpec :
    FunSpec({
        withTests(nameFn = { "of/${it.id}" }, GoalUtils.mixedGoalCases) { case ->
            assertEqualities(GoalImpl(case.body), Goal.of(case.body))
        }

        test("directive-of-vararg-constructs-tuple-for-multiple-arguments") {
            val directiveFirstPart = Atom.of("hello")
            val directiveSecondPart = Atom.of("world")

            val correctInstance = GoalImpl(Tuple.wrapIfNeeded(directiveFirstPart, directiveSecondPart))
            val toBeTested = Goal.of(directiveFirstPart, directiveSecondPart)

            assertEqualities(correctInstance, toBeTested)
        }

        test("directive-of-iterable-constructs-tuple-for-multiple-arguments") {
            val directiveFirstPart = Atom.of("hello")
            val directiveSecondPart = Atom.of("world")

            val correctInstance = GoalImpl(Tuple.wrapIfNeeded(directiveFirstPart, directiveSecondPart))
            val toBeTested = Goal.of(listOf(directiveFirstPart, directiveSecondPart))

            assertEqualities(correctInstance, toBeTested)
        }

        test("directive-of-iterable-complains-if-empty-iterable") {
            shouldThrow<IllegalArgumentException> { Goal.of(emptyList()) }
        }

        test("of-sequence/same-as-of-list") {
            val goals = listOf(Atom.of("a"), Atom.of("b"))
            Goal.of(goals.asSequence()) shouldBe Goal.of(goals)
        }

        test("template/one-anonymous-goal-by-default") {
            Goal
                .template()
                .body.isVar
                .shouldBeTrue()
        }

        test("template/as-many-goals-as-requested") {
            (Goal.template(3).bodyItems.count { it.isVar }) shouldBe 3
        }

        test("template/rejects-no-goals") {
            shouldThrow<IllegalArgumentException> { Goal.template(0) }
        }

        test("set-body-items/replaces-the-goals") {
            Goal.of(Atom.of("a")).setBodyItems(sequenceOf(Atom.of("b"))) shouldBe Goal.of(Atom.of("b"))
        }
    })
