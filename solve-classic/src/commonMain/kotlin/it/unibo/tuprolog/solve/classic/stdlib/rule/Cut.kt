package it.unibo.tuprolog.solve.classic.stdlib.rule

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.rule.RuleWrapper

/**
 * ISO cut `'!'/0`. This [RuleWrapper] only registers the `!/0` signature as a defined predicate (its default,
 * unused, body is just `true`): `StateRuleSelection` recognises `!` via its own `isCut()` check before it ever
 * reaches ordinary rule resolution, and performs the actual pruning of the choice-point queue directly.
 */
object Cut : RuleWrapper<ExecutionContext>("!", 0)
