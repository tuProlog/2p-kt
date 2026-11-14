package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Goal
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var

/**
 * Utils singleton for testing [Goal]
 *
 * @author Enrico
 */
internal object GoalUtils {
    /** A named [Goal] body, for parameterized tests */
    internal data class GoalCase(
        val id: String,
        val body: Term,
    )

    private fun bodiesOf(rules: List<RuleUtils.RuleCase>) = rules.map { GoalCase("body-of-${it.id}", it.body) }

    /** Contains ground Directives (aka without variables), well-formed */
    val groundWellFormedGoalCases by lazy {
        listOf(
            GoalCase("assert", Struct.of("assert", Struct.of("son", Atom.of("jack"), Atom.of("bob")))),
            GoalCase(
                "tuple-of-asserts",
                Tuple.wrapIfNeeded(
                    Struct.of("assertZ", Struct.of("money", Integer.of(50))),
                    Struct.of("assertA", Struct.of("debt", Integer.of(30))),
                ),
            ),
        ) + bodiesOf(RuleUtils.groundWellFormedRuleCases)
    }

    /** Contains non ground Directives, with variables and well-formed */
    val nonGroundWellFormedGoalCases by lazy {
        listOf(
            GoalCase("retract", Struct.of("retract", Struct.of("f", Var.anonymous()))),
            GoalCase("retract-all", Struct.of("retractAll", Struct.of("x", Var.of("X")))),
        ) + bodiesOf(RuleUtils.nonGroundWellFormedRuleCases).filterNot { it.body.isGround }
    }

    /** Contains ground Directives (aka without variables), not well-formed */
    val groundNonWellFormedGoalCases by lazy { bodiesOf(RuleUtils.groundNonWellFormedRuleCases) }

    /** Contains non ground Directives, with variables and not well-formed */
    val nonGroundNonWellFormedGoalCases by lazy {
        bodiesOf(RuleUtils.nonGroundNonWellFormedRuleCases).filterNot { it.body.isGround }
    }

    /** Contains well-formed Directives, ground and not */
    val wellFormedGoalCases by lazy { groundWellFormedGoalCases + nonGroundWellFormedGoalCases }

    /** Contains not well-formed Directives, ground and not */
    val nonWellFormedGoalCases by lazy {
        groundNonWellFormedGoalCases + nonGroundNonWellFormedGoalCases
    }

    /** Contains ground Directives (aka without variables), well-formed and not */
    val groundGoalCases by lazy { groundWellFormedGoalCases + groundNonWellFormedGoalCases }

    /** Contains non ground Directives, with variables, well-formed and not*/
    val nonGroundGoalCases by lazy { nonGroundWellFormedGoalCases + nonGroundNonWellFormedGoalCases }

    /** Contains mixed [groundGoalCases] and [nonGroundGoalCases] */
    val mixedGoalCases by lazy {
        groundWellFormedGoalCases +
            groundNonWellFormedGoalCases +
            nonGroundWellFormedGoalCases +
            nonGroundNonWellFormedGoalCases
    }
}
