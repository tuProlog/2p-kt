package it.unibo.tuprolog.testutils

import it.unibo.tuprolog.collections.rete.generic.ReteNode
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Empty
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.Unificator.Companion.matches
import kotlin.math.min
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Utils singleton for testing [ReteNode]
 *
 * @author Enrico
 */
internal object ReteNodeUtils {
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

    /** Contains some well-formed rules with no args head */
    internal val noArgHeadedRuleCases = ruleCases.filter { it.clause.head.isAtom }

    internal val noArgHeadedRules = noArgHeadedRuleCases.map { it.clause }

    /** Contains some well-formed wules with "a" functor */
    internal val aFunctorRules = rules.filter { it.head.functor == "a" }

    /** Contains some well-formed wules with "f" functor */
    internal val fFunctorRules = rules.filter { it.head.functor == "f" }

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

    /** Contains queries and results made excluding queries with non "a" functor from [ruleQueryCases] */
    internal val aFunctorRuleQueryCases = ruleQueryCases.filter { it.query.head.functor == "a" }

    /** Contains some well-formed directives */
    internal val directiveCases =
        listOf(
            ClauseCase("directive-true", Directive.of(Truth.TRUE)),
            ClauseCase("directive-fail", Directive.of(Truth.FAIL)),
            ClauseCase("directive-a", Directive.of(Atom.of("a"))),
            ClauseCase("directive-other", Directive.of(Atom.of("other"))),
            ClauseCase("directive-a-of-other", Directive.of(Struct.of("a", Atom.of("other")))),
            ClauseCase("directive-other-of-1", Directive.of(Struct.of("other", Integer.of(1)))),
            ClauseCase("directive-a-and-other", Directive.of(Atom.of("a"), Atom.of("other"))),
            ClauseCase("directive-a-of-other-and-a", Directive.of(Struct.of("a", Atom.of("other")), Atom.of("a"))),
            ClauseCase(
                "directive-f-of-a-b-of-x-do-something-else",
                Directive.of(Struct.of("f", Atom.of("a"), Struct.of("b", Var.of("X")), Atom.of("do_something_else"))),
            ),
            ClauseCase(
                "directive-a-of-22-and-anonymous",
                Directive.of(Struct.of("a", Integer.of(22)), Var.anonymous()),
            ),
            ClauseCase(
                "directive-f-of-a-and-variable",
                Directive.of(Struct.of("f", Atom.of("a")), Var.of("Variable")),
            ),
            ClauseCase(
                "directive-f-of-a-and-variable-again",
                Directive.of(Struct.of("f", Atom.of("a")), Var.of("Variable")),
            ),
            ClauseCase(
                "directive-a-of-anonymous-and-b-of-anonymous",
                Directive.of(Struct.of("a", Var.anonymous()), Struct.of("b", Var.anonymous())),
            ),
            ClauseCase(
                "directive-a-of-a-and-empty-block",
                Directive.of(Struct.of("a", Atom.of("a")), Empty.block()),
            ),
            ClauseCase(
                "directive-a-of-a-and-other-of-anonymous",
                Directive.of(Struct.of("a", Atom.of("a")), Struct.of("other", Var.anonymous())),
            ),
            ClauseCase(
                "directive-a-of-a-and-a-of-anonymous",
                Directive.of(Struct.of("a", Atom.of("a")), Struct.of("a", Var.anonymous())),
            ),
            ClauseCase(
                "directive-a-of-a-and-anonymous",
                Directive.of(Struct.of("a", Atom.of("a")), Var.anonymous()),
            ),
        )

    internal val directives = directiveCases.map { it.clause }

    /** Contains queries and results crafted watching [directives] collection (NOTE: any modifications must be reviewed by hand)*/
    internal val directiveQueryCases =
        listOf(
            QueryCase("directive-true", Directive.of(Truth.TRUE), listOf(Directive.of(Truth.TRUE))),
            QueryCase("directive-empty-list", Directive.of(Empty.list()), emptyList()),
            QueryCase(
                "directive-a-of-a-and-any",
                Directive.of(Struct.of("a", Atom.of("a")), Var.anonymous()),
                directives.takeLast(5),
            ),
            matchingQuery(
                "directive-any-and-a-of-other",
                Directive.of(Var.anonymous(), Struct.of("a", Atom.of("other"))),
                directives,
            ),
            matchingQuery(
                "directive-a-of-any-and-b-of-any",
                Directive.of(Struct.of("a", Var.anonymous()), Struct.of("b", Var.anonymous())),
                directives,
            ),
        )

    /** Contains well-formed mixed [rules] and [directives] */
    internal val mixedClauseCases: List<ClauseCase<Clause>> = ruleCases + directiveCases

    internal val mixedClauses = mixedClauseCases.map { it.clause }

    /** Contains queries and results obtained by [ruleQueryCases] and [directiveQueryCases] */
    internal val mixedClauseQueryCases: List<QueryCase<Clause>> = ruleQueryCases + directiveQueryCases

    /** Asserts that rete node has correct elements count */
    internal fun assertReteNodeElementCount(
        reteNode: ReteNode<*, Clause>,
        expectedCount: Int,
    ) = assertEquals(expectedCount, reteNode.indexedElements.count())

    /** Asserts that rete node is empty */
    internal fun assertReteNodeEmpty(reteNode: ReteNode<*, out Clause>) {
        assertTrue(reteNode.indexedElements.none())
        assertTrue(reteNode.children.none())
    }

    /** Asserts that rete node clauses are the same as expected */
    internal fun assertReteNodeClausesCorrect(
        reteNode: ReteNode<*, out Clause>,
        expectedClauses: Iterable<Clause>,
    ) = assertEquals(expectedClauses.toList(), reteNode.indexedElements.toList())

    /** Asserts that calling [idempotentAction] onto [reteNode] results in no actual change */
    internal inline fun assertNoChangesInReteNode(
        reteNode: ReteNode<*, out Clause>,
        idempotentAction: ReteNode<*, Clause>.() -> Sequence<Clause>,
    ) {
        val beforeContents = reteNode.indexedElements.toList()

        @Suppress("UNCHECKED_CAST") // nothing will be inserted, so it's safe
        val idempotentActionResult = (reteNode as ReteNode<*, Clause>).idempotentAction()

        assertTrue(idempotentActionResult.none())
        assertReteNodeClausesCorrect(reteNode, beforeContents)
    }

    /** Asserts that calling [removeAction] onto [reteNode] results in [removedExpected] elements removed */
    internal inline fun assertRemovedFromReteNode(
        reteNode: ReteNode<*, out Clause>,
        removedExpected: Iterable<Clause>,
        removeAction: ReteNode<*, Clause>.() -> Sequence<Clause>,
    ) {
        val allClauses = reteNode.indexedElements.asIterable()
        val allClauseCount = allClauses.count()
        val remainingClausesExpected = allClauses - removedExpected

        @Suppress("UNCHECKED_CAST") // nothing will be inserted, so it's safe
        val removedActual = (reteNode as ReteNode<*, Clause>).removeAction()

        assertEquals(removedExpected, removedActual.toList())
        assertReteNodeElementCount(reteNode, allClauseCount - removedExpected.count())
        assertReteNodeClausesCorrect(reteNode, remainingClausesExpected)
    }

    /** Asserts that calling [removeAction] onto [reteNode] results in [toRemoveMatched] [removeLimit] elements to be removed,
     * respecting the partial ordering; this means that removed elements can be taken in every order BUT respecting the partial order */
    internal inline fun assertRemovedFromReteNodeRespectingPartialOrder(
        reteNode: ReteNode<*, out Clause>,
        toRemoveMatched: Iterable<Clause>,
        removeLimit: Int = Int.MAX_VALUE,
        removeAction: ReteNode<*, Clause>.() -> Sequence<Clause>,
    ) {
        val allClauses = reteNode.indexedElements.asIterable()
        val allClauseCount = allClauses.count()
        val correctNumberOfRemoved = min(toRemoveMatched.count(), removeLimit)

        @Suppress("UNCHECKED_CAST") // nothing will be inserted, so it's safe
        val removedActualSequence = (reteNode as ReteNode<*, Clause>).removeAction()
        assertReteNodeElementCount(reteNode, allClauseCount - correctNumberOfRemoved)

        val removedActual = partialOrderingHeadClauseMap(removedActualSequence.asIterable())
        val removeMatchExpected = partialOrderingHeadClauseMap(toRemoveMatched)

        val checkerMap =
            removedActual.mapValues {
                it.value.zip(removeMatchExpected[it.key] ?: emptyList())
            }
        val (actualRemovedList, expectedRemovedList) = checkerMap.values.flatten().unzip()

        assertEquals(expectedRemovedList, actualRemovedList)

        assertClauseHeadPartialOrderingRespected(
            allClauses - expectedRemovedList,
            reteNode.indexedElements.asIterable(),
        )
    }

    /** Asserts that [actualClauses] respect partial ordering (checking for Clauses head structural equality) imposed by [expectedClauses] iteration order */
    internal fun assertClauseHeadPartialOrderingRespected(
        expectedClauses: Iterable<Clause>,
        actualClauses: Iterable<Clause>,
    ) {
//        assertEquals(expectedClauses.toList().sorted(), reteNode.indexedElements.toList().sorted()) TODO enable after solving issue #29 and delete two below assertions
        assertTrue("\nExpected:\t$expectedClauses\nActual:\t\t$actualClauses") {
            expectedClauses.toList().containsAll(actualClauses.toList())
        }
        assertTrue(actualClauses.toList().containsAll(expectedClauses.toList()))

        actualClauses.forEachStructurallyEqualsHead(
            partialOrderingHeadClauseMap(expectedClauses).toMutableMap(),
            onPresentEntry = { clause, entry ->
                when {
                    entry.value.none() -> fail("Clause $clause not indexed under its head Struct")
                    entry.value.first() == clause -> entry.setValue(entry.value - clause)
                    else ->
                        fail(
                            "Partial ordering not respected: $clause should come after these ${entry.value - clause}",
                        )
                }
            },
            onMissingEntry = { clause, _ -> fail("Clause $clause not expected among these: $expectedClauses") },
        )
    }

    /** Asserts that ReteTree node respects partial ordering (checking for Clauses head structural equality) imposed by [expectedClauses] iteration order */
    internal fun assertCorrectAndPartialOrderRespected(
        reteNode: ReteNode<*, out Clause>,
        expectedClauses: Iterable<Clause>,
    ) = assertClauseHeadPartialOrderingRespected(expectedClauses, reteNode.indexedElements.asIterable())

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
                        ?: clause.isDirective
                }?.also { onPresentEntry(clause, it) } ?: onMissingEntry(clause, partialOrderingMap)
        }
    }
}
