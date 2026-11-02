@file:Suppress("LocalVariableName")

package it.unibo.tuprolog.core.impl

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Constant
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertNotStructurallyEquals
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertStructurallyEquals
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.RealUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import org.gciatto.kt.math.BigDecimal

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

        reals("value") { case, real -> real.value shouldBe case.value }

        reals("decimal-value") { case, real -> real.decimalValue shouldBe case.value }

        reals("int-value") { case, real -> real.intValue shouldBe case.value.toBigInteger() }

        reals("to-string") { case, real -> real.toString() shouldBe Real.toStringEnsuringDecimal(case.value) }

        test("compare-to-works-as-expected") {
            val oneDotOne = RealImpl(BigDecimal.of(1.1))
            val oneDotTwentyTwo = RealImpl(BigDecimal.of(1.22f))

            oneDotOne.compareValueTo(oneDotOne) shouldBe 0
            (oneDotOne.compareValueTo(oneDotOne) >= 0).shouldBeTrue()

            oneDotOne.compareValueTo(oneDotTwentyTwo) shouldNotBe 0
            (oneDotOne.compareValueTo(oneDotTwentyTwo) <= 0).shouldBeTrue()
            (oneDotOne.compareValueTo(oneDotTwentyTwo) < 0).shouldBeTrue()

            (oneDotOne.compareValueTo(oneDotTwentyTwo) >= 0).shouldBeFalse()
            (oneDotOne.compareValueTo(oneDotTwentyTwo) > 0).shouldBeFalse()
        }

        reals("type-checks") { _, real -> TermTypeAssertionUtils.assertIsReal(real) }

        test("equals-works-as-expected") {
            val oneReal = RealImpl(BigDecimal.of(1.1))
            val oneAtom = Atom.of("1.1")

            oneReal shouldBe oneReal

            oneAtom.shouldNotBe<Constant>(oneReal)
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
