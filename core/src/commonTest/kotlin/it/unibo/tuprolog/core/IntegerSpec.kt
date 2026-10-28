package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.impl.IntegerImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.IntegerUtils
import org.gciatto.kt.math.BigInteger
import kotlin.test.assertTrue

/**
 * Test class for [Integer] companion object
 *
 * @author Enrico
 */
class IntegerSpec :
    FunSpec({
        withTests(nameFn = { "of-big-integer/${it.id}" }, IntegerUtils.integerCases) { case ->
            assertEqualities(Integer.of(case.value), IntegerImpl(case.value))
        }

        withTests(nameFn = { "of-long/${it.id}" }, IntegerUtils.integerCases.filter { it.long != null }) { case ->
            assertEqualities(Integer.of(case.long!!), IntegerImpl(BigInteger.of(case.long!!)))
        }

        withTests(nameFn = { "of-int/${it.id}" }, IntegerUtils.integerCases.filter { it.int != null }) { case ->
            assertEqualities(Integer.of(case.int!!), IntegerImpl(BigInteger.of(case.int!!)))
        }

        withTests(nameFn = { "of-short/${it.id}" }, IntegerUtils.integerCases.filter { it.short != null }) { case ->
            assertEqualities(Integer.of(case.short!!), IntegerImpl(BigInteger.of(case.short!!.toLong())))
        }

        withTests(nameFn = { "of-byte/${it.id}" }, IntegerUtils.integerCases.filter { it.byte != null }) { case ->
            assertEqualities(Integer.of(case.byte!!), IntegerImpl(BigInteger.of(case.byte!!.toLong())))
        }

        withTests(nameFn = { "of-string/${it.id}" }, IntegerUtils.integerCases) { case ->
            assertEqualities(Integer.of(case.string), IntegerImpl(case.value))
        }

        test("integer-of-string-with-radix") {
            val correct = IntegerImpl(BigInteger.of(34))
            val toTest = Integer.of("22", 16)

            assertEqualities(toTest, correct)
        }

        withTests(nameFn = { "string-matches-pattern/${it.id}" }, IntegerUtils.integerCases) { case ->
            assertTrue(case.string matches Integer.PATTERN)
        }

        withTests(nameFn = { "to-string-matches-pattern/${it.id}" }, IntegerUtils.integerCases) { case ->
            assertTrue(Integer.of(case.string).toString() matches Integer.PATTERN)
        }
    })
