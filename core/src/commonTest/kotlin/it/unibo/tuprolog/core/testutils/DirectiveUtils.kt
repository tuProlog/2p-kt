package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var

/**
 * Utils singleton for testing [Directive]
 *
 * @author Enrico
 */
internal object DirectiveUtils {
    /** A named [Directive] body, for parameterized tests */
    internal data class DirectiveCase(
        val id: String,
        val body: Term,
    )

    private fun bodiesOf(rules: List<RuleUtils.RuleCase>) = rules.map { DirectiveCase("body-of-${it.id}", it.body) }

    /** Contains ground Directives (aka without variables), well-formed */
    val groundWellFormedDirectiveCases by lazy {
        listOf(
            DirectiveCase("assert", Struct.of("assert", Struct.of("son", Atom.of("jack"), Atom.of("bob")))),
            DirectiveCase(
                "tuple-of-asserts",
                Tuple.wrapIfNeeded(
                    Struct.of("assertZ", Struct.of("money", Integer.of(50))),
                    Struct.of("assertA", Struct.of("debt", Integer.of(30))),
                ),
            ),
        ) + bodiesOf(RuleUtils.groundWellFormedRuleCases)
    }

    /** Contains non ground Directives, with variables and well-formed */
    val nonGroundWellFormedDirectiveCases by lazy {
        listOf(
            DirectiveCase("retract", Struct.of("retract", Struct.of("f", Var.anonymous()))),
            DirectiveCase("retract-all", Struct.of("retractAll", Struct.of("x", Var.of("X")))),
        ) + bodiesOf(RuleUtils.nonGroundWellFormedRuleCases).filterNot { it.body.isGround }
    }

    /** Contains ground Directives (aka without variables), not well-formed */
    val groundNonWellFormedDirectiveCases by lazy { bodiesOf(RuleUtils.groundNonWellFormedRuleCases) }

    /** Contains non ground Directives, with variables and not well-formed */
    val nonGroundNonWellFormedDirectiveCases by lazy {
        bodiesOf(RuleUtils.nonGroundNonWellFormedRuleCases).filterNot { it.body.isGround }
    }

    /** Contains well-formed Directives, ground and not */
    val wellFormedDirectiveCases by lazy { groundWellFormedDirectiveCases + nonGroundWellFormedDirectiveCases }

    /** Contains not well-formed Directives, ground and not */
    val nonWellFormedDirectiveCases by lazy {
        groundNonWellFormedDirectiveCases + nonGroundNonWellFormedDirectiveCases
    }

    /** Contains ground Directives (aka without variables), well-formed and not */
    val groundDirectiveCases by lazy { groundWellFormedDirectiveCases + groundNonWellFormedDirectiveCases }

    /** Contains non ground Directives, with variables, well-formed and not*/
    val nonGroundDirectiveCases by lazy { nonGroundWellFormedDirectiveCases + nonGroundNonWellFormedDirectiveCases }

    /** Contains mixed [groundDirectiveCases] and [nonGroundDirectiveCases] */
    val mixedDirectiveCases by lazy {
        groundWellFormedDirectiveCases +
            groundNonWellFormedDirectiveCases +
            nonGroundWellFormedDirectiveCases +
            nonGroundNonWellFormedDirectiveCases
    }
}
