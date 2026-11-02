package it.unibo.tuprolog.solve.problog

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.parsing.parseAsStruct
import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.flags.Unknown
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.probability
import it.unibo.tuprolog.solve.setProbabilistic
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.theory.parsing.parseAsTheory

private val operators = ProblogSolverFactory.defaultBuiltins.operators

private fun theory(text: String): Theory = text.parseAsTheory(operators)

private fun p(n: Int) = Struct.of("p", Integer.of(n))

private fun solver(): MutableSolver = ProblogSolverFactory.mutableSolverWithDefaultBuiltins()

/** The values of `X` in `p(X)`, in order. */
private fun MutableSolver.ps(): List<Term?> {
    val x = Var.of("X")
    return solve(Struct.of("p", x)).filterIsInstance<Solution.Yes>().map { it.substitution[x] }.toList()
}

/** The probability of [query]'s single solution. */
private fun Solver.probabilityOf(query: String): Double =
    solve(query.parseAsStruct(operators), SolveOptions.DEFAULT.setProbabilistic(true)).single().probability

class MutableProblogSolverSpec :
    FunSpec({
        test("assert-z/appends-facts") {
            val solver = solver()
            solver.assertZ(p(1))
            solver.assertZ(p(2))
            solver.ps() shouldBe listOf(Integer.of(1), Integer.of(2))
        }

        test("assert-a/prepends-facts") {
            val solver = solver()
            solver.assertA(p(1))
            solver.assertA(p(2))
            solver.ps() shouldBe listOf(Integer.of(2), Integer.of(1))
        }

        test("assert-z/keeps-every-head-of-an-annotated-disjunction") {
            val solver = solver()
            solver.assertZ(theory("0.5::a ; 0.5::b.").first())
            solver.probabilityOf("a") shouldBe 0.5
            solver.probabilityOf("b") shouldBe 0.5
        }

        test("assert-a/keeps-every-head-of-an-annotated-disjunction") {
            val solver = solver()
            solver.assertA(theory("0.3::a ; 0.7::b.").first())
            solver.probabilityOf("a") shouldBe 0.3
            solver.probabilityOf("b") shouldBe 0.7
        }

        test("retract/removes-a-fact") {
            val solver = solver()
            solver.assertZ(p(1))
            solver.assertZ(p(2))
            solver.retract(p(1))
            solver.ps() shouldBe listOf(Integer.of(2))
        }

        test("retract-all/removes-every-matching-fact") {
            val solver = solver()
            solver.assertZ(p(1))
            solver.assertZ(p(2))
            solver.retractAll(Struct.of("p", Var.anonymous()))
            solver.ps() shouldBe emptyList()
        }

        test("knowledge-bases/are-mapped-when-loaded-or-appended") {
            val solver = solver()
            solver.loadStaticKb(theory("0.6::a."))
            solver.appendStaticKb(theory("0.4::b."))
            solver.loadDynamicKb(theory("0.2::c."))
            solver.appendDynamicKb(theory("0.8::d."))
            (listOf("a", "b", "c", "d").map { solver.probabilityOf(it) }) shouldBe listOf(0.6, 0.4, 0.2, 0.8)
            solver.resetStaticKb()
            solver.resetDynamicKb()
            (solver.staticKb.isEmpty && solver.dynamicKb.isEmpty).shouldBeTrue()
        }

        test("copy/maps-the-theories-it-is-given") {
            solver().copy(staticKb = theory("0.6::h.")).probabilityOf("h") shouldBe 0.6
            solver().copy(dynamicKb = theory("0.6::h.")).probabilityOf("h") shouldBe 0.6
        }

        test("clone/keeps-the-knowledge-base") {
            val solver = solver()
            solver.loadStaticKb(theory("0.6::h."))
            solver.clone().probabilityOf("h") shouldBe 0.6
        }

        test("settings/reach-the-underlying-solver") {
            val solver = solver()
            val custom = Library.of("custom")
            solver.loadLibrary(custom)
            ("custom" in solver.libraries).shouldBeTrue()
            solver.unloadLibrary(custom)
            ("custom" !in solver.libraries).shouldBeTrue()
            solver.setRuntime(solver.libraries + custom)
            ("custom" in solver.libraries).shouldBeTrue()
            solver.setRuntime(Runtime.empty())
            solver.setFlag(Unknown.name, Unknown.FAIL)
            solver.flags[Unknown] shouldBe Unknown.FAIL
            solver.setFlag(Unknown.name to Unknown.WARNING)
            solver.flags[Unknown] shouldBe Unknown.WARNING
            solver.setFlag(Unknown)
            solver.flags[Unknown] shouldBe Unknown.defaultValue
        }
    })
