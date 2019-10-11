package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.dsl.theory.PrologWithTheories
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.theory.ClauseDatabase

interface SolverFactory {

    val defaultLibraries: Libraries
        get() = Libraries()

    val defaultFlags: Map<Atom, Term>
        get() = mapOf()

    val defaultStaticKB: ClauseDatabase
        get() = ClauseDatabase.empty()

    val defaultDynamicKB: ClauseDatabase
        get() = ClauseDatabase.empty()

    fun solverOf(libraries: Libraries = defaultLibraries,
                  flags: Map<Atom, Term> = defaultFlags,
                  staticKB: ClauseDatabase = defaultStaticKB,
                  dynamicKB: ClauseDatabase = defaultDynamicKB): Solver

    fun Solver.solveGoal(maxDuration: TimeDuration = TimeDuration.MAX_VALUE, scopedContext: PrologWithTheories.() -> Any): Sequence<Solution> =
            with(PrologWithTheories.empty()) {
                val rawGoal = this.scopedContext()
                when (val g = rawGoal.toTerm()) {
                    is Struct -> solve(g, maxDuration)
                    else -> throw IllegalArgumentException("Cannot convert $rawGoal into a struct")
                }
            }

}