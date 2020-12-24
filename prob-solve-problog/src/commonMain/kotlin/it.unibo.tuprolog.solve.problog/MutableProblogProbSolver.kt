package it.unibo.tuprolog.solve.problog

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.MutableProbSolver
import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.flags.NotableFlag
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProblogTheory
import it.unibo.tuprolog.theory.RetractResult
import it.unibo.tuprolog.theory.Theory

internal class MutableProblogProbSolver(
    private val solver: MutableSolver
) : ProblogProbSolver(solver), MutableProbSolver {

    override fun loadLibrary(library: AliasedLibrary) {
        solver.loadLibrary(library)
    }

    override fun unloadLibrary(library: AliasedLibrary) {
        solver.unloadLibrary(library)
    }

    override fun setLibraries(libraries: Libraries) {
        solver.setLibraries(libraries)
    }

    override fun loadStaticKb(theory: Theory) {
        solver.loadStaticKb(ProblogTheory.of(theory))
    }

    override fun appendStaticKb(theory: Theory) {
        solver.appendStaticKb(ProblogTheory.of(theory))
    }

    override fun resetStaticKb() {
        solver.resetStaticKb()
    }

    override fun loadDynamicKb(theory: Theory) {
        solver.loadDynamicKb(ProblogTheory.of(theory))
    }

    override fun appendDynamicKb(theory: Theory) {
        solver.appendDynamicKb(ProblogTheory.of(theory))
    }

    override fun resetDynamicKb() {
        solver.resetDynamicKb()
    }

    override fun assertA(clause: Clause) {
        solver.assertA(ProblogTheory.of(clause).clauses.first())
    }

    override fun assertA(fact: Struct) {
        solver.assertA(ProblogTheory.of(Fact.of(fact)).clauses.first())
    }

    override fun assertZ(clause: Clause) {
        solver.assertZ(ProblogTheory.of(clause).clauses.first())
    }

    override fun assertZ(fact: Struct) {
        solver.assertA(ProblogTheory.of(Fact.of(fact)).clauses.first())
    }

    override fun retract(clause: Clause): RetractResult<Theory> {
        return solver.retract(ProblogTheory.of(clause).clauses.first())
    }

    override fun retract(fact: Struct): RetractResult<Theory> {
        return solver.retract(ProblogTheory.of(Fact.of(fact)).clauses.first())
    }

    override fun retractAll(clause: Clause): RetractResult<Theory> {
        return solver.retractAll(ProblogTheory.of(clause).clauses.first())
    }

    override fun retractAll(fact: Struct): RetractResult<Theory> {
        return solver.retractAll(ProblogTheory.of(Fact.of(fact)).clauses.first())
    }

    override fun setFlag(name: String, value: Term) {
        setFlag(name, value)
    }

    override fun setFlag(flag: Pair<String, Term>) {
        setFlag(flag)
    }

    override fun setFlag(flag: NotableFlag) {
        setFlag(flag)
    }
}
