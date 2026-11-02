package it.unibo.tuprolog.core.operators

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.operators.Specifier.FX
import it.unibo.tuprolog.core.operators.Specifier.FY
import it.unibo.tuprolog.core.operators.Specifier.XF
import it.unibo.tuprolog.core.operators.Specifier.XFX
import it.unibo.tuprolog.core.operators.Specifier.XFY
import it.unibo.tuprolog.core.operators.Specifier.YF
import it.unibo.tuprolog.core.operators.Specifier.YFX
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities

/**
 * Test class for [Specifier]
 *
 * @author Enrico
 */
class SpecifierSpec :
    FunSpec({
        val prefixSpecifiers = setOf(FX, FY)

        val postfixSpecifiers = setOf(XF, YF)

        val infixSpecifiers = setOf(XFX, YFX, XFY)

        val allSpecifiers = prefixSpecifiers + postfixSpecifiers + infixSpecifiers

        fun specifiers(
            property: String,
            test: (Specifier) -> Unit,
        ) = withTests(nameFn = { "$property/${it.name.lowercase()}" }, allSpecifiers.toList()) { test(it) }

        specifiers("is-prefix") { it.isPrefix shouldBe (it in prefixSpecifiers) }

        specifiers("is-postfix") { it.isPostfix shouldBe (it in postfixSpecifiers) }

        specifiers("is-infix") { it.isInfix shouldBe (it in infixSpecifiers) }

        specifiers("to-term") { assertEqualities(Atom.of(it.name.lowercase()), it.toTerm()) }

        test("prefix-set") {
            Specifier.PREFIX shouldBe prefixSpecifiers
        }

        test("postfix-set") {
            Specifier.POSTFIX shouldBe postfixSpecifiers
        }

        test("infix-set") {
            Specifier.INFIX shouldBe infixSpecifiers
        }

        test("non-prefix-set") {
            Specifier.NON_PREFIX shouldBe (postfixSpecifiers + infixSpecifiers)
        }

        test("from-term-that-is-atom") {
            Specifier.fromTerm(Atom.of("xFy")) shouldBeSameInstanceAs XFY

            Specifier.fromTerm(Atom.of("XfX")) shouldNotBe YFX
        }

        test("from-term-that-is-atom-but-with-no-correspondence") {
            shouldThrow<IllegalArgumentException> { Specifier.fromTerm(Atom.of("ciao")) }
            shouldThrow<IllegalArgumentException> { Specifier.fromTerm(Truth.TRUE) }
        }

        test("from-term") {
            Specifier.fromTerm(Struct.of("xFy")) shouldBeSameInstanceAs XFY

            Specifier.fromTerm(Struct.of("XfX")) shouldNotBe YFX
        }

        test("from-term-that-is-not-atom") {
            shouldThrow<IllegalArgumentException> { Specifier.fromTerm(Var.of("XFX")) }
        }
    })
