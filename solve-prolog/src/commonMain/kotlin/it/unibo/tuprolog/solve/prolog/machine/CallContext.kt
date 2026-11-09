package it.unibo.tuprolog.solve.prolog.machine

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.prolog.AbstractPrologSolver
import it.unibo.tuprolog.solve.prolog.Engine
import it.unibo.tuprolog.solve.prolog.PrologExecutionContext
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator

/**
 * The context of a call made by a [PrologMachine]: the solver's [world] state, plus the call's [procedure], the
 * resolution's bindings as a [substitution], and the [logicStackTrace]. Both are computed on first access and kept, so
 * they read the same afterwards. The machine [pin]s a context that may be read after its bindings change (errors,
 * warnings, interceptors); a primitive's request context is read while they still hold, or after backtracking has
 * restored them.
 */
internal class CallContext(
    private val world: PrologExecutionContext,
    override val procedure: Struct?,
    substitution: () -> Substitution.Unifier,
    logicStackTrace: () -> List<Struct>,
) : ExecutionContext by world {
    override val substitution: Substitution.Unifier by lazy(substitution)

    override val logicStackTrace: List<Struct> by lazy(logicStackTrace)

    /** This context, with its [substitution] and [logicStackTrace] computed now. */
    fun pin(): CallContext = also { substitution.size + logicStackTrace.size }

    override fun createSolver(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        inputChannels: InputStore,
        outputChannels: OutputStore,
    ): Solver =
        world
            .createSolver(unificator, libraries, flags, staticKb, dynamicKb, inputChannels, outputChannels)
            .also { (it as AbstractPrologSolver).engine = Engine.MACHINE }

    override fun createMutableSolver(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        inputChannels: InputStore,
        outputChannels: OutputStore,
    ): MutableSolver =
        world
            .createMutableSolver(unificator, libraries, flags, staticKb, dynamicKb, inputChannels, outputChannels)
            .also { (it as AbstractPrologSolver).engine = Engine.MACHINE }

    override fun toString(): String = "CallContext(procedure=$procedure)"
}
