package it.unibo.tuprolog.core.impl

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.RuleUtils
import it.unibo.tuprolog.core.testutils.StructUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Test class for [RuleImpl] and [Rule]
 *
 * @author Enrico
 */
class RuleImplSpec :
    FunSpec({
        fun rules(
            property: String,
            cases: List<RuleUtils.RuleCase> = RuleUtils.mixedRuleCases,
            test: (RuleUtils.RuleCase, Rule) -> Unit,
        ) = withTests(nameFn = { "$property/${it.id}" }, cases) { test(it, RuleImpl(it.head, it.body)) }

        rules("head") { case, rule -> assertEqualities(case.head, rule.head) }

        rules("body") { case, rule -> assertEqualities(case.body, rule.body) }

        rules("is-well-formed", RuleUtils.wellFormedRuleCases) { _, rule ->
            assertTrue("$rule isWellFormed should be true") { rule.isWellFormed }
        }

        rules("is-not-well-formed", RuleUtils.nonWellFormedRuleCases) { _, rule ->
            assertFalse("$rule isWellFormed should be false") { rule.isWellFormed }
        }

        rules("type-checks") { _, rule -> TermTypeAssertionUtils.assertIsRule(rule) }

        test("fact-detected") {
            assertTrue(RuleImpl(Atom.of("hey"), Truth.TRUE).isFact)
        }

        rules("is-ground", RuleUtils.groundRuleCases) { _, rule -> assertTrue { rule.isGround } }

        rules("is-not-ground", RuleUtils.nonGroundRuleCases) { _, rule -> assertFalse { rule.isGround } }

        rules("fresh-copy-is-itself", RuleUtils.groundRuleCases) { _, rule ->
            ConstantUtils.assertFreshCopyIsItself(rule)
        }

        rules("fresh-copy-renews-variables", RuleUtils.nonGroundRuleCases) { _, rule ->
            StructUtils.assertFreshCopyRenewsContainedVariables(rule)
        }

        rules("functor") { _, rule -> assertEquals(":-", rule.functor) }

        rules("args") { case, rule -> assertEquals(listOf(case.head, case.body), rule.args) }

        rules("to-string") { _, rule -> assertEquals("${rule.head} ${rule.functor} ${rule.body}", rule.toString()) }
    })
