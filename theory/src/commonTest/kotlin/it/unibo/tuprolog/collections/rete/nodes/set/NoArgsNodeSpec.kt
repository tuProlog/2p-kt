package it.unibo.tuprolog.collections.rete.nodes.set

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
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
import kotlin.test.assertEquals
import kotlin.test.assertSame

/**
 * Test class for [NoArgsNode]
 *
 * @author Enrico
 */
class NoArgsNodeSpec :
    FunSpec({
        lateinit var emptyNoArgsNode: NoArgsNode

        lateinit var filledNoArgsNode: NoArgsNode

        val aRule: Rule = Rule.of(Atom.of("a"), Var.of("A"))

        fun init() {
            emptyNoArgsNode = NoArgsNode()
            filledNoArgsNode = NoArgsNode().apply { ReteNodeUtils.noArgHeadedRules.forEach { put(it) } }
        }

        beforeTest { init() }

        test("clauses-correct") {
            assertReteNodeEmpty(emptyNoArgsNode)
            assertReteNodeClausesCorrect(filledNoArgsNode, ReteNodeUtils.noArgHeadedRules)
        }

        test("put-clause-inserts-rule") {
            emptyNoArgsNode.put(aRule)

            assertEquals(aRule, emptyNoArgsNode.indexedElements.single())
        }

        test("put-clause-inserts-struct-headed-rule") {
            // no check is present to ensure that a correct clause is inserted as a child
            val aStructHeadedRule = Fact.of(Struct.of("f", Atom.of("a")))
            emptyNoArgsNode.put(aStructHeadedRule)

            assertEquals(aStructHeadedRule, emptyNoArgsNode.indexedElements.single())
        }

        test("put-clause-inserts-after-by-default") {
            filledNoArgsNode.put(aRule)

            assertEquals(aRule, filledNoArgsNode.indexedElements.last())
        }

        test("put-clause-inserts-before-all-if-specified") {
            filledNoArgsNode.put(aRule, true)

            assertEquals(aRule, filledNoArgsNode.indexedElements.first())
        }

        withTests(nameFn = { "get-clause/${it.id}" }, ReteNodeUtils.noArgHeadedRuleCases) { case ->
            assertEquals(case.clause, filledNoArgsNode.get(case.clause).single())
        }

        withTests(
            nameFn = { "remove-clause-with-zero-limit-does-nothing/${it.id}" },
            ReteNodeUtils.noArgHeadedRuleCases,
        ) { case ->
            assertNoChangesInReteNode(filledNoArgsNode) { remove(case.clause, 0) }
        }

        test("remove-clause-from-empty-node-does-nothing") {
            assertNoChangesInReteNode(emptyNoArgsNode) { remove(aRule) }
        }

        withTests(
            nameFn = { (limit, case) -> "remove-clause-with-limit-works-as-expected/limit-$limit/${case.id}" },
            (1..3).flatMap { limit -> ReteNodeUtils.noArgHeadedRuleCases.map { limit to it } },
        ) { (limit, case) ->
            assertRemovedFromReteNode(filledNoArgsNode, listOf(case.clause)) { remove(case.clause, limit) }
        }

        withTests(
            nameFn = { "remove-clause-with-negative-limit-removes-all-matching-rules/${it.id}" },
            ReteNodeUtils.noArgHeadedRuleCases,
        ) { case ->
            assertRemovedFromReteNode(filledNoArgsNode, listOf(case.clause)) { remove(case.clause, -1) }
        }

        withTests(nameFn = { "remove-all-clause/${it.id}" }, ReteNodeUtils.noArgHeadedRuleCases) { case ->
            assertRemovedFromReteNode(filledNoArgsNode, listOf(case.clause)) { removeAll(case.clause) }
        }

        test("deep-copy-creates-independent-instance") {
            emptyNoArgsNode.put(aRule)

            val independentCopy = emptyNoArgsNode.deepCopy()

            assertReteNodeClausesCorrect(emptyNoArgsNode, listOf(aRule))
            assertReteNodeClausesCorrect(independentCopy, listOf(aRule))

            emptyNoArgsNode.remove(aRule)

            assertReteNodeClausesCorrect(emptyNoArgsNode, emptyList())
            assertReteNodeClausesCorrect(independentCopy, listOf(aRule))
        }

        test("deep-copy-does-not-copy-indexed-instances") {
            emptyNoArgsNode.put(aRule)

            val independentCopy = emptyNoArgsNode.deepCopy()

            assertSame(emptyNoArgsNode.indexedElements.single(), independentCopy.indexedElements.single())
        }
    })
