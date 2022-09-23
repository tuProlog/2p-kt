package it.unibo.tuprolog.ui.gui.impl

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.ui.gui.SolverEvent
import it.unibo.tuprolog.unify.Unificator

internal data class SolverEventImpl<T>(
    override val name: String,
    override val event: T,
    override val unificator: Unificator,
    override val operators: OperatorSet,
    override val libraries: Runtime,
    override val flags: FlagStore,
    override val staticKb: Theory,
    override val dynamicKb: Theory,
    override val inputChannels: InputStore,
    override val outputChannels: OutputStore
) : SolverEvent<T>
