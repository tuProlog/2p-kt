package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.core.parsing.parseAsClause
import it.unibo.tuprolog.core.parsing.parseAsStruct
import it.unibo.tuprolog.solve.exception.LogicError
import it.unibo.tuprolog.theory.Theory

/**
 * The answers to the Prolog [query], on a fresh solver of this factory with default built-ins whose static knowledge
 * base holds the [clauses] (each one parsed on its own, with the default operators). The query is parsed with the
 * solver's operators, so it can use those the clauses declare.
 *
 * Each answer is rendered as text: `yes` followed by the bindings of the query's named variables, sorted by name
 * (e.g. `yes: X = 1, Y = a`), `no`, or `halt: ` followed by the error term of a logic error (e.g.
 * `halt: type_error(atom, 1)`). This keeps expectations close to what a Prolog user would read.
 */
fun SolverFactory.answers(
    query: String,
    vararg clauses: String,
    budget: SolveOptions = durations.mediumBudget,
): List<String> =
    solverWithDefaultBuiltins(staticKb = Theory.of(clauses.map { it.parseAsClause() })).answers(query, budget)

/** The answers to the Prolog [query] on this solver, rendered as in [SolverFactory.answers]. */
fun Solver.answers(
    query: String,
    budget: SolveOptions = durations.mediumBudget,
): List<String> {
    val formatter = TermFormatter.readable(operators)
    return solve(query.parseAsStruct(operators), budget).map { it.render(formatter) }.toList()
}

private val durations = object : SolverTest {}

private fun Solution.render(formatter: TermFormatter): String =
    when (this) {
        is Solution.Yes ->
            substitution
                .filterKeys { !it.name.startsWith("_") }
                .entries
                .sortedBy { it.key.name }
                .joinToString(", ", "yes: ") { (v, t) -> "${v.name} = ${formatter.format(t)}" }
                .removeSuffix(": ")
        is Solution.No -> "no"
        is Solution.Halt ->
            "halt: " + ((exception as? LogicError)?.errorStruct?.get(0)?.let(formatter::format) ?: exception.toString())
    }
