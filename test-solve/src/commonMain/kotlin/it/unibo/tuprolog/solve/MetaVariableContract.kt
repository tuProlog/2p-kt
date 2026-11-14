package it.unibo.tuprolog.solve

import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import it.unibo.tuprolog.core.Compound
import it.unibo.tuprolog.core.Goal
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Pattern
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.parsing.parseAsClause
import it.unibo.tuprolog.core.parsing.parseAsCompound
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.theory.Theory
import kotlin.time.Duration.Companion.seconds

/**
 * A query on a theory, and its answers: for each solution, in order, the values of [variables] (`_` for unbound
 * ones). The solutions are all the expected ones: no more, and no error ending the resolution.
 */
private class MetaCase(
    val id: String,
    val theory: List<String>,
    val query: String,
    val variables: List<String>,
    val answers: List<List<String>>,
)

/** [count] solutions, binding no variable of interest. */
private fun yes(count: Int = 1) = List(count) { emptyList<String>() }

private fun answers(vararg rows: String) = rows.map { row -> row.split(",").map { it.trim() } }

private val basic = listOf("f(1)", "g(2)", "h(3)")
private val arities = listOf("f(1)", "g(2)", "f(a, b)", "g(c, d)", "h(a, b, c)")
private val backtracking = listOf("f(a)", "f(b)", "g(a)", "h(b)", "h(c)")
private val stored =
    listOf(
        "stored(parent, alice, bob)",
        "stored(parent, bob, charlie)",
        "stored(likes, alice, pizza)",
        "P(X, Y) :- stored(P, X, Y)",
    )
private val composed =
    listOf(
        "parent(alice, bob)",
        "parent(bob, charlie)",
        "parent(charlie, david)",
        "compose(grandparent, parent, parent)",
        "P(X, Y) :- compose(P, Q, R), Q(X, Z), R(Z, Y)",
    )

// the guard is ternary: binary metarules apply to every binary predicate, a binary guard included, which would make
// `transitive_view(transitive_view, E)` call itself forever
private val recursive =
    listOf(
        "edge(a, b)",
        "edge(b, c)",
        "edge(c, d)",
        "edge(d, e)",
        "view(transitive, reachable, edge)",
        "P(X, Y) :- view(transitive, P, E), E(X, Y)",
        "P(X, Y) :- view(transitive, P, E), E(X, Z), P(Z, Y)",
    )
private val cached =
    listOf(
        "P(X) :- cached(P, X)",
        "status(b)",
        "status(c)",
        "other(z)",
        "status(d)",
        "cached(status, a)",
        "cached(other, w)",
    )

private val metaCases =
    listOf(
        // 1. basic resolution of variable-functor goals
        MetaCase("mr-01/enumerate-unary-predicates", basic, "F(X)", listOf("F", "X"), answers("f,1", "g,2", "h,3")),
        MetaCase("mr-02/partially-instantiated-argument", basic, "F(2)", listOf("F"), answers("g")),
        MetaCase("mr-03/nonexistent-argument", basic, "F(4)", listOf("F"), emptyList()),
        MetaCase("mr-04/functor-bound-before", basic, "F = g, F(X)", listOf("F", "X"), answers("g,2")),
        MetaCase("mr-05/argument-bound-before", basic, "X = 3, F(X)", listOf("F", "X"), answers("h,3")),
        MetaCase("mr-06/both-bound-before", basic, "F = f, F(1)", emptyList(), yes()),
        MetaCase("mr-07/incompatible-functor-binding", basic, "F = f, F(2)", emptyList(), emptyList()),
        MetaCase("mr-08/concrete-query", basic, "f(X)", listOf("X"), answers("1")),
        // 2. arity-based clause discovery
        MetaCase("mr-09/unary-predicates-only", arities, "F(X)", listOf("F", "X"), answers("f,1", "g,2")),
        MetaCase("mr-10/binary-predicates", arities, "F(X, Y)", listOf("F", "X", "Y"), answers("f,a,b", "g,c,d")),
        MetaCase("mr-11/ternary-predicates", arities, "F(X, Y, Z)", listOf("F", "X", "Y", "Z"), answers("h,a,b,c")),
        MetaCase("mr-12/arity-mismatch", arities, "F(a, b, c, d)", listOf("F"), emptyList()),
        MetaCase("mr-13/predicates-sharing-a-name", arities, "F(a, b)", listOf("F"), answers("f")),
        MetaCase("mr-14/empty-theory", emptyList(), "F(X)", listOf("F"), emptyList()),
        // 3. backtracking and variable propagation
        MetaCase(
            "mr-15/several-solutions-per-procedure",
            backtracking,
            "F(X)",
            listOf("F", "X"),
            answers("f,a", "f,b", "g,a", "h,b", "h,c"),
        ),
        MetaCase("mr-16/filter-by-argument", backtracking, "F(a)", listOf("F"), answers("f", "g")),
        MetaCase("mr-17/functor-reused-across-goals", backtracking, "F(a), F(b)", listOf("F"), answers("f")),
        MetaCase("mr-18/functor-reused-with-other-arguments", backtracking, "F(b), F(c)", listOf("F"), answers("h")),
        MetaCase("mr-19/backtracking-over-functor-bindings", backtracking, "F(a), F(c)", listOf("F"), emptyList()),
        MetaCase("mr-20/bindings-across-conjunctions", backtracking, "F(X), X = c", listOf("F", "X"), answers("h,c")),
        MetaCase(
            "mr-21/bindings-restored-after-failure",
            backtracking,
            "(F(a), F(c) ; F(b))",
            listOf("F"),
            answers("f", "h"),
        ),
        MetaCase("mr-22/duplicate-answers", backtracking + "f(a)", "F(a)", listOf("F"), answers("f", "g", "f")),
        // 4. pattern-headed clauses
        MetaCase("mr-23/concrete-goal-by-pattern-head", stored, "parent(alice, bob)", emptyList(), yes()),
        MetaCase("mr-24/another-virtual-predicate", stored, "likes(alice, pizza)", emptyList(), yes()),
        MetaCase("mr-25/arguments-bound-through-pattern-head", stored, "parent(alice, X)", listOf("X"), answers("bob")),
        MetaCase(
            "mr-26/enumerate-virtual-predicates",
            stored,
            "F(alice, X)",
            listOf("F", "X"),
            answers("parent,bob", "likes,pizza"),
        ),
        MetaCase(
            "mr-27/enumerate-virtual-predicates-unbound-arguments",
            stored,
            "F(X, Y)",
            listOf("F", "X", "Y"),
            answers("parent,alice,bob", "parent,bob,charlie", "likes,alice,pizza"),
        ),
        MetaCase("mr-28/nonexistent-virtual-relation", stored, "other(alice, bob)", emptyList(), emptyList()),
        MetaCase("mr-29/functor-binding-from-head-to-body", stored, "likes(alice, X)", listOf("X"), answers("pizza")),
        MetaCase("mr-30/arity-incompatible-pattern-head", stored, "F(alice)", listOf("F"), emptyList()),
        // 5. metarules with variable functors in the body
        MetaCase("mr-31/composed-relation", composed, "grandparent(alice, charlie)", emptyList(), yes()),
        MetaCase("mr-32/composed-relation-results", composed, "grandparent(alice, X)", listOf("X"), answers("charlie")),
        MetaCase(
            "mr-33/functor-bound-through-metarule",
            composed,
            "F(alice, charlie)",
            listOf("F"),
            answers("grandparent"),
        ),
        MetaCase("mr-34/functor-binding-to-several-goals", composed, "grandparent(bob, david)", emptyList(), yes()),
        MetaCase(
            "mr-35/backtracking-through-composed-goals",
            composed,
            "grandparent(alice, david)",
            emptyList(),
            emptyList(),
        ),
        MetaCase(
            "mr-36/nonexistent-composed-relation",
            composed,
            "grandparent(david, alice)",
            emptyList(),
            emptyList(),
        ),
        // 6. recursive metarules
        MetaCase("mr-37/direct-connection", recursive, "reachable(a, b)", emptyList(), yes()),
        MetaCase("mr-38/transitive-connection", recursive, "reachable(a, e)", emptyList(), yes()),
        MetaCase("mr-39/recursive-results", recursive, "reachable(a, X)", listOf("X"), answers("b", "c", "d", "e")),
        MetaCase("mr-40/unreachable-destination", recursive, "reachable(e, a)", emptyList(), emptyList()),
        MetaCase("mr-41/functor-binding-kept-recursively", recursive, "reachable(b, e)", emptyList(), yes()),
        MetaCase(
            "mr-42/functor-bound-through-recursive-metarules",
            recursive,
            "F(a, e)",
            listOf("F"),
            answers("reachable"),
        ),
        MetaCase("mr-43/termination-on-acyclic-graph", recursive, "reachable(e, X)", listOf("X"), emptyList()),
        // 7. clause ordering
        MetaCase(
            "mr-44/metarule-before-concrete-clauses",
            cached,
            "status(X)",
            listOf("X"),
            answers("a", "b", "c", "d"),
        ),
        MetaCase("mr-45/metarule-before-other-clauses", cached, "other(X)", listOf("X"), answers("w", "z")),
        MetaCase(
            "mr-46/concrete-and-pattern-headed-answers",
            cached,
            "F(X)",
            listOf("F", "X"),
            answers("status,a", "other,w", "status,b", "status,c", "other,z", "status,d"),
        ),
        MetaCase("mr-47/unrelated-predicate-fails-without-error", cached, "unrelated(X)", listOf("X"), emptyList()),
        MetaCase(
            "mr-47/unrelated-predicate-sees-metarules",
            cached + "cached(fresh, 1)",
            "fresh(X)",
            listOf("X"),
            answers("1"),
        ),
        MetaCase(
            "mr-48/concrete-clauses-before-metarule",
            listOf("status(b)", "status(c)", "P(X) :- cached(P, X)", "cached(status, a)"),
            "status(X)",
            listOf("X"),
            answers("b", "c", "a"),
        ),
        MetaCase(
            "mr-49/interleaved-clauses",
            listOf(
                "status(1)",
                "P(X) :- cached(P, X)",
                "status(2)",
                "Q(X) :- extra(Q, X)",
                "status(3)",
                "cached(status, m)",
                "extra(status, n)",
            ),
            "status(X)",
            listOf("X"),
            answers("1", "m", "2", "n", "3"),
        ),
    )

private fun SolverFactory.solverOf(clauses: List<String>) =
    solverWithDefaultBuiltins(staticKb = Theory.of(clauses.map { it.parseAsClause() }))

private val timeout = SolveOptions.allLazilyWithTimeout(10.seconds)

/** The values of [variables] in each of the solutions of [query], all of them successful but the last one. */
private fun Solver.answersOf(
    query: Compound,
    variables: List<String>,
): List<List<String>> {
    val solutions = solve(query, timeout).toList()
    withClue("$query ended with ${solutions.lastOrNull()}") {
        solutions.filterIsInstance<Solution.Halt>().shouldBeEmpty()
    }
    return solutions.filterIsInstance<Solution.Yes>().map { yes ->
        variables.map { yes.valueOf(it)?.toString() ?: "_" }
    }
}

private fun Solver.answersOf(
    query: String,
    variables: List<String>,
) = answersOf(query.parseAsCompound(operators), variables)

/**
 * Goals whose functors are variables (patterns), e.g. `F(X)`, resolve like any other goal: their functors are bound by
 * unifying them with the heads of the clauses of their arity, in the order of the theory, whether those heads are
 * concrete or patterns themselves. No option is needed, and library predicates are not candidates.
 */
fun metaVariableContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
    // whether solutions come in a fixed order: otherwise, they are compared as multisets
    ordered: Boolean = true,
) = contract(solverFactory, disabled) {
    infix fun List<List<String>>.shouldAnswer(expected: List<List<String>>) =
        if (ordered) this shouldBe expected else this shouldContainExactlyInAnyOrder expected

    for (case in metaCases) {
        scenario("meta-variables/${case.id}") {
            solverFactory.solverOf(case.theory).answersOf(case.query, case.variables) shouldAnswer case.answers
        }
    }

    scenario("meta-variables/mr-50/same-answers-once-indexed") {
        val clauses =
            (1..20).map { "status($it)" } + "P(X) :- cached(P, X)" + (21..40).map { "status($it)" } +
                "cached(status, 0)"
        val solver = solverFactory.solverOf(clauses)
        val expected = (1..20).map { listOf("$it") } + listOf(listOf("0")) + (21..40).map { listOf("$it") }
        // the first queries build the indexes the later ones use
        repeat(2) {
            solver.answersOf("status(X)", listOf("X")) shouldAnswer expected
            solver.answersOf("F(X)", listOf("X")) shouldAnswer expected
            solver.answersOf("status(30)", emptyList()) shouldAnswer yes()
            solver.answersOf("F(0)", listOf("F")) shouldAnswer answers("status")
        }
    }

    scenario("meta-variables/mr-52/metarules-asserted-and-retracted") {
        val solver = solverFactory.solverOf(listOf("status(b)", "cached(status, a)"))
        solver.answersOf("assertz((P(X) :- cached(P, X))), status(X)", listOf("X")) shouldAnswer answers("b", "a")
        solver.answersOf("retract((P(X) :- cached(P, X))), status(X)", listOf("X")) shouldAnswer answers("b")
        // the static knowledge base comes before the dynamic one, where the metarule is
        solver.answersOf("asserta((P(X) :- cached(P, X))), F(X)", listOf("F", "X")) shouldAnswer
            answers("status,b", "status,a")
    }

    scenario("meta-variables/mr-53/static-and-dynamic-theories-agree") {
        val clauses = cached.map { it.parseAsClause() }
        val static = solverFactory.solverWithDefaultBuiltins(staticKb = Theory.of(clauses))
        val dynamic = solverFactory.solverWithDefaultBuiltins(dynamicKb = Theory.of(clauses))
        for (query in listOf("status(X)", "other(X)", "F(X)", "F(a)")) {
            dynamic.answersOf(query, listOf("F", "X")) shouldAnswer static.answersOf(query, listOf("F", "X"))
        }
    }

    scenario("meta-variables/mr-82/pattern-solved-directly") {
        val (f, x) = Var.of("F") to Var.of("X")
        solverFactory.solverOf(basic).answersOf(Pattern.of(f, x), listOf("F", "X")) shouldAnswer
            answers("f,1", "g,2", "h,3")
    }

    scenario("meta-variables/mr-83/pattern-solved-as-goal") {
        val goal = Goal.of(Pattern.of(Var.of("F"), Var.of("X")))
        val solutions = solverFactory.solverOf(basic).solve(goal, timeout).toList()
        solutions.filterIsInstance<Solution.Yes>().map { it.query } shouldBe List(3) { goal }
        val solved = listOf(Struct.of("f", Integer.of(1)), Struct.of("g", Integer.of(2)), Struct.of("h", Integer.of(3)))
        val actual = solutions.filterIsInstance<Solution.Yes>().map { it.solvedQuery.body }
        if (ordered) actual shouldBe solved else actual shouldContainExactlyInAnyOrder solved
    }

    scenario("meta-variables/mr-84/variable-goal-is-not-a-pattern") {
        val solution = solverFactory.solverOf(basic).solveOnce(Goal.of(Var.of("G")), timeout)
        solution.shouldBeInstanceOf<Solution.Halt>()
        solution.exception.shouldBeInstanceOf<InstantiationError>()
    }

    scenario("meta-variables/mr-85/functor-bound-to-an-atom") {
        val solver = solverFactory.solverOf(basic)
        solver.answersOf("F = g, F(X)", listOf("X")) shouldAnswer solver.answersOf("g(X)", listOf("X"))
    }

    scenario("meta-variables/mr-86/functor-bound-to-a-non-atom") {
        val solver = solverFactory.solverOf(basic)
        for (query in listOf("F = 1, F(X)", "F = 1.5, F(X)", "F = f(a), F(X)")) {
            withClue(query) {
                val solution = solver.solveOnce(query.parseAsCompound(solver.operators), timeout)
                solution.shouldBeInstanceOf<Solution.Halt>()
                solution.exception.shouldBeInstanceOf<TypeError>()
            }
        }
    }

    scenario("meta-variables/mr-87/solutions-computed-lazily") {
        // the second candidate raises an error: reaching the first answer must not run it
        val solver = solverFactory.solverOf(listOf("f(1)", "g(X) :- throw(reached)"))
        val first = solver.solveOnce("F(X)".parseAsCompound(), timeout)
        first.shouldBeInstanceOf<Solution.Yes>()
        first.valueOf("F").toString() shouldBe "f"
    }

    scenario("meta-variables/mr-88/early-stop-tries-few-candidates") {
        val solver = solverFactory.solverOf((1..1000).map { "f($it)" })
        val counters = ResolutionCounters()
        val first = solver.solveOnce("F(X)".parseAsCompound(), timeout.setCounters(counters))
        first.shouldBeInstanceOf<Solution.Yes>()
        withClue("$counters") { (counters.alternatives < 10) shouldBe true }
    }

    scenario("meta-variables/library-predicates-are-not-candidates") {
        // call/1, once/1, \+/1 are library rules of arity 1: only the user's predicates answer
        val solver = solverFactory.solverOf(listOf("f(1)"))
        solver.answersOf("F(X)", listOf("F")) shouldAnswer answers("f")
    }
}
