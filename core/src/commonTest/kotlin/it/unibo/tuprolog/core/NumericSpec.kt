package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.IntegerUtils
import it.unibo.tuprolog.core.testutils.RealUtils
import org.gciatto.kt.math.BigDecimal
import kotlin.test.assertNotEquals

/**
 * Test class for [Numeric] companion object
 *
 * @author Enrico
 */
class NumericSpec :
    FunSpec({
        fun <T> assertEquals(
            expected: T,
            actual: T,
            message: String? = "Failed assertion $expected == $actual",
        ) {
            if (loggingOn) println("Object\n\t$actual\nis expected to be equal to\n\t$expected")
            try {
                kotlin.test.assertEquals(expected, actual, message)
            } catch (e: Exception) {
                if (loggingOn) println(e.message)
                throw e
            } finally {
                if (loggingOn) println("".padEnd(80, '-'))
            }
        }

        val realWithoutExponentCases = RealUtils.realCases.filterNot { "E" in it.string || "e" in it.string }

        withTests(nameFn = { "of-big-decimal/${it.id}" }, RealUtils.realCases) { case ->
            assertEqualities(Numeric.of(case.value), Real.of(case.value))
        }

        withTests(nameFn = { "of-number/real/${it.id}" }, RealUtils.realCases) { case ->
            @Suppress("USELESS_CAST")
            assertEqualities(Numeric.of(case.double as Number), Real.of(case.double))
        }

        withTests(
            nameFn = { "of-number/integer/${it.id}" },
            IntegerUtils.integerCases.filter { it.long != null },
        ) { case ->
            @Suppress("USELESS_CAST")
            assertEqualities(Numeric.of(case.long!! as Number), Integer.of(case.value))
        }

        withTests(nameFn = { "of-double/${it.id}" }, RealUtils.realCases) { case ->
            assertEqualities(Numeric.of(case.double), Real.of(case.double))
        }

        withTests(nameFn = { "of-float/${it.id}" }, RealUtils.realCases.filter { it.float != null }) { case ->
            assertEqualities(Numeric.of(case.float!!), Real.of(case.float!!))
        }

        withTests(nameFn = { "of-big-integer/${it.id}" }, IntegerUtils.integerCases) { case ->
            assertEqualities(Numeric.of(case.value), Integer.of(case.value))
        }

        withTests(nameFn = { "of-int/${it.id}" }, IntegerUtils.integerCases.filter { it.int != null }) { case ->
            assertEqualities(Numeric.of(case.int!!), Integer.of(case.int!!))
        }

        withTests(nameFn = { "of-long/${it.id}" }, IntegerUtils.integerCases.filter { it.long != null }) { case ->
            assertEqualities(Numeric.of(case.long!!), Integer.of(case.long!!))
        }

        withTests(nameFn = { "of-short/${it.id}" }, IntegerUtils.integerCases.filter { it.short != null }) { case ->
            assertEqualities(Numeric.of(case.short!!), Integer.of(case.short!!))
        }

        withTests(nameFn = { "of-byte/${it.id}" }, IntegerUtils.integerCases.filter { it.byte != null }) { case ->
            assertEqualities(Numeric.of(case.byte!!), Integer.of(case.byte!!))
        }

        withTests(nameFn = { "of-string/integer/${it.id}" }, IntegerUtils.integerCases) { case ->
            assertEqualities(Numeric.of(case.string), Integer.of(case.string))
        }

        withTests(nameFn = { "of-string/real/${it.id}" }, RealUtils.realCases) { case ->
            assertEqualities(Numeric.of(case.string), Real.of(case.string))
        }

        withTests(
            nameFn = { "integer-not-equal-to-real-even-if-same-value/${it.id}" },
            IntegerUtils.integerCases,
        ) { case ->
            assertNotEquals<Numeric>(
                Numeric.of(case.value),
                Numeric.of(case.value.toDouble()),
            )
        }

        withTests(
            nameFn = { "real-comparison-does-not-take-trailing-zeros-into-account/${it.id}" },
            realWithoutExponentCases,
        ) { case ->
            for (trailingZeros in listOf("0", "00")) {
                assertEquals(
                    Real.of(BigDecimal.of(case.string)),
                    Real.of(BigDecimal.of(case.string + trailingZeros)),
                )
            }
        }

        withTests(nameFn = { "real-hash-code-is-consistent-with-equals/${it.id}" }, realWithoutExponentCases) { case ->
            for (trailingZeros in listOf("0", "00")) {
                assertEquals(
                    Real.of(BigDecimal.of(case.string)).hashCode(),
                    Real.of(BigDecimal.of(case.string + trailingZeros)).hashCode(),
                )
            }
        }
    })

private inline val loggingOn get() = false
