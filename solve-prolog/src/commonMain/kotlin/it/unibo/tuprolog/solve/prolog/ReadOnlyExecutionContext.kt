package it.unibo.tuprolog.solve.prolog

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.sideffects.SideEffect
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator

/**
 * The view of [context] that primitives and functions get in their requests (#1010): its knowledge bases are
 * [ReadOnlyTheory]s, and [apply]/[update] derive new contexts without touching [context]'s. Sub-solvers it creates
 * share [context]'s knowledge bases unless given others, as they do when created from [context] directly.
 */
internal class ReadOnlyExecutionContext(
    private val context: ExecutionContext,
) : ExecutionContext by context {
    override val staticKb: Theory = context.staticKb.readOnly()

    override val dynamicKb: Theory = context.dynamicKb.readOnly()

    override fun createSolver(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        inputChannels: InputStore,
        outputChannels: OutputStore,
    ): Solver =
        context.createSolver(
            unificator,
            libraries,
            flags,
            staticKb.unwrap(),
            dynamicKb.unwrap(),
            inputChannels,
            outputChannels,
        )

    override fun createMutableSolver(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        inputChannels: InputStore,
        outputChannels: OutputStore,
    ): MutableSolver =
        context.createMutableSolver(
            unificator,
            libraries,
            flags,
            staticKb.unwrap(),
            dynamicKb.unwrap(),
            inputChannels,
            outputChannels,
        )

    // through this view, so side effects on the knowledge bases write copies, not the engine's
    override fun apply(sideEffect: SideEffect): ExecutionContext = super<ExecutionContext>.apply(sideEffect)

    override fun apply(sideEffects: Iterable<SideEffect>): ExecutionContext = super<ExecutionContext>.apply(sideEffects)

    override fun apply(sideEffects: Sequence<SideEffect>): ExecutionContext = super<ExecutionContext>.apply(sideEffects)

    override fun equals(other: Any?): Boolean = other is ReadOnlyExecutionContext && context == other.context

    override fun hashCode(): Int = context.hashCode()

    override fun toString(): String = "ReadOnlyExecutionContext($context)"
}
