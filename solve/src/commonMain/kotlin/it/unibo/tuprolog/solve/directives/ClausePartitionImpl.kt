package it.unibo.tuprolog.solve.directives

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.theory.Theory

internal data class ClausePartitionImpl(
    override val staticClauses: Theory,
    override val dynamicClauses: Theory,
    override val operators: OperatorSet,
    override val initialGoals: List<Struct>,
    override val includes: List<Atom>,
    override val flagStore: FlagStore
) : ClausePartition
