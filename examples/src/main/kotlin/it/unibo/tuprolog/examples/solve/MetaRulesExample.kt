package it.unibo.tuprolog.examples.solve

import it.unibo.tuprolog.core.parsing.parseAsCompound
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.theory.parsing.parse

/**
 * A lesson in variable predicate symbols: queries and rules whose functors are variables, which 2P-Kt resolves
 * natively, by unification, as it resolves any other goal.
 */
object MetaRulesExample {
    /** Each lesson: what it shows, a theory, and the queries showing it. */
    class Lesson(
        val title: String,
        val theory: String,
        val queries: List<String>,
    )

    val lessons =
        listOf(
            Lesson(
                // F(X) unifies with the head of every clause of arity 1: F is bound to f, then g, then h
                "Variable-functor queries enumerate the predicates of their arity",
                """
                f(1).
                g(2).
                h(3).
                """,
                listOf("F(X)", "F(2)", "F = g, F(X)"),
            ),
            Lesson(
                // a pattern head makes `parent` and `likes` predicates, although no clause is named after them
                "Pattern-headed clauses define virtual predicates",
                """
                stored(parent, alice, bob).
                stored(likes, alice, pizza).
                P(X, Y) :- stored(P, X, Y).
                """,
                listOf("parent(alice, X)", "F(alice, X)"),
            ),
            Lesson(
                // the body binds Q and R, then calls them: functors flow from goal to goal like any other binding
                "Metarules compose relations",
                """
                parent(alice, bob).
                parent(bob, charlie).
                parent(charlie, david).
                compose(grandparent, parent, parent).
                P(X, Y) :- compose(P, Q, R), Q(X, Z), R(Z, Y).
                """,
                listOf("grandparent(alice, X)", "F(alice, charlie)"),
            ),
            Lesson(
                // the guard is ternary: a binary one would be a binary predicate, which these binary metarules
                // would define too, calling themselves forever
                "Recursive metarules define closures",
                """
                edge(a, b).
                edge(b, c).
                edge(c, d).
                view(transitive, reachable, edge).
                P(X, Y) :- view(transitive, P, E), E(X, Y).
                P(X, Y) :- view(transitive, P, E), E(X, Z), P(Z, Y).
                """,
                listOf("reachable(a, X)"),
            ),
        )

    /** The answers to [query] on [theory], one line per solution, as the bindings of the query's variables. */
    fun answers(
        theory: String,
        query: String,
    ): List<String> {
        val solver = Solver.prolog.solverWithDefaultBuiltins(staticKb = Theory.parse(theory.trimIndent()))
        val goal = query.parseAsCompound(solver.operators)
        val variables = goal.variables.distinct().toList()
        return solver.solve(goal).toList().filterIsInstance<Solution.Yes>().map { solution ->
            when {
                variables.isEmpty() -> "yes"
                else -> variables.joinToString(", ") { "${it.name} = ${solution.valueOf(it)}" }
            }
        }
    }

    /** Prints every lesson: its theory, its queries, and their answers. */
    @JvmStatic
    fun main(args: Array<String>) {
        for (lesson in lessons) {
            println("% ${lesson.title}")
            println(lesson.theory.trimIndent())
            for (query in lesson.queries) {
                println("?- $query.")
                answers(lesson.theory, query).forEach { println("    $it") }
            }
            println()
        }
    }
}
