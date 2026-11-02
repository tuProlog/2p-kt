package it.unibo.tuprolog.core.impl

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.RuleUtils
import it.unibo.tuprolog.core.testutils.StructUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils

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
            withClue("$rule isWellFormed should be true") { rule.isWellFormed.shouldBeTrue() }
        }

        rules("is-not-well-formed", RuleUtils.nonWellFormedRuleCases) { _, rule ->
            withClue("$rule isWellFormed should be false") { rule.isWellFormed.shouldBeFalse() }
        }

        rules("type-checks") { _, rule -> TermTypeAssertionUtils.assertIsRule(rule) }

        test("fact-detected") {
            RuleImpl(Atom.of("hey"), Truth.TRUE).isFact.shouldBeTrue()
        }

        rules("is-ground", RuleUtils.groundRuleCases) { _, rule -> rule.isGround.shouldBeTrue() }

        rules("is-not-ground", RuleUtils.nonGroundRuleCases) { _, rule -> rule.isGround.shouldBeFalse() }

        rules("fresh-copy-is-itself", RuleUtils.groundRuleCases) { _, rule ->
            ConstantUtils.assertFreshCopyIsItself(rule)
        }

        rules("fresh-copy-renews-variables", RuleUtils.nonGroundRuleCases) { _, rule ->
            StructUtils.assertFreshCopyRenewsContainedVariables(rule)
        }

        rules("functor") { _, rule -> rule.functor shouldBe ":-" }

        rules("args") { case, rule -> rule.args shouldBe listOf(case.head, case.body) }

        rules("to-string") { _, rule -> rule.toString() shouldBe "${rule.head} ${rule.functor} ${rule.body}" }
    })
