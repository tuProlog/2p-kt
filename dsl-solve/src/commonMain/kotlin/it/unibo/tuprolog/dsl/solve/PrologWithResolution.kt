package it.unibo.tuprolog.dsl.solve

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.dsl.theory.PrologWithTheories
import it.unibo.tuprolog.solve.ClassicSolverFactory
import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.PrologFlags
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.Theory
import kotlin.js.JsName

interface PrologWithResolution : PrologWithTheories, MutableSolver {
    @JsName("solverFactory")
    val solverFactory: SolverFactory

    @JsName("defaultSolver")
    val defaultSolver: MutableSolver

    @JsName("solverOf")
    fun solverOf(
        otherLibraries: Libraries = solverFactory.defaultLibraries,
        flags: PrologFlags = solverFactory.defaultFlags,
        staticKb: Theory = solverFactory.defaultStaticKb,
        dynamicKb: Theory = solverFactory.defaultDynamicKb,
        stdIn: InputChannel<String> = solverFactory.defaultInputChannel,
        stdOut: OutputChannel<String> = solverFactory.defaultOutputChannel,
        stdErr: OutputChannel<String> = solverFactory.defaultErrorChannel,
        warnings: OutputChannel<PrologWarning> = solverFactory.defaultWarningsChannel
    ): MutableSolver = solverFactory.mutableSolverWithDefaultBuiltins(
        otherLibraries,
        flags,
        staticKb,
        dynamicKb,
        stdIn,
        stdOut,
        stdErr,
        warnings
    )

    @JsName("staticKb")
    fun staticKb(vararg clauses: Clause) = loadStaticClauses(*clauses)

    @JsName("staticKbIterable")
    fun staticKb(clauses: Iterable<Clause>) = loadStaticClauses(clauses)

    @JsName("staticKbSequence")
    fun staticKb(clauses: Sequence<Clause>) = loadStaticClauses(clauses)

    @JsName("staticKbTheory")
    fun staticKb(theory: Theory) = loadStaticKb(theory)

    @JsName("dynamicKb")
    fun dynamicKb(vararg clauses: Clause) = loadDynamicClauses(*clauses)

    @JsName("dynamicKbIterable")
    fun dynamicKb(clauses: Iterable<Clause>) = loadDynamicClauses(clauses)

    @JsName("dynamicKbSequence")
    fun dynamicKb(clauses: Sequence<Clause>) = loadDynamicClauses(clauses)

    @JsName("dynamicKbTheory")
    fun dynamicKb(theory: Theory) = loadDynamicKb(theory)

    companion object {
        @JsName("of")
        fun of(solverFactory: SolverFactory): PrologWithResolution = PrologWithResolutionImpl(solverFactory)

        @JsName("empty")
        fun empty(): PrologWithResolution = of(ClassicSolverFactory)
    }
}