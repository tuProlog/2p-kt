package it.unibo.tuprolog.dsl.solve

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.dsl.unify.LogicProgrammingScopeWithUnificator
import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName

interface LogicProgrammingScopeWithResolution<S : LogicProgrammingScopeWithResolution<S>> :
    LogicProgrammingScopeWithUnificator<S>,
    MutableSolver {
    @JsName("solverFactory")
    val solverFactory: SolverFactory

    @JsName("defaultSolver")
    val defaultSolver: MutableSolver

    @JsName("solverOf")
    fun solverOf(
        unificator: Unificator = this.unificator,
        otherLibraries: Runtime = solverFactory.defaultRuntime,
        flags: FlagStore = solverFactory.defaultFlags,
        staticKb: Theory = solverFactory.defaultStaticKb,
        dynamicKb: Theory = solverFactory.defaultDynamicKb,
        stdIn: InputChannel<String> = solverFactory.defaultInputChannel,
        stdOut: OutputChannel<String> = solverFactory.defaultOutputChannel,
        stdErr: OutputChannel<String> = solverFactory.defaultErrorChannel,
        warnings: OutputChannel<Warning> = solverFactory.defaultWarningsChannel,
    ): MutableSolver =
        solverFactory.mutableSolverWithDefaultBuiltins(
            unificator,
            otherLibraries,
            flags,
            staticKb,
            dynamicKb,
            stdIn,
            stdOut,
            stdErr,
            warnings,
        )

    @JsName("staticKbByArray")
    fun staticKb(vararg clauses: Clause) = defaultSolver.loadStaticClauses(*clauses)

    @JsName("staticKbByIterable")
    fun staticKb(clauses: Iterable<Clause>) = defaultSolver.loadStaticClauses(clauses)

    @JsName("staticKbBySequence")
    fun staticKb(clauses: Sequence<Clause>) = defaultSolver.loadStaticClauses(clauses)

    @JsName("staticKbByTheory")
    fun staticKb(theory: Theory) = defaultSolver.loadStaticKb(theory)

    @JsName("dynamicKbByArray")
    fun dynamicKb(vararg clauses: Clause) = defaultSolver.loadDynamicClauses(*clauses)

    @JsName("dynamicKbByIterable")
    fun dynamicKb(clauses: Iterable<Clause>) = defaultSolver.loadDynamicClauses(clauses)

    @JsName("dynamicKbBySequence")
    fun dynamicKb(clauses: Sequence<Clause>) = defaultSolver.loadDynamicClauses(clauses)

    @JsName("dynamicKbByTheory")
    fun dynamicKb(theory: Theory) = defaultSolver.loadDynamicKb(theory)
}
