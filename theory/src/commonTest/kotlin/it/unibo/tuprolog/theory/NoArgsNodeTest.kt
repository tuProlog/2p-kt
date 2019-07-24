package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.theory.testutils.ReteTreeUtils
import it.unibo.tuprolog.theory.testutils.ReteTreeUtils.assertNoChangesInReteNode
import it.unibo.tuprolog.theory.testutils.ReteTreeUtils.assertRemovedFromReteNode
import it.unibo.tuprolog.theory.testutils.ReteTreeUtils.assertReteNodeClausesCorrect
import it.unibo.tuprolog.theory.testutils.ReteTreeUtils.assertReteNodeEmpty
import kotlin.test.*

/**
 * Test class for [ReteTree.NoArgsNode]
 *
 * @author Enrico
 */
internal class NoArgsNodeTest {

    private lateinit var emptyNoArgsNode: ReteTree.NoArgsNode
    private lateinit var filledNoArgsNode: ReteTree.NoArgsNode

    private val aRule: Rule = Rule.of(Atom.of("a"), Var.of("A"))
    private val aDirective: Directive = Directive.of(Truth.`true`(), Var.of("B"))

    @BeforeTest
    fun init() {
        emptyNoArgsNode = ReteTree.NoArgsNode()
        filledNoArgsNode = ReteTree.NoArgsNode().apply { ReteTreeUtils.noArgHeadedRules.forEach { put(it) } }
    }

    @Test
    fun clausesCorrect() {
        assertReteNodeEmpty(emptyNoArgsNode)
        assertReteNodeClausesCorrect(filledNoArgsNode, ReteTreeUtils.noArgHeadedRules)
    }

    @Test
    fun putClauseInsertsRule() {
        emptyNoArgsNode.put(aRule)

        assertEquals(aRule, emptyNoArgsNode.clauses.single())
    }

    @Test
    fun putClauseInsertsStructHeadedRule() {
        // no check is present to ensure that a correct clause is inserted as a child
        val aStructHeadedRule = Fact.of(Struct.of("f", Atom.of("a")))
        emptyNoArgsNode.put(aStructHeadedRule)

        assertEquals(aStructHeadedRule, emptyNoArgsNode.clauses.single())
    }

    @Test
    fun putClauseDoesntInsertDirective() {
        emptyNoArgsNode.put(aDirective)

        assertReteNodeEmpty(emptyNoArgsNode)
    }

    @Test
    fun putClauseInsertsAfterByDefault() {
        filledNoArgsNode.put(aRule)

        assertEquals(aRule, filledNoArgsNode.clauses.last())
    }

    @Test
    fun putClauseInsertsBeforeAllIfSpecified() {
        filledNoArgsNode.put(aRule, true)

        assertEquals(aRule, filledNoArgsNode.clauses.first())
    }

    @Test
    fun getClause() {
        ReteTreeUtils.noArgHeadedRules.forEach { queryAndResult ->
            assertEquals(queryAndResult, filledNoArgsNode.get(queryAndResult).single())
        }
    }

    @Test
    fun getClauseWithDifferentTypeQueryAlwaysEmpty() {
        ReteTreeUtils.directivesQueryResultsMap.forEach { (query, _) ->
            assertTrue { filledNoArgsNode.get(query).none() }
        }
    }

    @Test
    fun removeClauseWithZeroLimitDoesNothing() {
        ReteTreeUtils.noArgHeadedRules.forEach { queryAndResult ->
            assertNoChangesInReteNode(filledNoArgsNode) { remove(queryAndResult, 0) }
        }
    }

    @Test
    fun removeClauseFromEmptyNodeDoesNothing() {
        assertNoChangesInReteNode(emptyNoArgsNode) { remove(aRule) }
    }

    @Test
    fun removeClauseWithLimitWorksAsExpected() {
        for (limit in 1..3) {
            ReteTreeUtils.noArgHeadedRules.forEach { queryAndResult ->
                init() // because removal of side-effects is needed

                assertRemovedFromReteNode(filledNoArgsNode, listOf(queryAndResult)) { remove(queryAndResult, limit) }
            }
        }
    }

    @Test
    fun removeClauseWithNegativeLimitRemovesAllMatchingRules() {
        val negativeLimit = -1
        ReteTreeUtils.noArgHeadedRules.forEach { queryAndResult ->
            init() // because removal of side-effects is needed

            assertRemovedFromReteNode(filledNoArgsNode, listOf(queryAndResult)) { remove(queryAndResult, negativeLimit) }
        }
    }

    @Test
    fun removeAllClause() {
        ReteTreeUtils.noArgHeadedRules.forEach { queryAndResult ->
            init() // because removal of side-effects is needed

            assertRemovedFromReteNode(filledNoArgsNode, listOf(queryAndResult)) { removeAll(queryAndResult) }
        }
    }

    @Test
    fun deepCopyCreatesIndependentInstance() {
        emptyNoArgsNode.put(aRule)

        val independentCopy = emptyNoArgsNode.deepCopy()

        assertReteNodeClausesCorrect(emptyNoArgsNode, listOf(aRule))
        assertReteNodeClausesCorrect(independentCopy, listOf(aRule))

        emptyNoArgsNode.remove(aRule)

        assertReteNodeClausesCorrect(emptyNoArgsNode, emptyList())
        assertReteNodeClausesCorrect(independentCopy, listOf(aRule))
    }

    @Test
    fun deepCopyDoesNotCopyIndexedInstances() {
        emptyNoArgsNode.put(aRule)

        val independentCopy = emptyNoArgsNode.deepCopy()

        assertSame(emptyNoArgsNode.clauses.single(), independentCopy.clauses.single())
    }

}
