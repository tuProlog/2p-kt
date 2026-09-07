package it.unibo.tuprolog.solve.directives

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName

/**
 * The result of splitting a collection of [Clause]s (typically a whole [Theory]) into the pieces of state a
 * [it.unibo.tuprolog.solve.Solver] must be seeded with when loading it: plain clauses bound for the static/dynamic
 * knowledge bases, plus everything expressed via directives (`:- Goal` facts) -- declared [operators], flags
 * ([flagStore]), goals to run at load time ([initialGoals]), and other theories to load ([includes]).
 *
 * Produced by [ClausePartitioner] (see [it.unibo.tuprolog.solve.directives.partition] extension functions), and
 * consumed by `AbstractSolver.initializeKb` (see [it.unibo.tuprolog.solve.impl.AbstractSolver]) when a knowledge
 * base is loaded into a solver.
 *
 * @see DirectiveSelector
 */
interface ClausePartition {
    /** Clauses bound for the static knowledge base (see [it.unibo.tuprolog.solve.ExecutionContextAware.staticKb]). */
    @JsName("staticClauses")
    val staticClauses: Theory

    /** Clauses bound for the dynamic knowledge base (see [it.unibo.tuprolog.solve.ExecutionContextAware.dynamicKb]). */
    @JsName("dynamicClauses")
    val dynamicClauses: Theory

    /** Operators declared via `:- op(Priority, Specifier, Name)` directives. */
    @JsName("operators")
    val operators: OperatorSet

    /** Goals to be solved once, at load time, declared via `:- initialization(Goal)`/`:- solve(Goal)` directives. */
    @JsName("initialGoals")
    val initialGoals: List<Struct>

    /** Names of other theories to be loaded, declared via `:- include(Name)`/`:- load(Name)` directives. */
    @JsName("includes")
    val includes: List<Atom>

    /** Flags set via `:- set_flag(Name, Value)`/`:- set_prolog_flag(Name, Value)` directives. */
    @JsName("flagStore")
    val flagStore: FlagStore

    /** Merges this partition with [other], concatenating/summing each corresponding component. */
    @JsName("plus")
    operator fun plus(other: ClausePartition): ClausePartition =
        ClausePartitionImpl(
            staticClauses + other.staticClauses,
            (dynamicClauses + other.dynamicClauses).toImmutableTheory(),
            operators + other.operators,
            initialGoals + other.initialGoals,
            includes + other.includes,
            flagStore + other.flagStore,
        )

    companion object {
        /** Creates a [ClausePartition] out of explicit components, defaulting unspecified ones to empty. */
        @JsName("of")
        fun of(
            unificator: Unificator,
            staticClauses: Theory? = null,
            dynamicClauses: Theory? = null,
            operators: OperatorSet? = null,
            initialGoals: List<Struct>? = null,
            includes: List<Atom>? = null,
            flagStore: FlagStore? = null,
        ): ClausePartition =
            ClausePartitionImpl(
                staticClauses ?: Theory.emptyIndexed(unificator),
                dynamicClauses ?: MutableTheory.emptyIndexed(unificator),
                operators ?: OperatorSet.EMPTY,
                initialGoals ?: emptyList(),
                includes ?: emptyList(),
                flagStore ?: FlagStore.empty(),
            )
    }
}
