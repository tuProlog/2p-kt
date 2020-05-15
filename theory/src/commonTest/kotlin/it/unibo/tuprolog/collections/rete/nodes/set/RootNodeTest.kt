package it.unibo.tuprolog.collections.rete.nodes.set

import it.unibo.tuprolog.collections.rete.generic.ReteNode
import it.unibo.tuprolog.collections.rete.generic.set.RootNode
import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.testutils.ReteNodeUtils
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertCorrectAndPartialOrderRespected
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertNoChangesInReteNode
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertRemovedFromReteNodeRespectingPartialOrder
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertReteNodeClausesCorrect
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertReteNodeEmpty
import kotlin.test.*

/**
 * Test class for [RootNode]
 *
 * @author Enrico
 */
internal class RootNodeTest {

    private lateinit var emptyRootNode: RootNode
    private lateinit var filledRootNode: RootNode

    private val aRule: Rule = Rule.of(Atom.of("a"), Var.of("A"))
    private val aDirective: Directive = Directive.of(Truth.TRUE, Var.of("B"))

    private val reteTreeTestContents by lazy {
        listOf(
            ReteNodeUtils.rules,
            ReteNodeUtils.directives,
            ReteNodeUtils.mixedClauses
        )
    }

    private val correctInstances = reteTreeTestContents.map { clauses ->
        RootNode().apply { clauses.forEach { put(it) } }
    }

    @BeforeTest
    fun init() {
        emptyRootNode = RootNode()
        filledRootNode = RootNode().apply { ReteNodeUtils.mixedClauses.forEach { put(it) } }
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
        assertCorrectAndPartialOrderRespected(filledRootNode, ReteNodeUtils.mixedClauses)
    }

    @Test
    fun putClauseInsertsRule() {
        emptyRootNode.put(aRule)

        assertEquals(aRule, emptyRootNode.indexedElements.single())
    }

    @Test
    fun putClauseInsertsRelativelyAfterByDefault() {
        filledRootNode.put(aRule)
        filledRootNode.put(aDirective)

        assertCorrectAndPartialOrderRespected(filledRootNode, ReteNodeUtils.mixedClauses + listOf(aRule, aDirective))
    }

    @Test
    fun putClauseInsertsRelativelyBeforeAllIfSpecified() {
        filledRootNode.put(aDirective, true)
        filledRootNode.put(aRule, true)

        assertCorrectAndPartialOrderRespected(filledRootNode,
            ReteNodeUtils.mixedClauses.toMutableList().apply {
                addAll(0, listOf(aRule, aDirective))
            })
    }

    @Test
    fun getClause() {
        ReteNodeUtils.mixedClausesQueryResultsMap.forEach { (query, result) ->
            assertEquals(result, filledRootNode.get(query).toList())
        }
    }

    @Test
    fun removeClauseWithZeroLimitDoesNothing() {
        ReteNodeUtils.mixedClausesQueryResultsMap.forEach { (query, _) ->
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
            ReteNodeUtils.mixedClausesQueryResultsMap.forEach { (query, allMatching) ->
                init() // because removal of side-effects is needed

                assertRemovedFromReteNodeRespectingPartialOrder(filledRootNode, allMatching, limit) {
                    remove(query, limit)
                }
            }
        }
    }

    @Test
    fun removeClauseWithNegativeLimitRemovesAllMatchingRules() {
        val negativeLimit = -1
        ReteNodeUtils.mixedClausesQueryResultsMap.forEach { (query, allMatching) ->
            init() // because removal of side-effects is needed

            assertRemovedFromReteNodeRespectingPartialOrder(filledRootNode, allMatching) {
                remove(query, negativeLimit)
            }
        }
    }

    @Test
    fun removeAllClause() {
        ReteNodeUtils.mixedClausesQueryResultsMap.forEach { (query, allMatching) ->
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

        assertSame(emptyRootNode.indexedElements.single(), independentCopy.indexedElements.single())
    }

    @Test
    fun reteTreeOfIterableCreatesCorrectInstances() {
        val toBeTested = reteTreeTestContents.map { ReteNode.ofSet(it) }

        assertEquals(correctInstances, toBeTested)
    }

    @Test
    fun reteTreeOfVarargsCreatesCorrectInstances() {
        val toBeTested = reteTreeTestContents.map { ReteNode.ofSet(*it.toTypedArray()) }

        assertEquals(correctInstances, toBeTested)
    }

}
