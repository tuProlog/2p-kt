package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.*

/**
 * Utils singleton for testing [Directive]
 *
 * @author Enrico
 */
internal object DirectiveUtils {

    /** Contains ground Directives (aka without variables), well-formed */
    val groundWellFormedDirectives by lazy {
        listOf(
                Struct.of("assert", Struct.of("son", Atom.of("jack"), Atom.of("bob"))),
                Tuple.wrapIfNeeded(
                        Struct.of("assertZ", Struct.of("money", Integer.of(50))),
                        Struct.of("assertA", Struct.of("debt", Integer.of(30))))

        ) + RuleUtils.groundWellFormedRules.map { (_, body) -> body }
    }

    /** Contains non ground Directives, with variables and well-formed */
    val nonGroundWellFormedDirectives by lazy {
        listOf(
                Struct.of("retract", Struct.of("f", Var.anonymous())),
                Struct.of("retractAll", Struct.of("x", Var.of("X")))
        ) + RuleUtils.nonGroundWellFormedRules.map { (_, body) -> body }.filterNot { it.isGround }
    }

    /** Contains ground Directives (aka without variables), not well-formed */
    val groundNonWellFormedDirectives by lazy {
        RuleUtils.groundNonWellFormedRules.map { (_, body) -> body }
    }

    /** Contains non ground Directives, with variables and not well-formed */
    val nonGroundNonWellFormedDirectives by lazy {
        RuleUtils.nonGroundNonWellFormedRules.map { (_, body) -> body }.filterNot { it.isGround }
    }

    /** Contains well-formed Directives, ground and not */
    val wellFormedDirectives by lazy { groundWellFormedDirectives + nonGroundWellFormedDirectives }
    /** Contains not well-formed Directives, ground and not */
    val nonWellFormedDirectives by lazy { groundNonWellFormedDirectives + nonGroundNonWellFormedDirectives }
    /** Contains ground Directives (aka without variables), well-formed and not */
    val groundDirectives by lazy { groundWellFormedDirectives + groundNonWellFormedDirectives }
    /** Contains non ground Directives, with variables, well-formed and not*/
    val nonGroundDirectives by lazy { nonGroundWellFormedDirectives + nonGroundNonWellFormedDirectives }

    /** Contains mixed [groundDirectives] and [nonGroundDirectives] */
    val mixedDirectives by lazy {
        groundWellFormedDirectives + groundNonWellFormedDirectives + nonGroundWellFormedDirectives + nonGroundNonWellFormedDirectives
    }

}
