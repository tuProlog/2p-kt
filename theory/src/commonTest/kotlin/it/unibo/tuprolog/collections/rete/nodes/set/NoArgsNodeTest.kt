package it.unibo.tuprolog.collections.rete.nodes.set

import it.unibo.tuprolog.collections.rete.generic.set.NoArgsNode
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.testutils.ReteNodeUtils
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertNoChangesInReteNode
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertRemovedFromReteNode
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertReteNodeClausesCorrect
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertReteNodeEmpty
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

/**
 * Test class for [NoArgsNode]
 *
 * @author Enrico
 */
internal class NoArgsNodeTest {

    private lateinit var emptyNoArgsNode: NoArgsNode
    private lateinit var filledNoArgsNode: NoArgsNode

    private val aRule: Rule = Rule.of(Atom.of("a"), Var.of("A"))

    @BeforeTest
    fun init() {
        emptyNoArgsNode = NoArgsNode()
        filledNoArgsNode = NoArgsNode().apply { ReteNodeUtils.noArgHeadedRules.forEach { put(it) } }
    }

    @Test
    fun clausesCorrect() {
        assertReteNodeEmpty(emptyNoArgsNode)
        assertReteNodeClausesCorrect(filledNoArgsNode, ReteNodeUtils.noArgHeadedRules)
    }

    @Test
    fun putClauseInsertsRule() {
        emptyNoArgsNode.put(aRule)

        assertEquals(aRule, emptyNoArgsNode.indexedElements.single())
    }

    @Test
    fun putClauseInsertsStructHeadedRule() {
        // no check is present to ensure that a correct clause is inserted as a child
        val aStructHeadedRule = Fact.of(Struct.of("f", Atom.of("a")))
        emptyNoArgsNode.put(aStructHeadedRule)

        assertEquals(aStructHeadedRule, emptyNoArgsNode.indexedElements.single())
    }

    @Test
    fun putClauseInsertsAfterByDefault() {
        filledNoArgsNode.put(aRule)

        assertEquals(aRule, filledNoArgsNode.indexedElements.last())
    }

    @Test
    fun putClauseInsertsBeforeAllIfSpecified() {
        filledNoArgsNode.put(aRule, true)

        assertEquals(aRule, filledNoArgsNode.indexedElements.first())
    }

    @Test
    fun getClause() {
        ReteNodeUtils.noArgHeadedRules.forEach { queryAndResult ->
            assertEquals(queryAndResult, filledNoArgsNode.get(queryAndResult).single())
        }
    }

    @Test
    fun removeClauseWithZeroLimitDoesNothing() {
        ReteNodeUtils.noArgHeadedRules.forEach { queryAndResult ->
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
            ReteNodeUtils.noArgHeadedRules.forEach { queryAndResult ->
                init() // because removal of side-effects is needed

                assertRemovedFromReteNode(filledNoArgsNode, listOf(queryAndResult)) { remove(queryAndResult, limit) }
            }
        }
    }

    @Test
    fun removeClauseWithNegativeLimitRemovesAllMatchingRules() {
        val negativeLimit = -1
        ReteNodeUtils.noArgHeadedRules.forEach { queryAndResult ->
            init() // because removal of side-effects is needed

            assertRemovedFromReteNode(filledNoArgsNode, listOf(queryAndResult)) {
                remove(queryAndResult, negativeLimit)
            }
        }
    }

    @Test
    fun removeAllClause() {
        ReteNodeUtils.noArgHeadedRules.forEach { queryAndResult ->
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

        assertSame(emptyNoArgsNode.indexedElements.single(), independentCopy.indexedElements.single())
    }
}
