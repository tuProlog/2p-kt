package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.theory.testutils.ReteTreeUtils
import it.unibo.tuprolog.theory.testutils.ReteTreeUtils.assertNoChangesInReteNode
import it.unibo.tuprolog.theory.testutils.ReteTreeUtils.assertRemovedFromReteNode
import it.unibo.tuprolog.theory.testutils.ReteTreeUtils.assertReteNodeClausesCorrect
import it.unibo.tuprolog.theory.testutils.ReteTreeUtils.assertReteNodeEmpty
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [ReteTree.RuleNode]
 *
 * @author Enrico
 */
internal class RuleNodeTest {

    private lateinit var emptyRuleNode: ReteTree.RuleNode
    private lateinit var filledRuleNode: ReteTree.RuleNode

    private val aRule: Rule = Fact.of(Atom.of("a"))
    private val aDirective: Directive = Directive.of(Truth.`true`())

    @BeforeTest
    fun init() {
        emptyRuleNode = ReteTree.RuleNode()
        filledRuleNode = ReteTree.RuleNode().apply { ReteTreeUtils.rules.forEach { put(it) } }
    }

    @Test
    fun clausesCorrect() {
        assertReteNodeEmpty(emptyRuleNode)
        assertReteNodeClausesCorrect(filledRuleNode, ReteTreeUtils.rules)
    }

    @Test
    fun putClauseInsertsRule() {
        emptyRuleNode.put(aRule)

        assertEquals(aRule, emptyRuleNode.clauses.single())
    }

    @Test
    fun putClauseDoesntInsertDirective() {
        emptyRuleNode.put(aDirective)

        assertReteNodeEmpty(emptyRuleNode)
    }

    @Test
    fun putClauseInsertsAfterByDefault() {
        filledRuleNode.put(aRule)

        assertEquals(aRule, filledRuleNode.clauses.last())
    }

    @Test
    fun putClauseInsertsBeforeAllIfSpecified() {
        filledRuleNode.put(aRule, true)

        assertEquals(aRule, filledRuleNode.clauses.first())
    }

    @Test
    fun getClause() {
        ReteTreeUtils.rulesQueryResultsMap.forEach { (query, result) ->
            assertEquals(result, filledRuleNode.get(query).toList())
        }
    }

    @Test
    fun removeClauseWithZeroLimitDoesNothing() {
        ReteTreeUtils.rulesQueryResultsMap.forEach { (query, _) ->
            assertNoChangesInReteNode(filledRuleNode) { remove(query, 0) }
        }
    }

    @Test
    fun removeClauseFromEmptyNodeDoesNothing() {
        assertNoChangesInReteNode(emptyRuleNode) { remove(aRule) }
    }

    @Test
    fun removeClauseWithLimitWorksAsExpected() {
        for (limit in 0..10) {
            ReteTreeUtils.rulesQueryResultsMap.forEach { (query, allMatching) ->
                init() // because removal of side-effects is needed

                assertRemovedFromReteNode(filledRuleNode, allMatching.take(limit)) { remove(query, limit) }
            }
        }
    }

    @Test
    fun removeClauseWithNegativeLimitRemovesAllMatchingRules() {
        val negativeLimit = -1
        ReteTreeUtils.rulesQueryResultsMap.forEach { (query, allMatching) ->
            init() // because removal of side-effects is needed

            assertRemovedFromReteNode(filledRuleNode, allMatching) { remove(query, negativeLimit) }
        }
    }

    @Test
    fun removeAllClause() {
        ReteTreeUtils.rulesQueryResultsMap.forEach { (query, allMatching) ->
            init() // because removal of side-effects is needed

            assertRemovedFromReteNode(filledRuleNode, allMatching) { removeAll(query) }
        }
    }

    @Test
    fun deepCopyCreatesIndependentInstance() {
        emptyRuleNode.put(aRule)

        val independentCopy = emptyRuleNode.deepCopy()

        assertReteNodeClausesCorrect(emptyRuleNode, listOf(aRule))
        assertReteNodeClausesCorrect(independentCopy, listOf(aRule))

        emptyRuleNode.remove(aRule)

        assertReteNodeClausesCorrect(emptyRuleNode, emptyList())
        assertReteNodeClausesCorrect(independentCopy, listOf(aRule))
    }
}
