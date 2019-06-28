package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.Atom
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Utils singleton for testing [Atom]
 *
 * @author Enrico
 */
internal object AtomUtils {

    internal val ATOM_REGEX_PATTERN = "^[a-z][a-zA-Z0-9_]*$".toRegex()

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
        assertSame(atom, atom.freshCopy())
    }
}
