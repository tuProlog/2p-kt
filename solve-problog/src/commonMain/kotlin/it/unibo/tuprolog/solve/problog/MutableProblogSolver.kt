package it.unibo.tuprolog.solve.problog

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.flags.NotableFlag
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProblogTheory
import it.unibo.tuprolog.theory.RetractResult
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator

internal class MutableProblogSolver(
    private val solver: MutableSolver
) : ProblogSolver(solver), MutableSolver {

    override fun loadLibrary(library: Library) {
        solver.loadLibrary(library)
    }

    override fun unloadLibrary(library: Library) {
        solver.unloadLibrary(library)
    }

    override fun setRuntime(libraries: Runtime) {
        solver.setRuntime(libraries)
    }

    override fun loadStaticKb(theory: Theory) {
        solver.loadStaticKb(ProblogTheory.of(solver.unificator, theory))
    }

    override fun appendStaticKb(theory: Theory) {
        solver.appendStaticKb(ProblogTheory.of(solver.unificator, theory))
    }

    override fun resetStaticKb() {
        solver.resetStaticKb()
    }

    override fun loadDynamicKb(theory: Theory) {
        solver.loadDynamicKb(ProblogTheory.of(solver.unificator, theory))
    }

    override fun appendDynamicKb(theory: Theory) {
        solver.appendDynamicKb(ProblogTheory.of(solver.unificator, theory))
    }

    override fun resetDynamicKb() {
        solver.resetDynamicKb()
    }

    override fun assertA(clause: Clause) {
        solver.assertA(ProblogTheory.of(solver.unificator, clause).clauses.first())
    }

    override fun assertA(fact: Struct) {
        solver.assertA(ProblogTheory.of(solver.unificator, Fact.of(fact)).clauses.first())
    }

    override fun assertZ(clause: Clause) {
        solver.assertZ(ProblogTheory.of(solver.unificator, clause).clauses.first())
    }

    override fun assertZ(fact: Struct) {
        solver.assertA(ProblogTheory.of(solver.unificator, Fact.of(fact)).clauses.first())
    }

    override fun retract(clause: Clause): RetractResult<Theory> {
        return solver.retract(ProblogTheory.of(solver.unificator, clause).clauses.first())
    }

    override fun retract(fact: Struct): RetractResult<Theory> {
        return solver.retract(ProblogTheory.of(solver.unificator, Fact.of(fact)).clauses.first())
    }

    override fun retractAll(clause: Clause): RetractResult<Theory> {
        return solver.retractAll(ProblogTheory.of(solver.unificator, clause).clauses.first())
    }

    override fun retractAll(fact: Struct): RetractResult<Theory> {
        return solver.retractAll(ProblogTheory.of(solver.unificator, Fact.of(fact)).clauses.first())
    }

    override fun setFlag(name: String, value: Term) {
        solver.setFlag(name, value)
    }

    override fun setFlag(flag: Pair<String, Term>) {
        solver.setFlag(flag)
    }

    override fun setFlag(flag: NotableFlag) {
        solver.setFlag(flag)
    }

    override fun setStandardInput(stdIn: InputChannel<String>) {
        solver.setStandardInput(stdIn)
    }

    override fun setStandardError(stdErr: OutputChannel<String>) {
        solver.setStandardError(stdErr)
    }

    override fun setStandardOutput(stdOut: OutputChannel<String>) {
        solver.setStandardOutput(stdOut)
    }

    override fun setWarnings(warnings: OutputChannel<Warning>) {
        solver.setWarnings(warnings)
    }

    override fun copy(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        stdIn: InputChannel<String>,
        stdOut: OutputChannel<String>,
        stdErr: OutputChannel<String>,
        warnings: OutputChannel<Warning>
    ): MutableSolver {
        return MutableProblogSolver(
            solver.copy(unificator, libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)
        )
    }

    override fun clone(): MutableSolver {
        return MutableProblogSolver(solver.clone())
    }
}
