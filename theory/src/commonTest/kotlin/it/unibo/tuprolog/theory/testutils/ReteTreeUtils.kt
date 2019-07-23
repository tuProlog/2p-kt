package it.unibo.tuprolog.theory.testutils

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.theory.ReteTree
import it.unibo.tuprolog.unify.Unification.Companion.matches
import kotlin.math.min
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Utils singleton for testing [ReteTree]
 *
 * @author Enrico
 */
internal object ReteTreeUtils {

    /** Contains some well-formed rules */
    internal val rules by lazy {
        listOf(
                Fact.of(Truth.`true`()),
                Fact.of(Truth.fail()),
                Fact.of(Atom.of("a")),
                Fact.of(Atom.of("other")),
                Fact.of(Struct.of("a", Atom.of("other"))),
                Fact.of(Struct.of("other", Integer.of(1))),
                Rule.of(Atom.of("a"), Atom.of("other")),
                Rule.of(Struct.of("a", Atom.of("other")), Atom.of("a")),
                Rule.of(Struct.of("f", Atom.of("a"), Struct.of("b", Var.of("X")), Atom.of("do_something_else"))),
                Rule.of(Struct.of("a", Integer.of(22)), Var.anonymous()),
                Rule.of(Struct.of("f", Atom.of("a")), Var.of("Variable")),
                Rule.of(Struct.of("f", Atom.of("a")), Var.of("Variable")),
                Rule.of(Struct.of("a", Var.anonymous()), Struct.of("b", Var.anonymous())),
                Rule.of(Struct.of("a", Atom.of("a")), Empty.set()),
                Rule.of(Struct.of("a", Atom.of("a")), Struct.of("other", Var.anonymous())),
                Rule.of(Struct.of("a", Atom.of("a")), Struct.of("a", Var.anonymous())),
                Rule.of(Struct.of("a", Atom.of("a")), Var.anonymous())
        )
    }

    /** Contains some well-formed rules with no args head */
    internal val noArgHeadedRules by lazy { rules.filter { it.head.isAtom } }

    /** Contains some well-formed wules with "a" functor */
    internal val aFunctorRules by lazy { rules.filter { it.head.functor == "a" } }

    /** Contains some well-formed wules with "f" functor */
    internal val fFunctorRules by lazy { rules.filter { it.head.functor == "f" } }

    /** Contains a map of queries and results crafted watching [rules] collection (NOTE: any modifications must be reviewed by hand) */
    internal val rulesQueryResultsMap by lazy {
        mapOf(
                Fact.of(Truth.`true`()) to listOf(Fact.of(Truth.`true`())),
                Fact.of(Empty.list()) to emptyList(),
                Rule.of(Struct.of("a", Atom.of("a")), Var.anonymous()) to rules.takeLast(5),
                Rule.of(Struct.of("a", Atom.of("other")), Var.anonymous()).run {
                    this to rules.filter { it matches this }
                },
                Rule.of(Struct.of("a", Var.anonymous()), Struct.of("b", Var.anonymous())).run {
                    this to rules.filter { it matches this }
                }
        )
    }

    /** Contains a map of queries and results made excluding queries with non "a" functor from [rulesQueryResultsMap] */
    internal val aFunctorRulesQueryResultsMap by lazy { rulesQueryResultsMap.filterKeys { it.head.functor == "a" } }

    /** Contains some well-formed directives */
    internal val directives by lazy {
        listOf(
                Directive.of(Truth.`true`()),
                Directive.of(Truth.fail()),
                Directive.of(Atom.of("a")),
                Directive.of(Atom.of("other")),
                Directive.of(Struct.of("a", Atom.of("other"))),
                Directive.of(Struct.of("other", Integer.of(1))),
                Directive.of(Atom.of("a"), Atom.of("other")),
                Directive.of(Struct.of("a", Atom.of("other")), Atom.of("a")),
                Directive.of(Struct.of("f", Atom.of("a"), Struct.of("b", Var.of("X")), Atom.of("do_something_else"))),
                Directive.of(Struct.of("a", Integer.of(22)), Var.anonymous()),
                Directive.of(Struct.of("f", Atom.of("a")), Var.of("Variable")),
                Directive.of(Struct.of("f", Atom.of("a")), Var.of("Variable")),
                Directive.of(Struct.of("a", Var.anonymous()), Struct.of("b", Var.anonymous())),
                Directive.of(Struct.of("a", Atom.of("a")), Empty.set()),
                Directive.of(Struct.of("a", Atom.of("a")), Struct.of("other", Var.anonymous())),
                Directive.of(Struct.of("a", Atom.of("a")), Struct.of("a", Var.anonymous())),
                Directive.of(Struct.of("a", Atom.of("a")), Var.anonymous())
        )
    }

    /** Contains a map of queries and results crafted watching [directives] collection (NOTE: any modifications must be reviewed by hand)*/
    internal val directivesQueryResultsMap by lazy {
        mapOf(
                Directive.of(Truth.`true`()) to listOf(Directive.of(Truth.`true`())),
                Directive.of(Empty.list()) to emptyList(),
                Directive.of(Struct.of("a", Atom.of("a")), Var.anonymous()) to directives.takeLast(5),
                Directive.of(Var.anonymous(), Struct.of("a", Atom.of("other"))).run {
                    this to directives.filter { it matches this }
                },
                Directive.of(Struct.of("a", Var.anonymous()), Struct.of("b", Var.anonymous())).run {
                    this to directives.filter { it matches this }
                }
        )
    }

    /** Asserts that rete node has correct elements count */
    internal fun assertReteNodeElementCount(reteNode: ReteTree<*>, expectedCount: Int) {
        assertEquals(expectedCount, reteNode.clauses.count())
    }

    /** Asserts that rete node is empty */
    internal fun assertReteNodeEmpty(reteNode: ReteTree<*>) {
        assertTrue(reteNode.clauses.none())
        assertTrue(reteNode.children.none())
    }

    /** Asserts that rete node clauses are the same as expected */
    internal fun assertReteNodeClausesCorrect(reteNode: ReteTree<*>, expectedClauses: Iterable<Clause>) {
        assertEquals(expectedClauses.toList(), reteNode.clauses.toList())
    }

    /** Asserts that calling [idempotentAction] onto [reteNode] results in no actual change */
    internal fun assertNoChangesInReteNode(reteNode: ReteTree<*>, idempotentAction: ReteTree<*>.() -> Sequence<Clause>) {
        val beforeContents = reteNode.clauses.toList()

        val idempotentActionResult = reteNode.idempotentAction()

        assertTrue(idempotentActionResult.none())
        assertReteNodeClausesCorrect(reteNode, beforeContents)
    }

    /** Asserts that calling [removeAction] onto [reteNode] results in [removedExpected] elements removed */
    internal fun assertRemovedFromReteNode(reteNode: ReteTree<*>, removedExpected: Iterable<Clause>, removeAction: ReteTree<*>.() -> Sequence<Clause>) {
        val allClauses = reteNode.clauses.asIterable()
        val allClauseCount = allClauses.count()
        val remainingClausesExpected = allClauses - removedExpected

        val removedActual = reteNode.removeAction()

        assertEquals(removedExpected, removedActual.toList())
        assertReteNodeElementCount(reteNode, allClauseCount - removedExpected.count())
        assertReteNodeClausesCorrect(reteNode, remainingClausesExpected)
    }

    /** Asserts that calling [removeAction] onto [reteNode] results in [toRemoveMatched] [removeLimit] elements to be removed,
     * respecting the partial ordering; this means that removed elements can be taken in every order BUT respecting the partial order */
    internal fun assertRemovedFromReteNodeRespectingPartialOrder(
            reteNode: ReteTree<*>,
            toRemoveMatched: Iterable<Rule>,
            removeLimit: Int = Int.MAX_VALUE,
            removeAction: ReteTree<*>.() -> Sequence<Clause>
    ) {
        val allClauses = reteNode.clauses.asIterable()
        val allClauseCount = allClauses.count()
        val correctNumberOfRemoved = min(toRemoveMatched.count(), removeLimit)

        val removedActualSequence = reteNode.removeAction()
        assertReteNodeElementCount(reteNode, allClauseCount - correctNumberOfRemoved)

        val removedActual = partialOrderingHeadRuleMap(removedActualSequence.map { it as Rule }.asIterable())
        val removeMatchExpected = partialOrderingHeadRuleMap(toRemoveMatched)

        val checkerMap = removedActual.mapValues {
            it.value.zip(removeMatchExpected[it.key] ?: emptyList())
        }
        val (actualRemovedList, expectedRemovedList) = checkerMap.values.flatten().unzip()

        assertEquals(expectedRemovedList, actualRemovedList)

        assertRuleHeadPartialOrderingRespected(
                allClauses.map { it as Rule } - expectedRemovedList,
                reteNode.clauses.asIterable().map { it as Rule }
        )
    }

    /** Asserts that [actualRules] respect partial ordering (checking for Rules head structural equality) imposed by [expectedRules] iteration order */
    internal fun assertRuleHeadPartialOrderingRespected(expectedRules: Iterable<Rule>, actualRules: Iterable<Rule>) {
//        assertEquals(expectedRules.toList().sorted(), reteNode.clauses.toList().sorted()) TODO enable after solving issue #29 and delete two below assertions
        assertTrue("\nExpected:\t$expectedRules\nActual:\t\t$actualRules") {
            expectedRules.toList().containsAll(actualRules.toList())
        }
        assertTrue(actualRules.toList().containsAll(expectedRules.toList()))

        actualRules.forEachStructurallyEqualsHead(partialOrderingHeadRuleMap(expectedRules).toMutableMap(),
                onPresentEntry = { rule, entry ->
                    when {
                        entry.value.none() -> fail("Rule $rule not indexed under its head Struct")
                        entry.value.first() == rule -> entry.setValue(entry.value - rule)
                        else -> fail("Partial ordering not respected: $rule should come after these ${entry.value - rule}")
                    }
                },
                onMissingEntry = { rule, _ -> fail("Rule $rule not expected among these: $expectedRules") }
        )
    }

    /** Asserts that ReteTree node respects partial ordering (checking for Rules head structural equality) imposed by [expectedRules] iteration order */
    internal fun assertCorrectAndPartialOrderRespected(reteNode: ReteTree<*>, expectedRules: Iterable<Rule>) =
            assertRuleHeadPartialOrderingRespected(expectedRules, reteNode.clauses.map { it as Rule }.asIterable())

    /** Creates a Map containing for each structurallyEquals Rule.head the rules ordered (according to [rules] iteration order),
     * constructing the overall partial ordering */
    private fun partialOrderingHeadRuleMap(rules: Iterable<Rule>): Map<Struct, Iterable<Rule>> =
            mutableMapOf<Struct, Iterable<Rule>>().also { resultMap ->
                rules.forEachStructurallyEqualsHead(resultMap,
                        onPresentEntry = { rule, entry -> entry.setValue(entry.value + rule) },
                        onMissingEntry = { rule, map -> map[rule.head] = mutableListOf(rule) }
                )
            }.toMap()


    /** Utility function to iterate over rules with partial ordering map, doing actions on found or missing entry */
    private fun Iterable<Rule>.forEachStructurallyEqualsHead(
            partialOrderingMap: MutableMap<Struct, Iterable<Rule>>,
            onPresentEntry: (Rule, MutableMap.MutableEntry<Struct, Iterable<Rule>>) -> Unit,
            onMissingEntry: (Rule, MutableMap<Struct, Iterable<Rule>>) -> Unit
    ) {
        forEach { rule ->
            partialOrderingMap.entries.find { (ruleHead, _) -> ruleHead structurallyEquals rule.head }?.also {
                onPresentEntry(rule, it)
            } ?: onMissingEntry(rule, partialOrderingMap)
        }
    }
}
