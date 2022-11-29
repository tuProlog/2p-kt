package it.unibo.tuprolog.solve.directives

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName

interface ClausePartition {
    @JsName("staticClauses")
    val staticClauses: Theory

    @JsName("dynamicClauses")
    val dynamicClauses: Theory

    @JsName("operators")
    val operators: OperatorSet

    @JsName("initialGoals")
    val initialGoals: List<Struct>

    @JsName("includes")
    val includes: List<Atom>

    @JsName("flagStore")
    val flagStore: FlagStore

    @JsName("plus")
    operator fun plus(other: ClausePartition): ClausePartition =
        ClausePartitionImpl(
            staticClauses + other.staticClauses,
            (dynamicClauses + other.dynamicClauses).toImmutableTheory(),
            operators + other.operators,
            initialGoals + other.initialGoals,
            includes + other.includes,
            flagStore + other.flagStore
        )

    companion object {
        @JsName("of")
        fun of(
            unificator: Unificator,
            staticClauses: Theory? = null,
            dynamicClauses: Theory? = null,
            operators: OperatorSet? = null,
            initialGoals: List<Struct>? = null,
            includes: List<Atom>? = null,
            flagStore: FlagStore? = null,
        ): ClausePartition = ClausePartitionImpl(
            staticClauses ?: Theory.emptyIndexed(unificator),
            dynamicClauses ?: MutableTheory.emptyIndexed(unificator),
            operators ?: OperatorSet.EMPTY,
            initialGoals ?: emptyList(),
            includes ?: emptyList(),
            flagStore ?: FlagStore.empty(),
        )
    }
}
