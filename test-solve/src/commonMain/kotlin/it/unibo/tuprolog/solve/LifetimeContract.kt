package it.unibo.tuprolog.solve

import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.parsing.parseAsClause
import it.unibo.tuprolog.core.parsing.parseAsStruct
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.primitive.Primitive
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.theory.Theory

/**
 * Objects handed out by the solver keep reading the same after it moves on (#1010, step 0): solutions, primitive
 * requests and their contexts, and errors, after further answers, backtracking, exhaustion and another query. A solver
 * that reuses mutable storage must not let these views change.
 */
fun lifetimeContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    val clauses =
        arrayOf(
            "p(1, a).",
            "p(2, b).",
            "p(3, c).",
            "q(X, Y) :- p(X, Z), keep(f(X, Z, W)), W = Y.",
            "r(X) :- p(X, _), X >= 3, Y is X * 2, throw(err(X, Y)).",
        )

    scenario("lifetime/retained-solutions") {
        val probe = LifetimeProbe(solverFactory, *clauses)
        val retained =
            probe.solver
                .solve("q(X, Y)".parseAsStruct())
                .map { it to it.render() }
                .toList()
        probe.solver.solve("q(X, Y), fail".parseAsStruct()).toList()
        retained.map { (solution, _) -> solution.render() } shouldBe retained.map { it.second }
    }

    scenario("lifetime/retained-requests") {
        val probe = LifetimeProbe(solverFactory, *clauses)
        probe.solver.solve("q(X, Y)".parseAsStruct()).toList()
        probe.solver.solve("q(A, b), r(_)".parseAsStruct()).toList()
        probe.kept.size shouldBe 4 // 3 for the first query; r/1 halts the second after its first answer
        probe.kept.map { (request, _) -> request.render() } shouldBe probe.kept.map { it.second }
    }

    scenario("lifetime/retained-errors") {
        val probe = LifetimeProbe(solverFactory, *clauses)
        val halt = probe.solver.solve("r(X)".parseAsStruct()).last() as Solution.Halt
        val rendered = halt.render()
        probe.solver.solve("q(X, Y)".parseAsStruct()).toList()
        probe.solver.solve("r(X)".parseAsStruct()).toList()
        halt.render() shouldBe rendered
    }
}

private class LifetimeProbe(
    solverFactory: SolverFactory,
    vararg clauses: String,
) {
    val kept = mutableListOf<Pair<Solve.Request<ExecutionContext>, String>>()

    private val library =
        Library.of(
            "test.lifetime",
            primitives =
                mapOf(
                    Signature("keep", 1) to
                        Primitive { request ->
                            kept += request to request.render()
                            sequenceOf(request.replySuccess())
                        },
                ),
        )

    val solver =
        solverFactory.solverWithDefaultBuiltins(
            otherLibraries = Runtime.of(library),
            staticKb = Theory.of(clauses.map { it.parseAsClause() }),
        )
}

private fun Solve.Request<ExecutionContext>.render(): String =
    "$query | ${context.procedure} | ${context.substitution} | ${context.logicStackTrace}"

private fun Solution.render(): String =
    "$query | $substitution | $solvedQuery | ${exception?.let { "${it.message} ${it.logicStackTrace}" }}"
