package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.impl.RealImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.RealUtils
import org.gciatto.kt.math.BigDecimal
import kotlin.test.assertFailsWith

/**
 * Test class for [Real] companion object
 *
 * @author Enrico
 */
class RealSpec :
    FunSpec({
        test("real-of-big-decimal") {
            val correct = RealUtils.bigDecimals.map(::RealImpl)
            val toTest = RealUtils.bigDecimals.map { Real.of(it) }

            onCorrespondingItems(toTest, correct, ::assertEqualities)
        }

        test("real-of-double") {
            val correct = RealUtils.decimalsAsDoubles.map { BigDecimal.of(it) }.map(::RealImpl)
            val toTest = RealUtils.decimalsAsDoubles.map { Real.of(it) }

            onCorrespondingItems(toTest, correct, ::assertEqualities)
        }

        test("real-of-float") {
            val correct = RealUtils.decimalsAsFloats.map { BigDecimal.of(it) }.map(::RealImpl)
            val toTest = RealUtils.decimalsAsFloats.map { Real.of(it) }

            onCorrespondingItems(toTest, correct, ::assertEqualities)
        }

        test("real-of-string") {
            val correct = RealUtils.bigDecimals.map(::RealImpl)
            val toTest = RealUtils.stringNumbers.map { Real.of(it) }

            onCorrespondingItems(toTest, correct, ::assertEqualities)
        }

        test("real-respect-its-regex-pattern") {
            RealUtils.stringNumbers.forEach { it matches Real.PATTERN }
            RealUtils.stringNumbers.map { Real.of(it) }.forEach { it.toString() matches Real.PATTERN }
        }

        test("infinity-handling") {
            assertFailsWith<NumberFormatException> { Real.of(Double.NEGATIVE_INFINITY) }
            assertFailsWith<NumberFormatException> { Real.of(Double.POSITIVE_INFINITY) }

            assertFailsWith<NumberFormatException> { Real.of(Float.NEGATIVE_INFINITY) }
            assertFailsWith<NumberFormatException> { Real.of(Float.POSITIVE_INFINITY) }
        }

        test("not-a-number-handling") {
            assertFailsWith<NumberFormatException> { Real.of(Double.NaN) }
            assertFailsWith<NumberFormatException> { Real.of(Float.NaN) }
        }
    })
