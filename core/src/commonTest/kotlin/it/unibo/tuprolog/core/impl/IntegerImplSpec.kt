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
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertNotStructurallyEquals
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertStructurallyEquals
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.IntegerUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger

/**
 * Test class for [IntegerImpl] and [Integer]
 *
 * @author Enrico
 */
class IntegerImplSpec :
    FunSpec({
        fun integers(
            property: String,
            test: (IntegerUtils.IntegerCase, Integer) -> Unit,
        ) = withTests(nameFn = { "$property/${it.id}" }, IntegerUtils.integerCases) { test(it, IntegerImpl(it.value)) }

        integers("value") { case, integer -> integer.value shouldBe case.value }

        integers("int-value") { case, integer -> integer.intValue shouldBe case.value }

        integers("decimal-value") { case, integer -> integer.decimalValue shouldBe BigDecimal.of(case.value) }

        integers("to-string") { case, integer -> integer.toString() shouldBe case.value.toString() }

        test("compare-to-works-as-expected") {
            val one = IntegerImpl(BigInteger.of(1))
            val two = IntegerImpl(BigInteger.of(2))

            one.compareValueTo(one) shouldBe 0
            (one.compareValueTo(one) >= 0).shouldBeTrue()

            one.compareValueTo(two) shouldNotBe 0
            (one.compareValueTo(two) <= 0).shouldBeTrue()
            (one.compareValueTo(two) < 0).shouldBeTrue()

            (one.compareValueTo(two) >= 0).shouldBeFalse()
            (one.compareValueTo(two) > 0).shouldBeFalse()
        }

        integers("type-checks") { _, integer -> TermTypeAssertionUtils.assertIsInteger(integer) }

        test("equals-works-as-expected") {
            val oneInteger = IntegerImpl(BigInteger.of(1))
            val oneReal = Real.of(1.0)
            val oneAtom = Atom.of("1")

            oneInteger shouldBe oneInteger

            oneReal.shouldNotBe<Numeric>(oneInteger)
            oneAtom.shouldNotBe<Constant>(oneInteger)
        }

        test("structurally-equals-works-as-expected") {
            val oneInteger = IntegerImpl(BigInteger.of(1))
            val oneReal = Real.of(1.0)
            val oneAtom = Atom.of("1")

            assertStructurallyEquals(oneInteger, oneInteger)
            assertStructurallyEquals(oneInteger, oneReal)

            assertNotStructurallyEquals(oneInteger, oneAtom)
        }

        integers("fresh-copy-is-itself") { _, integer -> ConstantUtils.assertFreshCopyIsItself(integer) }

        integers("fresh-copy-with-scope-is-itself") { _, integer ->
            ConstantUtils.assertFreshCopyWithScopeIsItself(integer)
        }

        integers("apply-is-itself") { _, integer -> ConstantUtils.assertApplyingSubstitutionIsItself(integer) }
    })
