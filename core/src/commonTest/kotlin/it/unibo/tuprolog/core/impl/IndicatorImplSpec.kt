package it.unibo.tuprolog.core.impl

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.IndicatorUtils
import it.unibo.tuprolog.core.testutils.StructUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

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

        indicators("functor") { _, indicator -> assertEquals("/", indicator.functor) }

        indicators("args") { case, indicator -> assertEquals(listOf(case.name, case.arity), indicator.args) }

        indicators("indicated-name", IndicatorUtils.wellFormedIndicatorCases) { case, indicator ->
            assertEquals(case.rawName, indicator.indicatedName)
        }

        indicators("indicated-name-null", IndicatorUtils.nonWellFormedNameIndicatorCases) { _, indicator ->
            assertEquals(null, indicator.indicatedName)
        }

        indicators("indicated-arity", IndicatorUtils.wellFormedIndicatorCases) { case, indicator ->
            assertEquals(case.rawArity, indicator.indicatedArity)
        }

        indicators("indicated-arity-null", IndicatorUtils.nonWellFormedArityIndicatorCases) { _, indicator ->
            assertEquals(null, indicator.indicatedArity)
        }

        indicators("to-string") { case, indicator ->
            assertEquals("${case.name}${Indicator.FUNCTOR}${case.arity}", indicator.toString())
        }

        indicators("type-checks") { _, indicator -> TermTypeAssertionUtils.assertIsIndicator(indicator) }

        indicators("two-arity") { _, indicator -> assertEquals(2, indicator.arity) }

        indicators("is-well-formed", IndicatorUtils.wellFormedIndicatorCases) { _, indicator ->
            assertTrue { indicator.isWellFormed }
        }

        indicators("is-not-well-formed", nonWellFormedIndicatorCases) { _, indicator ->
            assertFalse { indicator.isWellFormed }
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
