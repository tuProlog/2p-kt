package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.*
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.stdlib.DefaultBuiltins
import it.unibo.tuprolog.theory.Theory
import kotlin.test.Test

class ClassicSolverTestNonVar : TestNonVar, SolverFactory {

    private val prototype = TestNonVar.prototype(this)

    override val defaultBuiltins: AliasedLibrary
        get() = DefaultBuiltins

    override fun solverOf(
            libraries: Libraries,
            flags: PrologFlags,
            staticKb: Theory,
            dynamicKb: Theory,
            stdIn: InputChannel<String>,
            stdOut: OutputChannel<String>,
            stdErr: OutputChannel<String>,
            warnings: OutputChannel<PrologWarning>
    ) = Solver.classic(
            libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings
    )

    override fun mutableSolverOf(
            libraries: Libraries,
            flags: PrologFlags,
            staticKb: Theory,
            dynamicKb: Theory,
            stdIn: InputChannel<String>,
            stdOut: OutputChannel<String>,
            stdErr: OutputChannel<String>,
            warnings: OutputChannel<PrologWarning>
    ) = MutableSolver.classic(
            libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings
    )

    @Test
    override fun testNonVarNumber() {
        prototype.testNonVarNumber()
    }

    @Test
    override fun testNonVarFoo() {
        prototype.testNonVarFoo()
    }

    @Test
    override fun testNonVarFooCl() {
        prototype.testNonVarFooCl()
    }

    @Test
    override fun testNonVarFooAssignment() {
        prototype.testNonVarFooAssignment()
    }

    @Test
    override fun testNonVarAnyTerm() {
        prototype.testNonVarAnyTerm()
    }

    @Test
    override fun testNonVar() {
        prototype.testNonVar()
    }
}