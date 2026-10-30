package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.impl.DirectiveImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.DirectiveUtils
import kotlin.test.assertFailsWith

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
    })
