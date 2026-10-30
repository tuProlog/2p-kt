@file:Suppress("LocalVariableName")

package it.unibo.tuprolog.core.impl

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Constant
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertNotStructurallyEquals
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertStructurallyEquals
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.RealUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import org.gciatto.kt.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * Test class for [RealImpl] and [Real]
 *
 * @author Enrico
 */
class RealImplSpec :
    FunSpec({
        fun reals(
            property: String,
            test: (RealUtils.RealCase, Real) -> Unit,
        ) = withTests(nameFn = { "$property/${it.id}" }, RealUtils.realCases) { test(it, RealImpl(it.value)) }

        reals("value") { case, real -> assertEquals(case.value, real.value) }

        reals("decimal-value") { case, real -> assertEquals(case.value, real.decimalValue) }

        reals("int-value") { case, real -> assertEquals(case.value.toBigInteger(), real.intValue) }

        reals("to-string") { case, real -> assertEquals(Real.toStringEnsuringDecimal(case.value), real.toString()) }

        test("compare-to-works-as-expected") {
            val oneDotOne = RealImpl(BigDecimal.of(1.1))
            val oneDotTwentyTwo = RealImpl(BigDecimal.of(1.22f))

            assertTrue(oneDotOne.compareValueTo(oneDotOne) == 0)
            assertTrue(oneDotOne.compareValueTo(oneDotOne) >= 0)

            assertTrue(oneDotOne.compareValueTo(oneDotTwentyTwo) != 0)
            assertTrue(oneDotOne.compareValueTo(oneDotTwentyTwo) <= 0)
            assertTrue(oneDotOne.compareValueTo(oneDotTwentyTwo) < 0)

            assertFalse(oneDotOne.compareValueTo(oneDotTwentyTwo) >= 0)
            assertFalse(oneDotOne.compareValueTo(oneDotTwentyTwo) > 0)
        }

        reals("type-checks") { _, real -> TermTypeAssertionUtils.assertIsReal(real) }

        test("equals-works-as-expected") {
            val oneReal = RealImpl(BigDecimal.of(1.1))
            val oneAtom = Atom.of("1.1")

            assertEquals(oneReal, oneReal)

            assertNotEquals<Constant>(oneReal, oneAtom)
        }

        test("structurally-equals-works-as-expected") {
            val oneReal = RealImpl(BigDecimal.of(-2.6))
            val oneAtom = Atom.of("-2.6")

            assertStructurallyEquals(oneReal, oneReal)

            assertNotStructurallyEquals(oneReal, oneAtom)
        }

        reals("fresh-copy-is-itself") { _, real -> ConstantUtils.assertFreshCopyIsItself(real) }

        reals("fresh-copy-with-scope-is-itself") { _, real -> ConstantUtils.assertFreshCopyWithScopeIsItself(real) }

        reals("apply-is-itself") { _, real -> ConstantUtils.assertApplyingSubstitutionIsItself(real) }
    })
