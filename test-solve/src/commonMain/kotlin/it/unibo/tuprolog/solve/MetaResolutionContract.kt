package it.unibo.tuprolog.solve

import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.parsing.parseAsClause
import it.unibo.tuprolog.core.parsing.parseAsCompound
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.search.MetaResolutionPolicy
import it.unibo.tuprolog.theory.Theory

private fun SolverFactory.solverOf(vararg clauses: String) =
    solverWithDefaultBuiltins(staticKb = Theory.of(clauses.map { it.parseAsClause() }))

/** The values of [variable] in the successful solutions of [query], in order. */
private fun Solver.valuesOf(
    query: String,
    variable: String,
    options: SolveOptions = SolveOptions.allLazilyWithTimeout(TIMEOUT),
): List<Term?> =
    solve(query.parseAsCompound(), options).toList().filterIsInstance<Solution.Yes>().map { it.valueOf(variable) }

private fun Solver.haltOf(query: String): Solution.Halt {
    val solution = solveOnce(query.parseAsCompound(), SolveOptions.someLazilyWithTimeout(1, TIMEOUT))
    solution.shouldBeInstanceOf<Solution.Halt>()
    return solution
}

private val TIMEOUT = kotlin.time.Duration.parse("10s")

private fun enumerating(limit: Int = SolveOptions.ALL_SOLUTIONS) =
    SolveOptions
        .allLazilyWithTimeout(TIMEOUT)
        .setMetaResolution(MetaResolutionPolicy.ENUMERATE)
        .let { if (limit == SolveOptions.ALL_SOLUTIONS) it else it.setLimit(limit) }

/**
 * Pattern-headed rules (metarules) run natively: concrete calls select them, interleaved with concrete clauses, binding
 * their functor variables. Calls whose functor is an unbound variable need a [MetaResolutionPolicy].
 */
fun metaResolutionContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    scenario("meta-resolution/concrete-call-selects-metarules-in-order") {
        val solver = solverFactory.solverOf("p(1).", "P(X) :- base(P, X).", "p(3).", "base(p, 2).")
        solver.valuesOf("p(X)", "X") shouldBe listOf(1, 2, 3).map { it.toTerm() }
    }

    scenario("meta-resolution/metarule-for-a-symbol-without-clauses") {
        val solver = solverFactory.solverOf("P(X) :- base(P, X).", "base(q, a).")
        solver.valuesOf("q(X)", "X") shouldBe listOf(Atom.of("a"))
    }

    scenario("meta-resolution/bound-functor-calls-in-the-body") {
        val solver = solverFactory.solverOf("P(X) :- P(X, X).", "p(a, a).", "p(a, b).")
        solver.valuesOf("p(X)", "X") shouldBe listOf(Atom.of("a"))
    }

    scenario("meta-resolution/bound-pattern-query") {
        val solver = solverFactory.solverOf("p(1).")
        solver.valuesOf("F = p, F(X)", "X") shouldBe listOf(1.toTerm())
    }

    scenario("meta-resolution/metarules-do-not-extend-library-predicates") {
        val solver = solverFactory.solverOf("P(A, B) :- true.", "P(A) :- true.")
        // in any order, as concurrent solvers may find them: what matters is that there are no others
        solver.valuesOf("(X = 1 ; X = 2)", "X") shouldContainExactlyInAnyOrder listOf(1, 2).map { it.toTerm() }
        solver.valuesOf("once(X = 1)", "X") shouldBe listOf(1.toTerm())
    }

    scenario("meta-resolution/unbound-functor-requires-binding-by-default") {
        val solver = solverFactory.solverOf("q(1).")
        solver.haltOf("Q(X)").exception.shouldBeInstanceOf<InstantiationError>()
    }

    scenario("meta-resolution/unbound-functor-in-a-metarule-body") {
        val solver = solverFactory.solverOf("P(X) :- Q(X).", "q(1).")
        solver.haltOf("p(1)").exception.shouldBeInstanceOf<InstantiationError>()
    }

    scenario("meta-resolution/enumerate-user-predicates-of-the-arity") {
        val solver = solverFactory.solverOf("q(1).", "s(1, 2).", "r(2).", "q(3).")
        solver.valuesOf("Q(X)", "Q", enumerating()) shouldBe listOf("q", "q", "r").map { Atom.of(it) }
        solver.valuesOf("Q(X)", "X", enumerating()) shouldBe listOf(1, 3, 2).map { it.toTerm() }
    }

    scenario("meta-resolution/enumerate-respects-cut-and-negation") {
        val solver = solverFactory.solverOf("q(1).", "r(2).")
        solver.valuesOf("Q(X), !", "Q", enumerating()) shouldBe listOf(Atom.of("q"))
        solver.valuesOf("\\+ Q(5)", "Q", enumerating()) shouldBe listOf(null)
        solver.valuesOf("\\+ Q(2)", "Q", enumerating()) shouldBe emptyList()
    }

    scenario("meta-resolution/enumerate-sees-dynamic-predicates") {
        val solver = solverFactory.solverOf("q(1).")
        solver.valuesOf("assertz(t(9)), Q(X)", "Q", enumerating()) shouldBe listOf("q", "t").map { Atom.of(it) }
    }

    scenario("meta-resolution/enumerate-chain-metarule") {
        val solver = solverFactory.solverOf("edge(a, b).", "edge(b, c).", "P(A, B) :- Q(A, C), R(C, B).")
        solver.valuesOf("edge(a, Y)", "Y", enumerating(limit = 2)) shouldBe listOf("b", "c").map { Atom.of(it) }
    }

    scenario("meta-resolution/custom-policy") {
        val solver = solverFactory.solverOf("q(1).", "r(2).")
        val onlyR = MetaResolutionPolicy { _, _ -> sequenceOf(Atom.of("r")) }
        val options = SolveOptions.allLazilyWithTimeout(TIMEOUT).setMetaResolution(onlyR)
        solver.valuesOf("Q(X)", "X", options) shouldBe listOf(2.toTerm())
    }
}

private fun Int.toTerm(): Term =
    it.unibo.tuprolog.core.Integer
        .of(this)
