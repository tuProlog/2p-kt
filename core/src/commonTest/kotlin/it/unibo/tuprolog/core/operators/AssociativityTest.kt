package it.unibo.tuprolog.core.operators

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.operators.Associativity.*
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import kotlin.test.*

/**
 * Test class for [Associativity]
 *
 * @author Enrico
 */
internal class AssociativityTest {

    private val prefixAssociativity = setOf(FX, FY)
    private val postfixAssociativity = setOf(XF, YF)
    private val infixAssociativity = setOf(XFX, YFX, XFY)

    private val allAssociativity = prefixAssociativity + postfixAssociativity + infixAssociativity

    @Test
    fun isPrefix() {
        prefixAssociativity.forEach { assertTrue { it.isPrefix } }

        postfixAssociativity.forEach { assertFalse { it.isPrefix } }
        infixAssociativity.forEach { assertFalse { it.isPrefix } }
    }

    @Test
    fun isPostfix() {
        postfixAssociativity.forEach { assertTrue { it.isPostfix } }

        prefixAssociativity.forEach { assertFalse { it.isPostfix } }
        infixAssociativity.forEach { assertFalse { it.isPostfix } }
    }

    @Test
    fun isInfix() {
        infixAssociativity.forEach { assertTrue { it.isInfix } }

        prefixAssociativity.forEach { assertFalse { it.isInfix } }
        postfixAssociativity.forEach { assertFalse { it.isInfix } }
    }

    @Test
    fun toTerm() {
        val correctTerms = allAssociativity.map { Atom.of(it.name.toLowerCase()) }
        val toBeTested = allAssociativity.map { it.toTerm() }

        onCorrespondingItems(correctTerms, toBeTested, ::assertEqualities)
    }

    @Test
    fun prefixSet() {
        assertEquals(prefixAssociativity, Associativity.PREFIX)
    }

    @Test
    fun postfixSet() {
        assertEquals(postfixAssociativity, Associativity.POSTFIX)
    }

    @Test
    fun infixSet() {
        assertEquals(infixAssociativity, Associativity.INFIX)
    }

    @Test
    fun nonPrefixSet() {
        assertEquals(postfixAssociativity + infixAssociativity, Associativity.NON_PREFIX)
    }

    @Test
    fun fromTermThatIsAtom() {
        assertSame(XFY, Associativity.fromTerm(Atom.of("xFy")))

        assertNotEquals(YFX, Associativity.fromTerm(Atom.of("XfX")))
    }

    @Test
    fun fromTermThatIsAtomButWithNoCorrespondence() {
        assertFailsWith<IllegalArgumentException> { Associativity.fromTerm(Atom.of("ciao")) }
        assertFailsWith<IllegalArgumentException> { Associativity.fromTerm(Truth.`true`()) }
    }

    @Test
    fun fromTerm() {
        assertSame(XFY, Associativity.fromTerm(Struct.of("xFy")))

        assertNotEquals(YFX, Associativity.fromTerm(Struct.of("XfX")))
    }

    @Test
    fun fromTermThatIsNotAtom() {
        assertFailsWith<IllegalArgumentException> { Associativity.fromTerm(Var.of("XFX")) }
    }

}
