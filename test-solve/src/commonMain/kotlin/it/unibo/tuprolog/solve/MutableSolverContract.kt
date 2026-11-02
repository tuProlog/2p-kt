package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.flags.Unknown
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.theory.Theory
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

private fun p(n: Int): Struct = Struct.of("p", Integer.of(n))

private fun ps(vararg ns: Int): List<Term> = ns.map { Integer.of(it) }

/** The arguments of the theory's `p/1` clauses, in order. */
private fun Theory.ps(): List<Term> = clauses.mapNotNull { it.head?.takeIf { head -> head.functor == "p" }?.get(0) }

/** What every [MutableSolver] built by [solverFactory] lets one change in place. */
fun mutableSolverContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    fun solver(): MutableSolver = solverFactory.mutableSolverWithDefaultBuiltins()

    scenario("mutable-solver/libraries") {
        val solver = solver()
        val custom = Library.of("custom")
        solver.loadLibrary(custom)
        assertTrue("custom" in solver.libraries)
        solver.unloadLibrary(custom)
        assertFalse("custom" in solver.libraries)
        solver.setRuntime(solver.libraries + custom)
        assertTrue("custom" in solver.libraries)
    }

    scenario("mutable-solver/static-kb") {
        val solver = solver()
        solver.loadStaticKb(Theory.of(Fact.of(p(1))))
        assertEquals(ps(1), solver.staticKb.ps())
        solver.appendStaticKb(Theory.of(Fact.of(p(2))))
        assertEquals(ps(1, 2), solver.staticKb.ps())
        solver.loadStaticKb(Theory.of(Fact.of(p(3))))
        assertEquals(ps(3), solver.staticKb.ps())
        solver.resetStaticKb()
        assertTrue(solver.staticKb.isEmpty)
    }

    scenario("mutable-solver/dynamic-kb") {
        val solver = solver()
        solver.loadDynamicKb(Theory.of(Fact.of(p(1))))
        assertEquals(ps(1), solver.dynamicKb.ps())
        solver.appendDynamicKb(Theory.of(Fact.of(p(2))))
        assertEquals(ps(1, 2), solver.dynamicKb.ps())
        solver.resetDynamicKb()
        assertTrue(solver.dynamicKb.isEmpty)
    }

    scenario("mutable-solver/assert") {
        val solver = solver()
        solver.assertZ(p(2))
        solver.assertZ(Fact.of(p(3)))
        solver.assertA(p(1))
        solver.assertA(Fact.of(p(0)))
        assertEquals(ps(0, 1, 2, 3), solver.dynamicKb.ps())
    }

    scenario("mutable-solver/retract") {
        val solver = solver()
        listOf(1, 2, 3, 4).forEach { solver.assertZ(p(it)) }
        assertTrue(solver.retract(p(1)) is it.unibo.tuprolog.theory.RetractResult.Success)
        solver.retract(Fact.of(p(2)))
        assertEquals(ps(3, 4), solver.dynamicKb.ps())
        solver.retractAll(Fact.of(Struct.of("p", Var.anonymous())))
        assertEquals(ps(), solver.dynamicKb.ps())
        listOf(5, 6).forEach { solver.assertZ(p(it)) }
        solver.retractAll(Struct.of("p", Var.anonymous()))
        assertEquals(ps(), solver.dynamicKb.ps())
    }

    scenario("mutable-solver/flags") {
        val solver = solver()
        solver.setFlag(Unknown.name, Unknown.FAIL)
        assertEquals(Unknown.FAIL, solver.flags[Unknown])
        solver.setFlag(Unknown.name to Unknown.WARNING)
        assertEquals(Unknown.WARNING, solver.flags[Unknown])
        solver.setFlag(Unknown)
        assertEquals(Unknown.defaultValue, solver.flags[Unknown])
    }

    scenario("mutable-solver/channels") {
        val solver = solver()
        val output = OutputChannel.of<String> { }
        val error = OutputChannel.of<String> { }
        val warnings = OutputChannel.of<Warning> { }
        val input = InputChannel.of("x")
        solver.setStandardOutput(output)
        solver.setStandardError(error)
        solver.setWarnings(warnings)
        solver.setStandardInput(input)
        assertSame(output, solver.standardOutput)
        assertSame(error, solver.standardError)
        assertSame(warnings, solver.warnings)
        assertSame(input, solver.standardInput)
    }

    scenario("mutable-solver/copies-are-independent") {
        val solver = solver()
        solver.assertZ(p(1))
        val copy = solver.copy()
        val clone = solver.clone()
        solver.assertZ(p(2))
        assertEquals(ps(1, 2), solver.dynamicKb.ps())
        assertEquals(ps(1), copy.dynamicKb.ps())
        assertEquals(ps(1), clone.dynamicKb.ps())
    }
}
