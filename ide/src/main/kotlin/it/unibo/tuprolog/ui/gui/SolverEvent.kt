package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.ExecutionContextAware
import it.unibo.tuprolog.solve.FlagStore
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.Theory

data class SolverEvent<T>(
    val event: T,
    override val operators: OperatorSet,
    override val libraries: Libraries,
    override val flags: FlagStore,
    override val staticKb: Theory,
    override val dynamicKb: Theory,
    override val inputChannels: InputStore,
    override val outputChannels: OutputStore
) : ExecutionContextAware {
    constructor(event: T, other: ExecutionContextAware) :
        this(
            event = event,
            dynamicKb = other.dynamicKb,
            flags = other.flags,
            inputChannels = other.inputChannels,
            libraries = other.libraries,
            operators = other.operators,
            outputChannels = other.outputChannels,
            staticKb = other.staticKb
        )
}
