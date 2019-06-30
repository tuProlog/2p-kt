package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.RealImpl
import it.unibo.tuprolog.core.testutils.EqualityUtils
import it.unibo.tuprolog.core.testutils.RealUtils
import org.gciatto.kt.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertFailsWith

/**
 * Test class for [Real] companion object
 *
 * @author Enrico
 */
internal class RealTest {

    @Test
    fun realOfBigDecimal() {
        val correct = RealUtils.bigDecimals.map(::RealImpl)
        val toTest = RealUtils.bigDecimals.map { Real.of(it) }

        EqualityUtils.assertEqualities(toTest, correct)
    }

    @Test
    fun realOfDouble() {
        val correct = RealUtils.decimalsAsDoubles.map { BigDecimal.of(it) }.map(::RealImpl)
        val toTest = RealUtils.decimalsAsDoubles.map { Real.of(it) }

        EqualityUtils.assertEqualities(toTest, correct)
    }

    @Test
    fun realOfFloat() {
        val correct = RealUtils.decimalsAsFloats.map { BigDecimal.of(it) }.map(::RealImpl)
        val toTest = RealUtils.decimalsAsFloats.map { Real.of(it) }

        EqualityUtils.assertEqualities(toTest, correct)
    }

    @Test
    fun realOfString() {
        val correct = RealUtils.bigDecimals.map(::RealImpl)
        val toTest = RealUtils.stringNumbers.map { Real.of(it) }

        EqualityUtils.assertEqualities(toTest, correct)
    }

    @Test
    fun realRespectItsRegexPattern() {
        RealUtils.stringNumbers.forEach { it matches Real.REAL_REGEX_PATTERN }
        RealUtils.stringNumbers.map { Real.of(it) }.forEach { it.toString() matches Real.REAL_REGEX_PATTERN }
    }

    @Test
    fun infinityHandling() {
        assertFailsWith<NumberFormatException> { Real.of(Double.NEGATIVE_INFINITY) }
        assertFailsWith<NumberFormatException> { Real.of(Double.POSITIVE_INFINITY) }

        assertFailsWith<NumberFormatException> { Real.of(Float.NEGATIVE_INFINITY) }
        assertFailsWith<NumberFormatException> { Real.of(Float.POSITIVE_INFINITY) }
    }

    @Test
    fun notANumberHandling() {
        assertFailsWith<NumberFormatException> { Real.of(Double.NaN) }
        assertFailsWith<NumberFormatException> { Real.of(Float.NaN) }
    }
}
