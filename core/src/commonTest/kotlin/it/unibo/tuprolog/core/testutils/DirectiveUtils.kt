package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.*

/**
 * Utils singleton for testing [Directive]
 *
 * @author Enrico
 */
internal object DirectiveUtils {

    /**
     * Contains ground Directives (aka without variables)
     */
    val groundDirectives by lazy {
        listOf(
                Struct.of("assert", Struct.of("son", Atom.of("jack"), Atom.of("bob"))),
                Tuple.wrapIfNeeded(
                        Struct.of("assertZ", Struct.of("money", Integer.of(50))),
                        Struct.of("assertA", Struct.of("debt", Integer.of(30))))

        )
    }

    /**
     * Contains non ground Directives, with variables
     */
    val nonGroundDirectives by lazy {
        listOf(
                Struct.of("retract", Struct.of("f", Var.anonymous())),
                Struct.of("retractAll", Struct.of("x", Var.of("X")))
        )
    }

    /**
     * Contains mixed [groundDirectives] and [nonGroundDirectives]
     */
    val mixedDirectives by lazy { groundDirectives + nonGroundDirectives }

}
