package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.theory.testutils.ReteTreeUtils
import it.unibo.tuprolog.theory.testutils.ReteTreeUtils.assertNoChangesInReteNode
import it.unibo.tuprolog.theory.testutils.ReteTreeUtils.assertRemovedFromReteNode
import it.unibo.tuprolog.theory.testutils.ReteTreeUtils.assertReteNodeClausesCorrect
import it.unibo.tuprolog.theory.testutils.ReteTreeUtils.assertReteNodeEmpty
import kotlin.test.*

/**
 * Test class for [ReteTree.DirectiveNode]
 *
 * @author Enrico
 */
internal class DirectiveNodeTest {

    private lateinit var emptyDirectiveNode: ReteTree.DirectiveNode
    private lateinit var filledDirectiveNode: ReteTree.DirectiveNode

    private val aRule: Rule = Rule.of(Atom.of("a"), Var.of("A"))
    private val aDirective: Directive = Directive.of(Truth.`true`(), Var.of("B"))

    @BeforeTest
    fun init() {
        emptyDirectiveNode = ReteTree.DirectiveNode()
        filledDirectiveNode = ReteTree.DirectiveNode().apply { ReteTreeUtils.directives.forEach { put(it) } }
    }

    @Test
    fun clausesCorrect() {
        assertReteNodeEmpty(emptyDirectiveNode)
        assertReteNodeClausesCorrect(filledDirectiveNode, ReteTreeUtils.directives)
    }

    @Test
    fun putClauseInsertsDirective() {
        emptyDirectiveNode.put(aDirective)

        assertEquals(aDirective, emptyDirectiveNode.clauses.single())
    }

    @Test
    fun putClauseDoesntInsertRule() {
        emptyDirectiveNode.put(aRule)

        assertReteNodeEmpty(emptyDirectiveNode)
    }

    @Test
    fun putClauseInsertsAfterByDefault() {
        filledDirectiveNode.put(aDirective)

        assertEquals(aDirective, filledDirectiveNode.clauses.last())
    }

    @Test
    fun putClauseInsertsBeforeAllIfSpecified() {
        filledDirectiveNode.put(aDirective, true)

        assertEquals(aDirective, filledDirectiveNode.clauses.first())
    }

    @Test
    fun getClause() {
        ReteTreeUtils.directivesQueryResultsMap.forEach { (query, result) ->
            assertEquals(result, filledDirectiveNode.get(query).toList())
        }
    }

    @Test
    fun getClauseWithDifferentTypeQueryAlwaysEmpty() {
        ReteTreeUtils.rulesQueryResultsMap.forEach { (query, _) ->
            assertTrue { filledDirectiveNode.get(query).none() }
        }
    }

    @Test
    fun removeClauseWithZeroLimitDoesNothing() {
        ReteTreeUtils.directivesQueryResultsMap.forEach { (query, _) ->
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
            ReteTreeUtils.directivesQueryResultsMap.forEach { (query, allMatching) ->
                init() // because removal of side-effects is needed

                assertRemovedFromReteNode(filledDirectiveNode, allMatching.take(limit)) { remove(query, limit) }
            }
        }
    }

    @Test
    fun removeClauseWithNegativeLimitRemovesAllMatchingDirectives() {
        val negativeLimit = -1
        ReteTreeUtils.directivesQueryResultsMap.forEach { (query, allMatching) ->
            init() // because removal of side-effects is needed

            assertRemovedFromReteNode(filledDirectiveNode, allMatching) { remove(query, negativeLimit) }
        }
    }

    @Test
    fun removeAllClause() {
        ReteTreeUtils.directivesQueryResultsMap.forEach { (query, allMatching) ->
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

        assertSame(emptyDirectiveNode.clauses.single(), independentCopy.clauses.single())
    }

}
