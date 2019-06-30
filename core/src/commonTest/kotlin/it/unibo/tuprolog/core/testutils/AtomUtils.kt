package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.Atom
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Utils singleton for testing [Atom]
 *
 * @author Enrico
 */
internal object AtomUtils {

    /**
     * For non special atoms are intended all valid atoms excluding: `true, fail, [], {}`
     */
    internal val nonSpecialAtoms by lazy {
        // these could be randomly generated from some library in future, maybe starting from Atom regex
        listOf("anAtom",
                "AnUppercaseAtom",
                "_anAtomStartingWithUnderscore",
                "a_snake_cased_atom",
                "a string",
                "1",
                "1.3",
                "+",
                ",",
                "is",
                "!")
    }

    /**
     * Special atoms are atoms for which there's a known subclass, like: `true, fail, [], {}`
     */
    internal val specialAtoms by lazy {
        listOf(
                "[]",
                "{}",
                "true",
                "fail")
    }

    /**
     * Asserts that no arguments are present in an Atom
     */
    internal fun assertNoArguments(atom: Atom) {
        assertTrue(atom.args.isEmpty())
        assertTrue(atom.argsList.isEmpty())
        assertTrue(atom.argsSequence.toList().isEmpty())
    }

    /**
     * Asserts that arity is zero in an Atom
     */
    internal fun assertZeroArity(atom: Atom) {
        assertTrue(atom.arity == 0)
    }

    /**
     * Asserts that value and functor are the same in an Atom
     */
    internal fun assertSameValueAndFunctor(atom: Atom) {
        assertSame(atom.value, atom.functor)
    }

    /**
     * Asserts that a freshCopy of an Atom is the Atom itself
     */
    internal fun assertFreshCopyIsItself(atom: Atom) {
        assertEquals(atom, atom.freshCopy())
        assertTrue(atom structurallyEquals atom.freshCopy())
        assertTrue(atom strictlyEquals atom.freshCopy())
        assertSame(atom, atom.freshCopy())
    }
}
