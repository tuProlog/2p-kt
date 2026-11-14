package it.unibo.tuprolog.testutils

import io.kotest.assertions.fail
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Empty
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Goal
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.Unificator.Companion.matches

/**
 * Clause and query fixtures shared by the `:theory` specs
 *
 * @author Enrico
 */
internal object ClauseFixtures {
    /** A named clause, for parameterized tests */
    internal data class ClauseCase<out C : Clause>(
        val id: String,
        val clause: C,
    )

    /** A named query, along with the clauses it is expected to retrieve, for parameterized tests */
    internal data class QueryCase<out C : Clause>(
        val id: String,
        val query: C,
        val expected: List<C>,
    )

    /** Contains some well-formed rules */
    internal val ruleCases =
        listOf(
            ClauseCase("fact-true", Fact.of(Truth.TRUE)),
            ClauseCase("fact-fail", Fact.of(Truth.FAIL)),
            ClauseCase("fact-a", Fact.of(Atom.of("a"))),
            ClauseCase("fact-other", Fact.of(Atom.of("other"))),
            ClauseCase("fact-a-of-other", Fact.of(Struct.of("a", Atom.of("other")))),
            ClauseCase("fact-other-of-1", Fact.of(Struct.of("other", Integer.of(1)))),
            ClauseCase("fact-tuple-of-vars", Fact.of(Tuple.of(Var.of("A"), Var.of("B")))),
            ClauseCase("rule-a-if-other", Rule.of(Atom.of("a"), Atom.of("other"))),
            ClauseCase("rule-tuple-of-vars-if-a", Rule.of(Tuple.of(Var.of("A"), Var.of("B")), Atom.of("a"))),
            ClauseCase("rule-a-of-other-if-a", Rule.of(Struct.of("a", Atom.of("other")), Atom.of("a"))),
            ClauseCase(
                "fact-f-of-a-b-of-x-do-something-else",
                Rule.of(Struct.of("f", Atom.of("a"), Struct.of("b", Var.of("X")), Atom.of("do_something_else"))),
            ),
            ClauseCase("rule-a-of-22-if-anonymous", Rule.of(Struct.of("a", Integer.of(22)), Var.anonymous())),
            ClauseCase("rule-f-of-a-if-variable", Rule.of(Struct.of("f", Atom.of("a")), Var.of("Variable"))),
            ClauseCase("rule-f-of-a-if-variable-again", Rule.of(Struct.of("f", Atom.of("a")), Var.of("Variable"))),
            ClauseCase(
                "rule-a-of-anonymous-if-b-of-anonymous",
                Rule.of(Struct.of("a", Var.anonymous()), Struct.of("b", Var.anonymous())),
            ),
            ClauseCase("rule-a-of-a-if-empty-block", Rule.of(Struct.of("a", Atom.of("a")), Empty.block())),
            ClauseCase(
                "rule-a-of-a-if-other-of-anonymous",
                Rule.of(Struct.of("a", Atom.of("a")), Struct.of("other", Var.anonymous())),
            ),
            ClauseCase(
                "rule-a-of-a-if-a-of-anonymous",
                Rule.of(Struct.of("a", Atom.of("a")), Struct.of("a", Var.anonymous())),
            ),
            ClauseCase("rule-a-of-a-if-anonymous", Rule.of(Struct.of("a", Atom.of("a")), Var.anonymous())),
        )

    internal val rules = ruleCases.map { it.clause }

    private fun <C : Clause> matchingQuery(
        id: String,
        query: C,
        clauses: List<C>,
    ) = QueryCase(id, query, clauses.filter { query matches it })

    /** Contains queries and results crafted watching [rules] collection (NOTE: any modifications must be reviewed by hand) */
    internal val ruleQueryCases =
        listOf(
            QueryCase("true", Fact.of(Truth.TRUE), listOf(Fact.of(Truth.TRUE))),
            QueryCase("empty-list", Fact.of(Empty.list()), emptyList()),
            QueryCase("a-of-a", Rule.of(Struct.of("a", Atom.of("a")), Var.anonymous()), rules.takeLast(5)),
            matchingQuery("a-of-other", Rule.of(Struct.of("a", Atom.of("other")), Var.anonymous()), rules),
            matchingQuery(
                "a-of-any-if-b-of-any",
                Rule.of(Struct.of("a", Var.anonymous()), Struct.of("b", Var.anonymous())),
                rules,
            ),
            matchingQuery("a-of-any", Rule.of(Struct.of("a", Var.anonymous()), Var.anonymous()), rules),
        )

    /** Contains some well-formed directives */
    internal val directiveCases =
        listOf(
            ClauseCase("directive-true", Goal.of(Truth.TRUE)),
            ClauseCase("directive-fail", Goal.of(Truth.FAIL)),
            ClauseCase("directive-a", Goal.of(Atom.of("a"))),
            ClauseCase("directive-other", Goal.of(Atom.of("other"))),
            ClauseCase("directive-a-of-other", Goal.of(Struct.of("a", Atom.of("other")))),
            ClauseCase("directive-other-of-1", Goal.of(Struct.of("other", Integer.of(1)))),
            ClauseCase("directive-a-and-other", Goal.of(Atom.of("a"), Atom.of("other"))),
            ClauseCase("directive-a-of-other-and-a", Goal.of(Struct.of("a", Atom.of("other")), Atom.of("a"))),
            ClauseCase(
                "directive-f-of-a-b-of-x-do-something-else",
                Goal.of(Struct.of("f", Atom.of("a"), Struct.of("b", Var.of("X")), Atom.of("do_something_else"))),
            ),
            ClauseCase(
                "directive-a-of-22-and-anonymous",
                Goal.of(Struct.of("a", Integer.of(22)), Var.anonymous()),
            ),
            ClauseCase(
                "directive-f-of-a-and-variable",
                Goal.of(Struct.of("f", Atom.of("a")), Var.of("Variable")),
            ),
            ClauseCase(
                "directive-f-of-a-and-variable-again",
                Goal.of(Struct.of("f", Atom.of("a")), Var.of("Variable")),
            ),
            ClauseCase(
                "directive-a-of-anonymous-and-b-of-anonymous",
                Goal.of(Struct.of("a", Var.anonymous()), Struct.of("b", Var.anonymous())),
            ),
            ClauseCase(
                "directive-a-of-a-and-empty-block",
                Goal.of(Struct.of("a", Atom.of("a")), Empty.block()),
            ),
            ClauseCase(
                "directive-a-of-a-and-other-of-anonymous",
                Goal.of(Struct.of("a", Atom.of("a")), Struct.of("other", Var.anonymous())),
            ),
            ClauseCase(
                "directive-a-of-a-and-a-of-anonymous",
                Goal.of(Struct.of("a", Atom.of("a")), Struct.of("a", Var.anonymous())),
            ),
            ClauseCase(
                "directive-a-of-a-and-anonymous",
                Goal.of(Struct.of("a", Atom.of("a")), Var.anonymous()),
            ),
        )

    internal val directives = directiveCases.map { it.clause }

    /** Contains queries and results crafted watching [directives] collection (NOTE: any modifications must be reviewed by hand)*/
    internal val directiveQueryCases =
        listOf(
            QueryCase("directive-true", Goal.of(Truth.TRUE), listOf(Goal.of(Truth.TRUE))),
            QueryCase("directive-empty-list", Goal.of(Empty.list()), emptyList()),
            QueryCase(
                "directive-a-of-a-and-any",
                Goal.of(Struct.of("a", Atom.of("a")), Var.anonymous()),
                directives.takeLast(5),
            ),
            matchingQuery(
                "directive-any-and-a-of-other",
                Goal.of(Var.anonymous(), Struct.of("a", Atom.of("other"))),
                directives,
            ),
            matchingQuery(
                "directive-a-of-any-and-b-of-any",
                Goal.of(Struct.of("a", Var.anonymous()), Struct.of("b", Var.anonymous())),
                directives,
            ),
        )

    /** Contains well-formed mixed [rules] and [directives] */
    internal val mixedClauseCases: List<ClauseCase<Clause>> = ruleCases + directiveCases

    internal val mixedClauses = mixedClauseCases.map { it.clause }

    /** Contains queries and results obtained by [ruleQueryCases] and [directiveQueryCases] */
    internal val mixedClauseQueryCases: List<QueryCase<Clause>> = ruleQueryCases + directiveQueryCases

    /** Asserts that [actualClauses] respect partial ordering (checking for Clauses head structural equality) imposed by [expectedClauses] iteration order */
    internal fun assertClauseHeadPartialOrderingRespected(
        expectedClauses: Iterable<Clause>,
        actualClauses: Iterable<Clause>,
    ) {
        actualClauses.toList() shouldContainExactlyInAnyOrder expectedClauses.toList()

        actualClauses.forEachStructurallyEqualsHead(
            partialOrderingHeadClauseMap(expectedClauses).toMutableMap(),
            onPresentEntry = { clause, entry ->
                when {
                    entry.value.none() -> fail("Clause $clause not indexed under its head Struct")
                    entry.value.first() == clause -> entry.setValue(entry.value - clause)
                    else ->
                        fail("Partial ordering not respected: $clause should come after these ${entry.value - clause}")
                }
            },
            onMissingEntry = { clause, _ -> fail("Clause $clause not expected among these: $expectedClauses") },
        )
    }

    /** Creates a Map containing for each structurallyEquals Clause.head the clauses ordered (according to [clauses] iteration order),
     * constructing the overall partial ordering */
    private fun partialOrderingHeadClauseMap(clauses: Iterable<Clause>): Map<Struct?, Iterable<Clause>> =
        mutableMapOf<Struct?, Iterable<Clause>>()
            .also { resultMap ->
                clauses.forEachStructurallyEqualsHead(
                    resultMap,
                    onPresentEntry = { clause, entry -> entry.setValue(entry.value + clause) },
                    onMissingEntry = { clause, map -> map[clause.head] = mutableListOf(clause) },
                )
            }.toMap()

    /** Utility function to iterate over clauses with partial ordering map, doing actions on found or missing entry */
    private inline fun Iterable<Clause>.forEachStructurallyEqualsHead(
        partialOrderingMap: MutableMap<Struct?, Iterable<Clause>>,
        onPresentEntry: (Clause, MutableMap.MutableEntry<Struct?, Iterable<Clause>>) -> Unit,
        onMissingEntry: (Clause, MutableMap<Struct?, Iterable<Clause>>) -> Unit,
    ) {
        forEach { clause ->
            partialOrderingMap.entries
                .find { (clauseHead, _) ->
                    clauseHead?.let { clause is Rule && it structurallyEquals clause.head }
                        ?: clause.isGoal
                }?.also { onPresentEntry(clause, it) } ?: onMissingEntry(clause, partialOrderingMap)
        }
    }
}
