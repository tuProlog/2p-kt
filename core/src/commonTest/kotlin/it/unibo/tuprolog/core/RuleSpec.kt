package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.impl.RuleImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.FactUtils
import it.unibo.tuprolog.core.testutils.RuleUtils

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
    })
