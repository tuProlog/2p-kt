package it.unibo.tuprolog.solve.directives

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.FlagStore
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.Theory
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
            Theory.indexedOf(sequenceOf(this, other).flatMap { it.staticClauses.asSequence() }),
            MutableTheory.indexedOf(sequenceOf(this, other).flatMap { it.dynamicClauses.asSequence() }),
            sequenceOf(this, other).map { it.operators }.reduce(OperatorSet::plus),
            listOf(this, other).flatMap { it.initialGoals },
            listOf(this, other).flatMap { it.includes },
            sequenceOf(this, other).map { it.flagStore }.reduce(FlagStore::plus)
        )
}
