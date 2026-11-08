package it.unibo.tuprolog.solve.prolog

import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator

/**
 * [PrologSolverFactory]'s solvers, running the stateful machine of #1010 instead of the FSM. Internal until it becomes
 * the default; the solvers it makes, and the solvers those create (copies, sub-solvers), all use the machine.
 */
internal object MachinePrologSolverFactory : SolverFactory {
    override val defaultBuiltins: Library
        get() = PrologSolverFactory.defaultBuiltins

    private fun <S : Solver> S.onMachine(): S = also { (it as AbstractPrologSolver).engine = Engine.MACHINE }

    override fun solverOf(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        inputs: InputStore,
        outputs: OutputStore,
    ): Solver =
        PrologSolverFactory.solverOf(unificator, libraries, flags, staticKb, dynamicKb, inputs, outputs).onMachine()

    override fun solverOf(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        stdIn: InputChannel<String>,
        stdOut: OutputChannel<String>,
        stdErr: OutputChannel<String>,
        warnings: OutputChannel<Warning>,
    ): Solver =
        PrologSolverFactory
            .solverOf(unificator, libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)
            .onMachine()

    override fun mutableSolverOf(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        stdIn: InputChannel<String>,
        stdOut: OutputChannel<String>,
        stdErr: OutputChannel<String>,
        warnings: OutputChannel<Warning>,
    ): MutableSolver =
        PrologSolverFactory
            .mutableSolverOf(unificator, libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)
            .onMachine()

    override fun mutableSolverOf(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        inputs: InputStore,
        outputs: OutputStore,
    ): MutableSolver =
        PrologSolverFactory
            .mutableSolverOf(
                unificator,
                libraries,
                flags,
                staticKb,
                dynamicKb,
                inputs,
                outputs,
            ).onMachine()
}
