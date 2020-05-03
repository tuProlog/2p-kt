package it.unibo.tuprolog.collections.rete.nodes.set

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.testutils.ReteNodeUtils
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertNoChangesInReteNode
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertRemovedFromReteNode
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertReteNodeClausesCorrect
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertReteNodeEmpty
import kotlin.test.*

/**
 * Test class for [RuleNode]
 *
 * @author Enrico
 */
internal class RuleNodeTest {

    private lateinit var emptyRuleNode: RuleNode
    private lateinit var filledRuleNode: RuleNode

    private val aRule: Rule = Rule.of(Atom.of("a"), Var.of("A"))

    @BeforeTest
    fun init() {
        emptyRuleNode = RuleNode()
        filledRuleNode = RuleNode().apply { ReteNodeUtils.rules.forEach { put(it) } }
    }

    @Test
    fun childrenEmpty() {
        assertTrue(emptyRuleNode.children.isEmpty())
        assertTrue(filledRuleNode.children.isEmpty())
    }

    @Test
    fun clausesCorrect() {
        assertReteNodeEmpty(emptyRuleNode)
        assertReteNodeClausesCorrect(filledRuleNode, ReteNodeUtils.rules)
    }

    @Test
    fun putClauseInsertsRule() {
        emptyRuleNode.put(aRule)

        assertEquals(aRule, emptyRuleNode.indexedElements.single())
    }

    @Test
    fun putClauseInsertsAfterByDefault() {
        filledRuleNode.put(aRule)

        assertEquals(aRule, filledRuleNode.indexedElements.last())
    }

    @Test
    fun putClauseInsertsBeforeAllIfSpecified() {
        filledRuleNode.put(aRule, true)

        assertEquals(aRule, filledRuleNode.indexedElements.first())
    }

    @Test
    fun getClause() {
        ReteNodeUtils.rulesQueryResultsMap.forEach { (query, result) ->
            assertEquals(result, filledRuleNode.get(query).toList())
        }
    }

    @Test
    fun removeClauseWithZeroLimitDoesNothing() {
        ReteNodeUtils.rulesQueryResultsMap.forEach { (query, _) ->
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
            ReteNodeUtils.rulesQueryResultsMap.forEach { (query, allMatching) ->
                init() // because removal of side-effects is needed

                assertRemovedFromReteNode(filledRuleNode, allMatching.take(limit)) { remove(query, limit) }
            }
        }
    }

    @Test
    fun removeClauseWithNegativeLimitRemovesAllMatchingRules() {
        val negativeLimit = -1
        ReteNodeUtils.rulesQueryResultsMap.forEach { (query, allMatching) ->
            init() // because removal of side-effects is needed

            assertRemovedFromReteNode(filledRuleNode, allMatching) { remove(query, negativeLimit) }
        }
    }

    @Test
    fun removeAllClause() {
        ReteNodeUtils.rulesQueryResultsMap.forEach { (query, allMatching) ->
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

    @Test
    fun deepCopyDoesNotCopyIndexedInstances() {
        emptyRuleNode.put(aRule)

        val independentCopy = emptyRuleNode.deepCopy()

        assertSame(emptyRuleNode.indexedElements.single(), independentCopy.indexedElements.single())
    }
}
