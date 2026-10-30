package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.impl.DirectiveImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.DirectiveUtils
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

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
            assertFailsWith<IllegalArgumentException> { Directive.of(emptyList()) }
        }

        test("of-sequence/same-as-of-list") {
            val goals = listOf(Atom.of("a"), Atom.of("b"))
            assertEquals(Directive.of(goals), Directive.of(goals.asSequence()))
        }

        test("template/one-anonymous-goal-by-default") {
            assertTrue(Directive.template().body.isVar)
        }

        test("template/as-many-goals-as-requested") {
            assertEquals(3, Directive.template(3).bodyItems.count { it.isVar })
        }

        test("template/rejects-no-goals") {
            assertFailsWith<IllegalArgumentException> { Directive.template(0) }
        }

        test("set-body-items/replaces-the-goals") {
            assertEquals(Directive.of(Atom.of("b")), Directive.of(Atom.of("a")).setBodyItems(sequenceOf(Atom.of("b"))))
        }
    })
