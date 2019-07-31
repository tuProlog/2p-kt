package it.unibo.tuprolog.theory.rete.clause

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.theory.testutils.ReteNodeUtils
import it.unibo.tuprolog.theory.testutils.ReteNodeUtils.assertClauseHeadPartialOrderingRespected
import it.unibo.tuprolog.theory.testutils.ReteNodeUtils.assertCorrectAndPartialOrderRespected
import it.unibo.tuprolog.theory.testutils.ReteNodeUtils.assertNoChangesInReteNode
import it.unibo.tuprolog.theory.testutils.ReteNodeUtils.assertRemovedFromReteNodeRespectingPartialOrder
import it.unibo.tuprolog.theory.testutils.ReteNodeUtils.assertReteNodeClausesCorrect
import it.unibo.tuprolog.theory.testutils.ReteNodeUtils.assertReteNodeEmpty
import kotlin.test.*

/**
 * Test class for [FunctorNode]
 *
 * @author Enrico
 */
internal class FunctorNodeTest {

    private lateinit var emptyAFunctorNode: FunctorNode
    private lateinit var filledAFunctorNode: FunctorNode

    private val aRule: Rule = Rule.of(Atom.of("a"), Var.of("A"))

    @BeforeTest
    fun init() {
        emptyAFunctorNode = FunctorNode("a")
        filledAFunctorNode = FunctorNode("a").apply { ReteNodeUtils.aFunctorRules.forEach { put(it) } }
    }

    @Test
    fun emptyNodeHasEmptyChildrenCollection() {
        assertTrue { emptyAFunctorNode.children.isEmpty() }
    }

    @Test
    fun filledNodeHasNonEmptyChildrenCollection() {
        assertTrue { filledAFunctorNode.children.isNotEmpty() }
    }

    @Test
    fun clausesCorrect() {
        assertReteNodeEmpty(emptyAFunctorNode)
        assertCorrectAndPartialOrderRespected(filledAFunctorNode, ReteNodeUtils.aFunctorRules)
    }

    @Test
    fun putClauseInsertsRule() {
        emptyAFunctorNode.put(aRule)

        assertEquals(aRule, emptyAFunctorNode.indexedElements.single())
    }

    @Test
    fun putClauseDoesntInsertWrongFunctorRule() {
        ReteNodeUtils.fFunctorRules.forEach { emptyAFunctorNode.put(it) }

        assertReteNodeEmpty(emptyAFunctorNode)
    }

    @Test
    fun putClauseInsertsRelativelyAfterByDefault() {
        filledAFunctorNode.put(aRule)

        assertCorrectAndPartialOrderRespected(filledAFunctorNode, ReteNodeUtils.aFunctorRules + aRule)
    }

    @Test
    fun putClauseInsertsRelativelyBeforeAllIfSpecified() {
        filledAFunctorNode.put(aRule, true)

        assertCorrectAndPartialOrderRespected(filledAFunctorNode, ReteNodeUtils.aFunctorRules.toMutableList().apply { add(0, aRule) })
    }

    @Test
    fun getClause() {
        ReteNodeUtils.aFunctorRulesQueryResultsMap.forEach { (query, result) ->
            assertClauseHeadPartialOrderingRespected(result, filledAFunctorNode.get(query).toList())
        }
    }

    @Test
    fun removeClauseWithZeroLimitDoesNothing() {
        ReteNodeUtils.aFunctorRulesQueryResultsMap.forEach { (query, _) ->
            assertNoChangesInReteNode(filledAFunctorNode) { remove(query, 0) }
        }
    }

    @Test
    fun removeClauseFromEmptyNodeDoesNothing() {
        assertNoChangesInReteNode(emptyAFunctorNode) { remove(aRule) }
    }

    @Test
    fun removeClauseWithLimitWorksAsExpected() {
        for (limit in 0..10) {
            ReteNodeUtils.aFunctorRulesQueryResultsMap.forEach { (query, allMatching) ->
                init() // because removal of side-effects is needed

                assertRemovedFromReteNodeRespectingPartialOrder(filledAFunctorNode, allMatching, limit) { remove(query, limit) }
            }
        }
    }

    @Test
    fun removeClauseWithNegativeLimitRemovesAllMatchingRules() {
        val negativeLimit = -1
        ReteNodeUtils.aFunctorRulesQueryResultsMap.forEach { (query, allMatching) ->
            init() // because removal of side-effects is needed

            assertRemovedFromReteNodeRespectingPartialOrder(filledAFunctorNode, allMatching) { remove(query, negativeLimit) }
        }
    }

    @Test
    fun removeAllClause() {
        ReteNodeUtils.aFunctorRulesQueryResultsMap.forEach { (query, allMatching) ->
            init() // because removal of side-effects is needed

            assertRemovedFromReteNodeRespectingPartialOrder(filledAFunctorNode, allMatching) { removeAll(query) }
        }
    }

    @Test
    fun deepCopyCreatesIndependentInstance() {
        emptyAFunctorNode.put(aRule)

        val independentCopy = emptyAFunctorNode.deepCopy()

        assertReteNodeClausesCorrect(emptyAFunctorNode, listOf(aRule))
        assertReteNodeClausesCorrect(independentCopy, listOf(aRule))

        emptyAFunctorNode.remove(aRule)

        assertReteNodeClausesCorrect(emptyAFunctorNode, emptyList())
        assertReteNodeClausesCorrect(independentCopy, listOf(aRule))
    }

    @Test
    fun deepCopyDoesNotCopyIndexedInstances() {
        emptyAFunctorNode.put(aRule)

        val independentCopy = emptyAFunctorNode.deepCopy()

        assertSame(emptyAFunctorNode.indexedElements.single(), independentCopy.indexedElements.single())
    }
}
