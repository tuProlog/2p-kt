package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.ExecutionContextAware
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator

/**
 * The payload published by every `onXxx` [org.reactfx.EventStream] of [TuPrologIDEModel]: it couples a
 * domain-specific [event] (e.g. a [it.unibo.tuprolog.solve.Solution], a resolution counter, the current goal)
 * with a snapshot of the solver's [ExecutionContextAware] state ([unificator], [operators], [libraries], [flags],
 * [staticKb], [dynamicKb], [inputChannels], [outputChannels]) taken at the time the event fired.
 *
 * Bundling the context together with the event lets subscribers (namely [TuPrologIDEController]) refresh
 * context-sensitive views (the operators table, the flags table, the loaded libraries tree, the static/dynamic
 * knowledge base panes) only when something actually changed, by comparing consecutive [SolverEvent]s.
 *
 * @param T the type of the wrapped domain event.
 */
data class SolverEvent<T>(
    val event: T,
    override val unificator: Unificator,
    override val operators: OperatorSet,
    override val libraries: Runtime,
    override val flags: FlagStore,
    override val staticKb: Theory,
    override val dynamicKb: Theory,
    override val inputChannels: InputStore,
    override val outputChannels: OutputStore,
) : ExecutionContextAware {
    /**
     * Builds a [SolverEvent] wrapping [event], copying the [it.unibo.tuprolog.solve.ExecutionContext] fields
     * from [other] (typically the solver instance itself); [staticKb] and [dynamicKb] are snapshotted into
     * immutable theories so that later mutations of the live solver do not retroactively change this event.
     */
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
            staticKb = other.staticKb.toImmutableTheory(),
        )
}
