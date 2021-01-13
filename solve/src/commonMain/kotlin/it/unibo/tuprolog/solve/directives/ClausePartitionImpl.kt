package it.unibo.tuprolog.solve.directives

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.Theory

internal data class ClausePartitionImpl(
    override val staticClauses: Theory = Theory.emptyIndexed(),
    override val dynamicClauses: Theory = MutableTheory.emptyIndexed(),
    override val operators: OperatorSet = OperatorSet.EMPTY,
    override val initialGoals: List<Struct> = emptyList(),
    override val includes: List<Atom> = emptyList(),
    override val flagStore: FlagStore = FlagStore.empty()
) : ClausePartition
