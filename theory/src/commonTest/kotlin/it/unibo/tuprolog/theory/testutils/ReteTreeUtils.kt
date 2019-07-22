package it.unibo.tuprolog.theory.testutils

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.theory.ReteTree
import it.unibo.tuprolog.unify.Unification.Companion.matches
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

    /** Contains a map of queries and results crafted watching [rules] collection (NOTE: any modifications must be reviewed by hand)*/
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

//    internal fun assertRemovedFromReteNodeRespectingPartialOrder(reteNode: ReteTree<*>, matchToRemove: Iterable<Clause>, removeLimit: Int, removeAction: ReteTree<*>.() -> Sequence<Clause>) {
//
//    }

//    internal fun partialOrderingHeadRuleMap(rules: Iterable<Rule>): Map<Struct, Iterable<Rule>> =
//            mutableMapOf<Struct, Iterable<Rule>>().also { resultMap ->
//                rules.forEach { rule ->
//                    resultMap.entries.find { (ruleHead, _) -> ruleHead structurallyEquals rule.head }?.also {
//                        it.setValue(it.value + rule)
//                    } ?: resultMap.put(rule.head, mutableListOf(rule))
//                }
//            }.toMap()

    /** Asserts that [actualRules] respect partial ordering (checking for Rules head structural equality) imposed by [expectedRules] iteration order */
    internal fun assertRuleHeadPartialOrderingRespected(expectedRules: Iterable<Rule>, actualRules: Iterable<Rule>) {
//        assertEquals(expectedRules.toList().sorted(), reteNode.clauses.toList().sorted()) TODO enable after solving issue #29 and delete two below assertions
        assertTrue(expectedRules.toList().containsAll(actualRules.toList()))
        assertTrue(actualRules.toList().containsAll(expectedRules.toList()))

//        val partialOrderingHeadRuleMap = partialOrderingHeadRuleMap(expectedRules).toMutableMap()
//        actualRules.forEach { rule ->
//            partialOrderingHeadRuleMap.entries.find { (ruleHead, _) -> ruleHead structurallyEquals rule.head }?.also {
//                when {
//                    it.value.none() -> fail("Rule $rule not indexed under its head Struct")
//                    it.value.first() == rule -> it.setValue(it.value - rule)
//                    else -> fail("Partial ordering not respected: $rule came before one of these ${it.value}")
//                }
//            } ?: fail("Rule $rule not expected among these: $expectedRules")
//        }

        val supportIndexesMap = mutableMapOf<Struct, Int>()
        actualRules.forEach { rule ->
            supportIndexesMap.entries.find { (ruleHead, _) -> ruleHead structurallyEquals rule.head }?.also {
                val expectedList = expectedRules.toList()

                val alreadyPresentIndex = it.value
                val subsequentIndex = expectedList.indexOf(rule)

                if (alreadyPresentIndex > subsequentIndex) {
                    fail("Partial ordering between `${expectedList[alreadyPresentIndex]}` and `${expectedList[subsequentIndex]}` " +
                            "not respected; the first should come after the second")
                } else it.setValue(subsequentIndex)

            } ?: supportIndexesMap.put(rule.head, expectedRules.indexOf(rule))
        }
    }

    /** Asserts that ReteTree node respects partial ordering (checking for Rules head structural equality) imposed by [expectedRules] iteration order */
    internal fun assertCorrectAndPartialOrderRespected(reteNode: ReteTree<*>, expectedRules: Iterable<Rule>) =
            assertRuleHeadPartialOrderingRespected(expectedRules, reteNode.clauses.map { it as Rule }.asIterable())

}
