package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.Atom
import kotlin.test.assertTrue

/**
 * Utils singleton for testing [Atom]
 *
 * @author Enrico
 */
internal object AtomUtils {
    /** A named atom value, for parameterized tests */
    internal data class AtomCase(
        val id: String,
        val value: String,
    )

    /** For non special atoms are intended all valid atoms excluding: `true, fail, [], {}` */
    internal val nonSpecialAtomCases by lazy {
        listOf(
            AtomCase("camel-case", "anAtom"),
            AtomCase("uppercase-initial", "AnUppercaseAtom"),
            AtomCase("underscore-initial", "_anAtomStartingWithUnderscore"),
            AtomCase("snake-case", "a_snake_cased_atom"),
            AtomCase("with-space", "a string"),
            AtomCase("integer-digits", "1"),
            AtomCase("real-digits", "1.3"),
            AtomCase("plus", "+"),
            AtomCase("comma", ","),
            AtomCase("is", "is"),
            AtomCase("cut", "!"),
        )
    }

    /** Special atoms are atoms for which there's a known subclass, like: `true, fail, [], {}` */
    internal val specialAtomCases by lazy {
        listOf(
            AtomCase("empty-list", "[]"),
            AtomCase("empty-block", "{}"),
            AtomCase("true", "true"),
            AtomCase("fail", "fail"),
            AtomCase("false", "false"),
        )
    }

    /** A mix of special and non special atoms */
    internal val mixedAtomCases by lazy { nonSpecialAtomCases + specialAtomCases }

    /** Asserts that no arguments are present in an Atom */
    internal fun assertNoArguments(atom: Atom) {
        assertTrue(atom.args.isEmpty())
        assertTrue(atom.args.isEmpty())
        assertTrue(atom.argsSequence.toList().isEmpty())
    }
}
