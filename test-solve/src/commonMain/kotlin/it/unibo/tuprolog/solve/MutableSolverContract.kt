package it.unibo.tuprolog.solve

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs
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
        ("custom" in solver.libraries).shouldBeTrue()
        solver.unloadLibrary(custom)
        ("custom" in solver.libraries).shouldBeFalse()
        solver.setRuntime(solver.libraries + custom)
        ("custom" in solver.libraries).shouldBeTrue()
    }

    scenario("mutable-solver/static-kb") {
        val solver = solver()
        solver.loadStaticKb(Theory.of(Fact.of(p(1))))
        solver.staticKb.ps() shouldBe ps(1)
        solver.appendStaticKb(Theory.of(Fact.of(p(2))))
        solver.staticKb.ps() shouldBe ps(1, 2)
        solver.loadStaticKb(Theory.of(Fact.of(p(3))))
        solver.staticKb.ps() shouldBe ps(3)
        solver.resetStaticKb()
        solver.staticKb.isEmpty.shouldBeTrue()
    }

    scenario("mutable-solver/dynamic-kb") {
        val solver = solver()
        solver.loadDynamicKb(Theory.of(Fact.of(p(1))))
        solver.dynamicKb.ps() shouldBe ps(1)
        solver.appendDynamicKb(Theory.of(Fact.of(p(2))))
        solver.dynamicKb.ps() shouldBe ps(1, 2)
        solver.resetDynamicKb()
        solver.dynamicKb.isEmpty.shouldBeTrue()
    }

    scenario("mutable-solver/assert") {
        val solver = solver()
        solver.assertZ(p(2))
        solver.assertZ(Fact.of(p(3)))
        solver.assertA(p(1))
        solver.assertA(Fact.of(p(0)))
        solver.dynamicKb.ps() shouldBe ps(0, 1, 2, 3)
    }

    scenario("mutable-solver/retract") {
        val solver = solver()
        listOf(1, 2, 3, 4).forEach { solver.assertZ(p(it)) }
        solver.retract(p(1)).shouldBeInstanceOf<it.unibo.tuprolog.theory.RetractResult.Success<*>>()
        solver.retract(Fact.of(p(2)))
        solver.dynamicKb.ps() shouldBe ps(3, 4)
        solver.retractAll(Fact.of(Struct.of("p", Var.anonymous())))
        solver.dynamicKb.ps() shouldBe ps()
        listOf(5, 6).forEach { solver.assertZ(p(it)) }
        solver.retractAll(Struct.of("p", Var.anonymous()))
        solver.dynamicKb.ps() shouldBe ps()
    }

    scenario("mutable-solver/flags") {
        val solver = solver()
        solver.setFlag(Unknown.name, Unknown.FAIL)
        solver.flags[Unknown] shouldBe Unknown.FAIL
        solver.setFlag(Unknown.name to Unknown.WARNING)
        solver.flags[Unknown] shouldBe Unknown.WARNING
        solver.setFlag(Unknown)
        solver.flags[Unknown] shouldBe Unknown.defaultValue
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
        solver.standardOutput shouldBeSameInstanceAs output
        solver.standardError shouldBeSameInstanceAs error
        solver.warnings shouldBeSameInstanceAs warnings
        solver.standardInput shouldBeSameInstanceAs input
    }

    scenario("mutable-solver/copies-are-independent") {
        val solver = solver()
        solver.assertZ(p(1))
        val copy = solver.copy()
        val clone = solver.clone()
        solver.assertZ(p(2))
        solver.dynamicKb.ps() shouldBe ps(1, 2)
        copy.dynamicKb.ps() shouldBe ps(1)
        clone.dynamicKb.ps() shouldBe ps(1)
    }
}
