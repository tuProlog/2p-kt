package it.unibo.tuprolog.core.operators

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
import kotlin.test.Test
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
internal class SpecifierTest {

    private val prefixSpecifiers = setOf(FX, FY)
    private val postfixSpecifiers = setOf(XF, YF)
    private val infixSpecifiers = setOf(XFX, YFX, XFY)

    private val allSpecifiers = prefixSpecifiers + postfixSpecifiers + infixSpecifiers

    @Test
    fun isPrefix() {
        prefixSpecifiers.forEach { assertTrue { it.isPrefix } }

        postfixSpecifiers.forEach { assertFalse { it.isPrefix } }
        infixSpecifiers.forEach { assertFalse { it.isPrefix } }
    }

    @Test
    fun isPostfix() {
        postfixSpecifiers.forEach { assertTrue { it.isPostfix } }

        prefixSpecifiers.forEach { assertFalse { it.isPostfix } }
        infixSpecifiers.forEach { assertFalse { it.isPostfix } }
    }

    @Test
    fun isInfix() {
        infixSpecifiers.forEach { assertTrue { it.isInfix } }

        prefixSpecifiers.forEach { assertFalse { it.isInfix } }
        postfixSpecifiers.forEach { assertFalse { it.isInfix } }
    }

    @Test
    fun toTerm() {
        val correctTerms = allSpecifiers.map { Atom.of(it.name.lowercase()) }
        val toBeTested = allSpecifiers.map { it.toTerm() }

        onCorrespondingItems(correctTerms, toBeTested, ::assertEqualities)
    }

    @Test
    fun prefixSet() {
        assertEquals(prefixSpecifiers, Specifier.PREFIX)
    }

    @Test
    fun postfixSet() {
        assertEquals(postfixSpecifiers, Specifier.POSTFIX)
    }

    @Test
    fun infixSet() {
        assertEquals(infixSpecifiers, Specifier.INFIX)
    }

    @Test
    fun nonPrefixSet() {
        assertEquals(postfixSpecifiers + infixSpecifiers, Specifier.NON_PREFIX)
    }

    @Test
    fun fromTermThatIsAtom() {
        assertSame(XFY, Specifier.fromTerm(Atom.of("xFy")))

        assertNotEquals(YFX, Specifier.fromTerm(Atom.of("XfX")))
    }

    @Test
    fun fromTermThatIsAtomButWithNoCorrespondence() {
        assertFailsWith<IllegalArgumentException> { Specifier.fromTerm(Atom.of("ciao")) }
        assertFailsWith<IllegalArgumentException> { Specifier.fromTerm(Truth.TRUE) }
    }

    @Test
    fun fromTerm() {
        assertSame(XFY, Specifier.fromTerm(Struct.of("xFy")))

        assertNotEquals(YFX, Specifier.fromTerm(Struct.of("XfX")))
    }

    @Test
    fun fromTermThatIsNotAtom() {
        assertFailsWith<IllegalArgumentException> { Specifier.fromTerm(Var.of("XFX")) }
    }
}
