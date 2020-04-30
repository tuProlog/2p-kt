package it.unibo.tuprolog.theory.rete.nodes

import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.theory.testutils.ReteNodeUtils
import it.unibo.tuprolog.theory.testutils.ReteNodeUtils.assertNoChangesInReteNode
import it.unibo.tuprolog.theory.testutils.ReteNodeUtils.assertRemovedFromReteNode
import it.unibo.tuprolog.theory.testutils.ReteNodeUtils.assertReteNodeClausesCorrect
import it.unibo.tuprolog.theory.testutils.ReteNodeUtils.assertReteNodeEmpty
import kotlin.test.*

/**
 * Test class for [DirectiveNode]
 *
 * @author Enrico
 */
internal class DirectiveNodeTest {

    private lateinit var emptyDirectiveNode: DirectiveNode
    private lateinit var filledDirectiveNode: DirectiveNode

    private val aDirective: Directive = Directive.of(Truth.TRUE, Var.of("B"))

    @BeforeTest
    fun init() {
        emptyDirectiveNode = DirectiveNode()
        filledDirectiveNode = DirectiveNode().apply { ReteNodeUtils.directives.forEach { put(it) } }
    }

    @Test
    fun childrenEmpty() {
        assertTrue(emptyDirectiveNode.children.isEmpty())
        assertTrue(filledDirectiveNode.children.isEmpty())
    }

    @Test
    fun clausesCorrect() {
        assertReteNodeEmpty(emptyDirectiveNode)
        assertReteNodeClausesCorrect(filledDirectiveNode, ReteNodeUtils.directives)
    }

    @Test
    fun putClauseInsertsDirective() {
        emptyDirectiveNode.put(aDirective)

        assertEquals(aDirective, emptyDirectiveNode.indexedElements.single())
    }

    @Test
    fun putClauseInsertsAfterByDefault() {
        filledDirectiveNode.put(aDirective)

        assertEquals(aDirective, filledDirectiveNode.indexedElements.last())
    }

    @Test
    fun putClauseInsertsBeforeAllIfSpecified() {
        filledDirectiveNode.put(aDirective, true)

        assertEquals(aDirective, filledDirectiveNode.indexedElements.first())
    }

    @Test
    fun getClause() {
        ReteNodeUtils.directivesQueryResultsMap.forEach { (query, result) ->
            assertEquals(result, filledDirectiveNode.get(query).toList())
        }
    }

    @Test
    fun removeClauseWithZeroLimitDoesNothing() {
        ReteNodeUtils.directivesQueryResultsMap.forEach { (query, _) ->
            assertNoChangesInReteNode(filledDirectiveNode) { remove(query, 0) }
        }
    }

    @Test
    fun removeClauseFromEmptyNodeDoesNothing() {
        assertNoChangesInReteNode(emptyDirectiveNode) { remove(aDirective) }
    }

    @Test
    fun removeClauseWithLimitWorksAsExpected() {
        for (limit in 0..10) {
            ReteNodeUtils.directivesQueryResultsMap.forEach { (query, allMatching) ->
                init() // because removal of side-effects is needed

                assertRemovedFromReteNode(filledDirectiveNode, allMatching.take(limit)) { remove(query, limit) }
            }
        }
    }

    @Test
    fun removeClauseWithNegativeLimitRemovesAllMatchingDirectives() {
        val negativeLimit = -1
        ReteNodeUtils.directivesQueryResultsMap.forEach { (query, allMatching) ->
            init() // because removal of side-effects is needed

            assertRemovedFromReteNode(filledDirectiveNode, allMatching) { remove(query, negativeLimit) }
        }
    }

    @Test
    fun removeAllClause() {
        ReteNodeUtils.directivesQueryResultsMap.forEach { (query, allMatching) ->
            init() // because removal of side-effects is needed

            assertRemovedFromReteNode(filledDirectiveNode, allMatching) { removeAll(query) }
        }
    }

    @Test
    fun deepCopyCreatesIndependentInstance() {
        emptyDirectiveNode.put(aDirective)

        val independentCopy = emptyDirectiveNode.deepCopy()

        assertReteNodeClausesCorrect(emptyDirectiveNode, listOf(aDirective))
        assertReteNodeClausesCorrect(independentCopy, listOf(aDirective))

        emptyDirectiveNode.remove(aDirective)

        assertReteNodeClausesCorrect(emptyDirectiveNode, emptyList())
        assertReteNodeClausesCorrect(independentCopy, listOf(aDirective))
    }

    @Test
    fun deepCopyDoesNotCopyIndexedInstances() {
        emptyDirectiveNode.put(aDirective)

        val independentCopy = emptyDirectiveNode.deepCopy()

        assertSame(emptyDirectiveNode.indexedElements.single(), independentCopy.indexedElements.single())
    }

}
