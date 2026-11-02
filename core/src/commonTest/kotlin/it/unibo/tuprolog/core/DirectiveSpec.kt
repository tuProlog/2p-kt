package it.unibo.tuprolog.core

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.impl.DirectiveImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.DirectiveUtils

/**
 * Test class for [Directive] companion object
 *
 * @author Enrico
 */
class DirectiveSpec :
    FunSpec({
        withTests(nameFn = { "of/${it.id}" }, DirectiveUtils.mixedDirectiveCases) { case ->
            assertEqualities(DirectiveImpl(case.body), Directive.of(case.body))
        }

        test("directive-of-vararg-constructs-tuple-for-multiple-arguments") {
            val directiveFirstPart = Atom.of("hello")
            val directiveSecondPart = Atom.of("world")

            val correctInstance = DirectiveImpl(Tuple.wrapIfNeeded(directiveFirstPart, directiveSecondPart))
            val toBeTested = Directive.of(directiveFirstPart, directiveSecondPart)

            assertEqualities(correctInstance, toBeTested)
        }

        test("directive-of-iterable-constructs-tuple-for-multiple-arguments") {
            val directiveFirstPart = Atom.of("hello")
            val directiveSecondPart = Atom.of("world")

            val correctInstance = DirectiveImpl(Tuple.wrapIfNeeded(directiveFirstPart, directiveSecondPart))
            val toBeTested = Directive.of(listOf(directiveFirstPart, directiveSecondPart))

            assertEqualities(correctInstance, toBeTested)
        }

        test("directive-of-iterable-complains-if-empty-iterable") {
            shouldThrow<IllegalArgumentException> { Directive.of(emptyList()) }
        }

        test("of-sequence/same-as-of-list") {
            val goals = listOf(Atom.of("a"), Atom.of("b"))
            Directive.of(goals.asSequence()) shouldBe Directive.of(goals)
        }

        test("template/one-anonymous-goal-by-default") {
            Directive
                .template()
                .body.isVar
                .shouldBeTrue()
        }

        test("template/as-many-goals-as-requested") {
            (Directive.template(3).bodyItems.count { it.isVar }) shouldBe 3
        }

        test("template/rejects-no-goals") {
            shouldThrow<IllegalArgumentException> { Directive.template(0) }
        }

        test("set-body-items/replaces-the-goals") {
            Directive.of(Atom.of("a")).setBodyItems(sequenceOf(Atom.of("b"))) shouldBe Directive.of(Atom.of("b"))
        }
    })
