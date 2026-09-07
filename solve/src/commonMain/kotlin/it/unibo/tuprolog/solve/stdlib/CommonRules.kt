package it.unibo.tuprolog.solve.stdlib

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.rule.RuleWrapper
import it.unibo.tuprolog.solve.stdlib.rule.Append
import it.unibo.tuprolog.solve.stdlib.rule.Arrow
import it.unibo.tuprolog.solve.stdlib.rule.CurrentPrologFlag
import it.unibo.tuprolog.solve.stdlib.rule.Member
import it.unibo.tuprolog.solve.stdlib.rule.Not
import it.unibo.tuprolog.solve.stdlib.rule.Once
import it.unibo.tuprolog.solve.stdlib.rule.Semicolon
import it.unibo.tuprolog.solve.stdlib.rule.SetPrologFlag

/**
 * Registry of every standard control-construct predicate implemented as a Prolog clause rather than a Kotlin
 * [it.unibo.tuprolog.solve.primitive.Primitive] -- e.g. `\+/1` ([it.unibo.tuprolog.solve.stdlib.rule.Not]), `;/2`
 * ([it.unibo.tuprolog.solve.stdlib.rule.Semicolon]), `->/2` ([it.unibo.tuprolog.solve.stdlib.rule.Arrow]),
 * `member/2`, `append/3`, `once/1`, and the `*_prolog_flag/2` predicates -- each implemented as a [RuleWrapper]
 * under `it.unibo.tuprolog.solve.stdlib.rule`.
 *
 * Consumed by [CommonBuiltins] to populate the `prolog.lang` library's [it.unibo.tuprolog.solve.library.Library.clauses].
 */
object CommonRules {
    /** Every standard [RuleWrapper] instance, in declaration order. */
    val wrappers: Sequence<RuleWrapper<ExecutionContext>> =
        sequenceOf(
            Not,
            Arrow,
            Semicolon.If.Then,
            Semicolon.If.Else,
            Semicolon.Or.Left,
            Semicolon.Or.Right,
            Member.Base,
            Member.Recursive,
            Append.Base,
            Append.Recursive,
            Once,
            SetPrologFlag,
            CurrentPrologFlag,
        )

    /** [wrappers]' underlying [Clause]s, as consumed by [it.unibo.tuprolog.solve.library.Library.clauses]. */
    val clauses: List<Clause>
        get() = wrappers.map { it.implementation }.toList()
}
