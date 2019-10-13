package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.libraries.Library
import it.unibo.tuprolog.libraries.stdlib.Conjunction
import it.unibo.tuprolog.solve.solver.SolverSLD
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlin.test.Test

class TestSolverSLD : SolverFactory {

    val prototype = SolverTestPrototype(this)

    override val defaultLibraries: Libraries
        get() = Libraries(
                Library.of(
                        alias = "prolog.test.conjunction",
                        primitives = mapOf(Conjunction.descriptionPair)
                )
        )

    override fun solverOf(libraries: Libraries, flags: Map<Atom, Term>, staticKB: ClauseDatabase, dynamicKB: ClauseDatabase): Solver =
            SolverSLD(libraries, flags, staticKB, dynamicKB)


    @Test
    fun testConjunction() {
        prototype.testConjunction()
    }

    @Test
    fun testConjunctionWithUnification() {
        prototype.testConjunctionWithUnification()
    }

    @Test
    fun testBuiltinApi() {
        prototype.testBuiltinApi()
    }

    @Test
    fun testDisjunction() {
        prototype.testDisjunction()
    }

    @Test
    fun testFailure() {
        prototype.testFailure()
    }

    @Test
    fun testDisjunctionWithUnification() {
        prototype.testDisjunctionWithUnification()
    }
}
