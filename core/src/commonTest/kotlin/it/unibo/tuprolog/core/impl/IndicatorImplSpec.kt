package it.unibo.tuprolog.core.impl

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.IndicatorUtils
import it.unibo.tuprolog.core.testutils.StructUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils

/**
 * Test class for [IndicatorImpl] and [Indicator]
 *
 * @author Enrico
 */
class IndicatorImplSpec :
    FunSpec({
        fun indicators(
            property: String,
            cases: List<IndicatorUtils.IndicatorCase> = IndicatorUtils.mixedIndicatorCases,
            test: (IndicatorUtils.IndicatorCase, IndicatorImpl) -> Unit,
        ) = withTests(nameFn = { "$property/${it.id}" }, cases) { test(it, IndicatorImpl(it.name, it.arity)) }

        val nonWellFormedIndicatorCases =
            IndicatorUtils.nonWellFormedNameIndicatorCases + IndicatorUtils.nonWellFormedArityIndicatorCases

        indicators("name-term") { case, indicator -> assertEqualities(case.name, indicator.nameTerm) }

        indicators("arity-term") { case, indicator -> assertEqualities(case.arity, indicator.arityTerm) }

        indicators("functor") { _, indicator -> indicator.functor shouldBe "/" }

        indicators("args") { case, indicator -> indicator.args shouldBe listOf(case.name, case.arity) }

        indicators("indicated-name", IndicatorUtils.wellFormedIndicatorCases) { case, indicator ->
            indicator.indicatedName shouldBe case.rawName
        }

        indicators("indicated-name-null", IndicatorUtils.nonWellFormedNameIndicatorCases) { _, indicator ->
            indicator.indicatedName shouldBe null
        }

        indicators("indicated-arity", IndicatorUtils.wellFormedIndicatorCases) { case, indicator ->
            indicator.indicatedArity shouldBe case.rawArity
        }

        indicators("indicated-arity-null", IndicatorUtils.nonWellFormedArityIndicatorCases) { _, indicator ->
            indicator.indicatedArity shouldBe null
        }

        indicators("to-string") { case, indicator ->
            indicator.toString() shouldBe "${case.name}${Indicator.FUNCTOR}${case.arity}"
        }

        indicators("type-checks") { _, indicator -> TermTypeAssertionUtils.assertIsIndicator(indicator) }

        indicators("two-arity") { _, indicator -> indicator.arity shouldBe 2 }

        indicators("is-well-formed", IndicatorUtils.wellFormedIndicatorCases) { _, indicator ->
            indicator.isWellFormed.shouldBeTrue()
        }

        indicators("is-not-well-formed", nonWellFormedIndicatorCases) { _, indicator ->
            indicator.isWellFormed.shouldBeFalse()
        }

        indicators("fresh-copy-is-itself", IndicatorUtils.mixedIndicatorCases.filter { it.isGround }) { _, indicator ->
            ConstantUtils.assertFreshCopyIsItself(indicator)
        }

        indicators(
            "fresh-copy-renews-variables",
            IndicatorUtils.mixedIndicatorCases.filterNot { it.isGround },
        ) { _, indicator -> StructUtils.assertFreshCopyRenewsContainedVariables(indicator) }

        test("fresh-copy-merges-different-variables-with-same-name") {
            StructUtils.assertFreshCopyMergesDifferentVariablesWithSameName(::IndicatorImpl)
        }
    })
