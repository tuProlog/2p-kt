package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.impl.RuleImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.FactUtils
import it.unibo.tuprolog.core.testutils.RuleUtils
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Test class for [Rule] companion object
 *
 * @author Enrico
 */
class RuleSpec :
    FunSpec({
        withTests(nameFn = { "of-head-only-is-fact/${it.id}" }, FactUtils.mixedFactCases) { case ->
            assertEqualities(Fact.of(case.head), Rule.of(case.head))
        }

        withTests(nameFn = { "of-head-and-true-is-fact/${it.id}" }, FactUtils.mixedFactCases) { case ->
            assertEqualities(Fact.of(case.head), Rule.of(case.head, Truth.TRUE))
        }

        withTests(nameFn = { "of-head-and-body/${it.id}" }, RuleUtils.mixedRuleCases) { case ->
            assertEqualities(RuleImpl(case.head, case.body), Rule.of(case.head, case.body))
        }

        test("rule-of-passing-more-terms-creates-body-as-tuple") {
            val bodyVariables = arrayOf(Var.of("A"), Var.of("B"))

            val correctInstance = RuleImpl(Atom.of("a"), Tuple.wrapIfNeeded(*bodyVariables))
            val toBeTested = Rule.of(Atom.of("a"), *bodyVariables)

            assertEqualities(correctInstance, toBeTested)
        }

        test("set-head-args/keeps-the-body") {
            val (a, x) = listOf(Atom.of("a"), Var.of("X"))
            val rule = Rule.of(Struct.of("h", a), a, a)
            val expected = Rule.of(Struct.of("h", x), a, a)
            assertEquals(expected, rule.setHeadArgs(setOf(x)))
            assertEquals(expected, rule.setHeadArgs(sequenceOf(x)))
        }

        test("set-body-items/keeps-the-head") {
            val (a, x) = listOf(Atom.of("a"), Var.of("X"))
            val rule = Rule.of(Struct.of("h", a), a, a)
            assertEquals(Rule.of(Struct.of("h", a), x), rule.setBodyItems(setOf(x)))
            assertEquals(Rule.of(Struct.of("h", a), x, x), rule.setBodyItems(sequenceOf(x, x)))
        }

        test("get-body-item/out-of-range-throws") {
            assertFailsWith<IndexOutOfBoundsException> { Rule.of(Atom.of("h"), Atom.of("a")).getBodyItem(1) }
        }
    })
