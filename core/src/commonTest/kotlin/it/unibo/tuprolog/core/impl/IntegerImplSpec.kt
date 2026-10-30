@file:Suppress("LocalVariableName")

package it.unibo.tuprolog.core.impl

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
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
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

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

        integers("value") { case, integer -> assertEquals(case.value, integer.value) }

        integers("int-value") { case, integer -> assertEquals(case.value, integer.intValue) }

        integers("decimal-value") { case, integer -> assertEquals(BigDecimal.of(case.value), integer.decimalValue) }

        integers("to-string") { case, integer -> assertEquals(case.value.toString(), integer.toString()) }

        test("compare-to-works-as-expected") {
            val one = IntegerImpl(BigInteger.of(1))
            val two = IntegerImpl(BigInteger.of(2))

            assertTrue(one.compareValueTo(one) == 0)
            assertTrue(one.compareValueTo(one) >= 0)

            assertTrue(one.compareValueTo(two) != 0)
            assertTrue(one.compareValueTo(two) <= 0)
            assertTrue(one.compareValueTo(two) < 0)

            assertFalse(one.compareValueTo(two) >= 0)
            assertFalse(one.compareValueTo(two) > 0)
        }

        integers("type-checks") { _, integer -> TermTypeAssertionUtils.assertIsInteger(integer) }

        test("equals-works-as-expected") {
            val oneInteger = IntegerImpl(BigInteger.of(1))
            val oneReal = Real.of(1.0)
            val oneAtom = Atom.of("1")

            assertEquals(oneInteger, oneInteger)

            assertNotEquals<Numeric>(oneInteger, oneReal)
            assertNotEquals<Constant>(oneInteger, oneAtom)
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
