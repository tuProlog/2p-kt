package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.impl.DirectiveImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.DirectiveUtils
import kotlin.test.assertFailsWith

/**
 * Test class for [Directive] companion object
 *
 * @author Enrico
 */
class DirectiveSpec :
    FunSpec({
        val correctInstances = DirectiveUtils.mixedDirectives.map(::DirectiveImpl)

        test("directive-of-vararg-returns-correct-instance") {
            val toBeTested = DirectiveUtils.mixedDirectives.map { body -> Directive.of(body) }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
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
    })
