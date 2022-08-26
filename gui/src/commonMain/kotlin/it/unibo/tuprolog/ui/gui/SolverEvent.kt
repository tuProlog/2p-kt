package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.ExecutionContextAware
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator

data class SolverEvent<T>(
    val event: T,
    override val unificator: Unificator,
    override val operators: OperatorSet,
    override val libraries: Runtime,
    override val flags: FlagStore,
    override val staticKb: Theory,
    override val dynamicKb: Theory,
    override val inputChannels: InputStore,
    override val outputChannels: OutputStore
) : ExecutionContextAware {
    constructor(event: T, other: ExecutionContextAware) :
        this(
            event = event,
            unificator = other.unificator,
            dynamicKb = other.dynamicKb.toImmutableTheory(),
            flags = other.flags,
            inputChannels = other.inputChannels,
            libraries = other.libraries,
            operators = other.operators,
            outputChannels = other.outputChannels,
            staticKb = other.staticKb.toImmutableTheory()
        )
}
