package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.parsing.parse
import it.unibo.tuprolog.solve.exception.LogicError
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.unify.Unificator
import kotlin.test.assertTrue

private val EXAMPLE_BLOCK = Regex("```prolog\\n([\\s\\S]*?)```")

private const val EXAMPLE_TIMEOUT: TimeDuration = 10_000

/** Whether [markdown] contains a ```` ```prolog ```` example block. */
fun hasExample(markdown: String): Boolean = EXAMPLE_BLOCK.containsMatchIn(markdown)

/** Same as the other [assertExamplesHold], for the documentation of [library] plus [extra] subjects. */
fun assertExamplesHold(
    library: Library,
    skip: Set<String> = emptySet(),
    extra: Map<String, String> = emptyMap(),
    newSolver: () -> Solver,
) = assertExamplesHold(
    library.documentation.mapKeys { (signature, _) -> "${signature.name}/${signature.arity}" } + extra,
    skip,
    newSolver,
)

/**
 * Runs the ```` ```prolog ```` example blocks of every subject in [documentation] (e.g. `atom/1` or `flag(unknown)`)
 * but the [skip]ped ones, each block on a fresh solver created by [newSolver], and asserts they behave as stated.
 *
 * A block is a sequence of `?- Goal.` queries, run in order, each followed by its expected answer: `yes.`, `no.`,
 * `throws Error.` (`Error` must unify with the raised error term), or the bindings of the first solutions, e.g.
 * `X = 1 ; X = 2.` (only the listed variables and solutions are checked). Lines starting with `%` are ignored.
 */
fun assertExamplesHold(
    documentation: Map<String, String>,
    skip: Set<String> = emptySet(),
    newSolver: () -> Solver,
) {
    val problems =
        documentation.filterKeys { it !in skip }.flatMap { (subject, markdown) ->
            EXAMPLE_BLOCK.findAll(markdown).flatMap { block ->
                val solver = newSolver()
                block.groupValues[1].sentences().chunked(2).mapNotNull { (query, answer) ->
                    runCatching { solver.check(query, answer) }
                        .getOrElse { "${it::class.simpleName}: ${it.message}" }
                        ?.let { "$subject: `$query` → $it" }
                }
            }
        }
    assertTrue(problems.isEmpty(), "Wrong documentation examples:\n${problems.joinToString("\n")}")
}

/** The `.`-terminated sentences of this block, without the final `.`, skipping blank and `%` comment lines. */
private fun String.sentences(): List<String> {
    val sentences = mutableListOf<String>()
    val current = StringBuilder()
    for (line in lines().map { it.trim() }.filter { it.isNotEmpty() && !it.startsWith("%") }) {
        current.append(line).append(' ')
        if (line.endsWith(".")) {
            sentences += current.toString().trim().removeSuffix(".")
            current.clear()
        }
    }
    if (current.isNotBlank()) sentences += current.toString().trim()
    if (sentences.size % 2 != 0) sentences += "<missing answer>"
    return sentences
}

/** Why [query]'s solutions differ from the expected [answer], or `null` if they match. */
private fun Solver.check(
    query: String,
    answer: String,
): String? {
    if (!query.startsWith("?-")) return "not a query"
    val goal = Struct.parse(query.removePrefix("?-").trim(), operators)
    val solutions = solve(goal, EXAMPLE_TIMEOUT).iterator()

    fun next(): Solution? = if (solutions.hasNext()) solutions.next() else null
    return when {
        answer == "yes" -> next().takeUnless { it is Solution.Yes }?.let { "expected yes, got ${it.describe()}" }
        answer == "no" -> next().takeUnless { it is Solution.No }?.let { "expected no, got ${it.describe()}" }
        answer.startsWith("throws ") -> {
            val expected = Term.parse(answer.removePrefix("throws "), operators)
            val solution = next()
            val error = ((solution as? Solution.Halt)?.exception as? LogicError)?.errorStruct
            "expected throws $expected, got ${solution.describe()}"
                .takeUnless { error != null && Unificator.default.match(expected, error) }
        }
        else ->
            Term.parse(answer, operators).alternatives().withIndex().firstNotNullOfOrNull { (i, bindings) ->
                val solution = next()
                if (solution !is Solution.Yes) return "expected solution #${i + 1}, got ${solution.describe()}"
                bindings.firstNotNullOfOrNull { binding ->
                    val variable = binding.getArgAt(0) as? Var ?: return "malformed binding `$binding`"
                    val queried = goal.variables.firstOrNull { it.name == variable.name }
                    val actual = queried?.let { solution.substitution[it] ?: it }
                    "solution #${i + 1}: expected $binding, got ${solution.describe()}"
                        .takeUnless { actual != null && actual structurallyEquals binding.getArgAt(1) }
                }
            }
    }
}

/** The `;`-separated alternatives of this answer, each as its `,`-separated `Var = Value` bindings. */
private fun Term.alternatives(): List<List<Struct>> =
    split(";").map { alternative -> alternative.split(",").map { it as? Struct ?: Struct.of("=", it) } }

private fun Term.split(functor: String): List<Term> =
    if (this is Struct &&
        this.functor == functor &&
        arity == 2
    ) {
        getArgAt(0).split(functor) + getArgAt(1).split(functor)
    } else {
        listOf(this)
    }

private fun Solution?.describe(): String =
    when (this) {
        null -> "no answer"
        is Solution.Yes -> "yes ${substitution.filterKeys { !it.name.startsWith("_") }}"
        is Solution.No -> "no"
        is Solution.Halt -> "throws ${(exception as? LogicError)?.errorStruct ?: exception}"
    }
