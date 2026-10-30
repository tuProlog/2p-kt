package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.impl.RealImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.RealUtils
import org.gciatto.kt.math.BigDecimal
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Test class for [Real] companion object
 *
 * @author Enrico
 */
class RealSpec :
    FunSpec({
        withTests(nameFn = { "of-big-decimal/${it.id}" }, RealUtils.realCases) { case ->
            assertEqualities(Real.of(case.value), RealImpl(case.value))
        }

        withTests(nameFn = { "of-double/${it.id}" }, RealUtils.realCases) { case ->
            assertEqualities(Real.of(case.double), RealImpl(BigDecimal.of(case.double)))
        }

        withTests(nameFn = { "of-float/${it.id}" }, RealUtils.realCases.filter { it.float != null }) { case ->
            assertEqualities(Real.of(case.float!!), RealImpl(BigDecimal.of(case.float!!)))
        }

        withTests(nameFn = { "of-string/${it.id}" }, RealUtils.realCases) { case ->
            assertEqualities(Real.of(case.string), RealImpl(case.value))
        }

        withTests(nameFn = { "string-matches-pattern/${it.id}" }, RealUtils.realCases) { case ->
            assertTrue(case.string matches Real.PATTERN)
        }

        withTests(nameFn = { "to-string-matches-pattern/${it.id}" }, RealUtils.realCases) { case ->
            assertTrue(Real.of(case.string).toString() matches Real.PATTERN)
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
