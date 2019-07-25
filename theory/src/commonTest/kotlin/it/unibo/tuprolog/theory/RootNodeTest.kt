package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.theory.testutils.ReteTreeUtils
import it.unibo.tuprolog.theory.testutils.ReteTreeUtils.assertCorrectAndPartialOrderRespected
import it.unibo.tuprolog.theory.testutils.ReteTreeUtils.assertNoChangesInReteNode
import it.unibo.tuprolog.theory.testutils.ReteTreeUtils.assertRemovedFromReteNodeRespectingPartialOrder
import it.unibo.tuprolog.theory.testutils.ReteTreeUtils.assertReteNodeClausesCorrect
import it.unibo.tuprolog.theory.testutils.ReteTreeUtils.assertReteNodeEmpty
import kotlin.test.*

/**
 * Test class for [ReteTree.RootNode]
 *
 * @author Enrico
 */
internal class RootNodeTest {

    private lateinit var emptyRootNode: ReteTree.RootNode
    private lateinit var filledRootNode: ReteTree.RootNode

    private val aRule: Rule = Rule.of(Atom.of("a"), Var.of("A"))
    private val aDirective: Directive = Directive.of(Truth.`true`(), Var.of("B"))

    @BeforeTest
    fun init() {
        emptyRootNode = ReteTree.RootNode()
        filledRootNode = ReteTree.RootNode().apply { ReteTreeUtils.mixedClauses.forEach { put(it) } }
    }

    @Test
    fun emptyNodeHasEmptyChildrenCollection() {
        assertTrue { emptyRootNode.children.isEmpty() }
    }

    @Test
    fun filledNodeHasNonEmptyChildrenCollection() {
        assertTrue { filledRootNode.children.isNotEmpty() }
    }

    @Test
    fun clausesCorrect() {
        assertReteNodeEmpty(emptyRootNode)
        assertCorrectAndPartialOrderRespected(filledRootNode, ReteTreeUtils.mixedClauses)
    }

    @Test
    fun putClauseInsertsRule() {
        emptyRootNode.put(aRule)

        assertEquals(aRule, emptyRootNode.clauses.single())
    }

    @Test
    fun putClauseInsertsRelativelyAfterByDefault() {
        filledRootNode.put(aRule)
        filledRootNode.put(aDirective)

        assertCorrectAndPartialOrderRespected(filledRootNode, ReteTreeUtils.mixedClauses + listOf(aRule, aDirective))
    }

    @Test
    fun putClauseInsertsRelativelyBeforeAllIfSpecified() {
        filledRootNode.put(aDirective, true)
        filledRootNode.put(aRule, true)

        assertCorrectAndPartialOrderRespected(filledRootNode,
                ReteTreeUtils.mixedClauses.toMutableList().apply {
                    addAll(0, listOf(aRule, aDirective))
                })
    }

    @Test
    fun getClause() {
        ReteTreeUtils.mixedClausesQueryResultsMap.forEach { (query, result) ->
            assertEquals(result, filledRootNode.get(query).toList())
        }
    }

    @Test
    fun removeClauseWithZeroLimitDoesNothing() {
        ReteTreeUtils.mixedClausesQueryResultsMap.forEach { (query, _) ->
            assertNoChangesInReteNode(filledRootNode) { remove(query, 0) }
        }
    }

    @Test
    fun removeClauseFromEmptyNodeDoesNothing() {
        assertNoChangesInReteNode(emptyRootNode) { remove(aRule) }
    }

    @Test
    fun removeClauseWithLimitWorksAsExpected() {
        for (limit in 0..10) {
            ReteTreeUtils.mixedClausesQueryResultsMap.forEach { (query, allMatching) ->
                init() // because removal of side-effects is needed

                assertRemovedFromReteNodeRespectingPartialOrder(filledRootNode, allMatching,limit) { remove(query, limit) }
            }
        }
    }

    @Test
    fun removeClauseWithNegativeLimitRemovesAllMatchingRules() {
        val negativeLimit = -1
        ReteTreeUtils.mixedClausesQueryResultsMap.forEach { (query, allMatching) ->
            init() // because removal of side-effects is needed

            assertRemovedFromReteNodeRespectingPartialOrder(filledRootNode, allMatching) { remove(query, negativeLimit) }
        }
    }

    @Test
    fun removeAllClause() {
        ReteTreeUtils.mixedClausesQueryResultsMap.forEach { (query, allMatching) ->
            init() // because removal of side-effects is needed

            assertRemovedFromReteNodeRespectingPartialOrder(filledRootNode, allMatching) { removeAll(query) }
        }
    }

    @Test
    fun deepCopyCreatesIndependentInstance() {
        emptyRootNode.put(aRule)

        val independentCopy = emptyRootNode.deepCopy()

        assertReteNodeClausesCorrect(emptyRootNode, listOf(aRule))
        assertReteNodeClausesCorrect(independentCopy, listOf(aRule))

        emptyRootNode.remove(aRule)

        assertReteNodeClausesCorrect(emptyRootNode, emptyList())
        assertReteNodeClausesCorrect(independentCopy, listOf(aRule))
    }

    @Test
    fun deepCopyDoesNotCopyIndexedInstances() {
        emptyRootNode.put(aRule)

        val independentCopy = emptyRootNode.deepCopy()

        assertSame(emptyRootNode.clauses.single(), independentCopy.clauses.single())
    }

}
