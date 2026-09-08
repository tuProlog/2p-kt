package it.unibo.tuprolog.solve.streams

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext

/**
 * The pluggable choice points of `:solve-streams`' resolution engine: given a `Sequence` of candidates, which
 * one does the solver commit to first, and when is a goal considered proven?
 *
 * Every `StreamsExecutionContext` carries one [SolverStrategies] instance (defaulting to [prologStandard]), and
 * the internal state-machine states (`StateGoalEvaluation`, `StateRuleSelection`, ...) consult it -- together
 * with [it.unibo.tuprolog.solve.streams.solver.orderWithStrategy] -- whenever they need to order candidate
 * predications/clauses or decide whether a term counts as a successful demonstration.
 *
 * Because `:solve-streams` currently exposes no public constructor parameter (on [StreamsSolverFactory] or
 * elsewhere) to inject a custom [SolverStrategies] into a [it.unibo.tuprolog.solve.Solver] instance, this
 * interface is, at present, effectively fixed to [prologStandard] from outside this module.
 *
 * @author Enrico
 */
interface SolverStrategies {
    /**
     * Selects, from [predicationSequence], the predication that should be solved first.
     *
     * Called with the full, not-yet-fully-consumed sequence of pending predications available for selection in
     * [context]; the standard behaviour ([prologStandard]) picks [Sequence.first], honouring Prolog's
     * left-to-right resolution order.
     */
    fun <P : Term> predicationChoiceStrategy(
        predicationSequence: Sequence<P>,
        context: ExecutionContext,
    ): P

    /**
     * Selects, from [unifiableClauses], the clause that should be expanded in place of the current predication.
     *
     * Called with the sequence of clauses whose head already unifies with the predication being solved, in
     * [context]; the standard behaviour ([prologStandard]) picks [Sequence.first], honouring the textual order
     * clauses appear in the knowledge base (leaving the rest available for backtracking).
     */
    fun <C : Clause> clauseChoiceStrategy(
        unifiableClauses: Sequence<C>,
        context: ExecutionContext,
    ): C

    /** Determines whether [term] -- the result of a resolution step in [context] -- counts as a successful demonstration. */
    fun successCheckStrategy(
        term: Term,
        context: ExecutionContext,
    ): Boolean

    companion object {
        /**
         * The [SolverStrategies] implementing the ISO/Standard-Prolog behaviour: leftmost predication first,
         * clauses tried in their knowledge-base order, and success recognized exactly when the resulting term is
         * [Term.isTrue].
         *
         * This is the only [SolverStrategies] instance `:solve-streams` currently wires up (see
         * `StreamsExecutionContext`'s default `solverStrategies` value).
         */
        val prologStandard =
            object : SolverStrategies {
                override fun <P : Term> predicationChoiceStrategy(
                    predicationSequence: Sequence<P>,
                    context: ExecutionContext,
                ): P = predicationSequence.first()

                override fun <C : Clause> clauseChoiceStrategy(
                    unifiableClauses: Sequence<C>,
                    context: ExecutionContext,
                ): C = unifiableClauses.first()

                override fun successCheckStrategy(
                    term: Term,
                    context: ExecutionContext,
                ): Boolean = term.isTrue
            }
    }
}
