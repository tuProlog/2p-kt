package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.Atom
import kotlin.test.assertTrue

/**
 * Utils singleton for testing [Atom]
 *
 * @author Enrico
 */
internal object AtomUtils {

    /** For non special atoms are intended all valid atoms excluding: `true, fail, [], {}` */
    internal val nonSpecialAtoms by lazy {
        // these could be randomly generated from some library in future, maybe starting from Atom regex
        listOf(
            "anAtom",
            "AnUppercaseAtom",
            "_anAtomStartingWithUnderscore",
            "a_snake_cased_atom",
            "a string",
            "1",
            "1.3",
            "+",
            ",",
            "is",
            "!"
        )
    }

    /** Special atoms are atoms for which there's a known subclass, like: `true, fail, [], {}` */
    internal val specialAtoms by lazy {
        listOf(
            "[]",
            "{}",
            "true",
            "fail"
        )
    }

    /**
     * A mix of special and non special atoms
     *
     * @see specialAtoms
     * @see nonSpecialAtoms
     */
    internal val mixedAtoms by lazy { nonSpecialAtoms + specialAtoms }

    /** Asserts that no arguments are present in an Atom */
    internal fun assertNoArguments(atom: Atom) {
        assertTrue(atom.args.isEmpty())
        assertTrue(atom.argsList.isEmpty())
        assertTrue(atom.argsSequence.toList().isEmpty())
    }

}
