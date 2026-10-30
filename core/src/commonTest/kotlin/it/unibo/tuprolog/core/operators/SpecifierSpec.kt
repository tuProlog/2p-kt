package it.unibo.tuprolog.core.operators

import io.kotest.core.spec.style.FunSpec
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
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

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

        test("is-prefix") {
            prefixSpecifiers.forEach { assertTrue { it.isPrefix } }

            postfixSpecifiers.forEach { assertFalse { it.isPrefix } }
            infixSpecifiers.forEach { assertFalse { it.isPrefix } }
        }

        test("is-postfix") {
            postfixSpecifiers.forEach { assertTrue { it.isPostfix } }

            prefixSpecifiers.forEach { assertFalse { it.isPostfix } }
            infixSpecifiers.forEach { assertFalse { it.isPostfix } }
        }

        test("is-infix") {
            infixSpecifiers.forEach { assertTrue { it.isInfix } }

            prefixSpecifiers.forEach { assertFalse { it.isInfix } }
            postfixSpecifiers.forEach { assertFalse { it.isInfix } }
        }

        test("to-term") {
            val correctTerms = allSpecifiers.map { Atom.of(it.name.lowercase()) }
            val toBeTested = allSpecifiers.map { it.toTerm() }

            onCorrespondingItems(correctTerms, toBeTested, ::assertEqualities)
        }

        test("prefix-set") {
            assertEquals(prefixSpecifiers, Specifier.PREFIX)
        }

        test("postfix-set") {
            assertEquals(postfixSpecifiers, Specifier.POSTFIX)
        }

        test("infix-set") {
            assertEquals(infixSpecifiers, Specifier.INFIX)
        }

        test("non-prefix-set") {
            assertEquals(postfixSpecifiers + infixSpecifiers, Specifier.NON_PREFIX)
        }

        test("from-term-that-is-atom") {
            assertSame(XFY, Specifier.fromTerm(Atom.of("xFy")))

            assertNotEquals(YFX, Specifier.fromTerm(Atom.of("XfX")))
        }

        test("from-term-that-is-atom-but-with-no-correspondence") {
            assertFailsWith<IllegalArgumentException> { Specifier.fromTerm(Atom.of("ciao")) }
            assertFailsWith<IllegalArgumentException> { Specifier.fromTerm(Truth.TRUE) }
        }

        test("from-term") {
            assertSame(XFY, Specifier.fromTerm(Struct.of("xFy")))

            assertNotEquals(YFX, Specifier.fromTerm(Struct.of("XfX")))
        }

        test("from-term-that-is-not-atom") {
            assertFailsWith<IllegalArgumentException> { Specifier.fromTerm(Var.of("XFX")) }
        }
    })
