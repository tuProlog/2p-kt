package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlin.test.Test

class TestMutableSolver : SolverFactory {

    val prototype = SolverTestPrototype(this)

    override fun solverOf(libraries: Libraries, flags: Map<Atom, Term>, staticKB: ClauseDatabase, dynamicKB: ClauseDatabase): Solver =
            MutableSolver(libraries, flags, staticKB, dynamicKB)


    @Test
    fun testConjunction() {
        prototype.testConjunction()
    }

    @Test
    fun testConjunctionWithUnification() {
        prototype.testConjunctionWithUnification()
    }
}