package it.unibo.tuprolog.solve.flags

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.toTerm
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FlagDomainSpec :
    FunSpec({
        test("IntRange iterates every value in the range, not just the minimum") {
            val domain = FlagDomain.IntRange(1, 3)
            assertEquals(listOf(1.toTerm(), 2.toTerm(), 3.toTerm()), domain.toList())
        }

        test("IntRange with a single value iterates exactly that value") {
            val domain = FlagDomain.IntRange(5, 5)
            assertEquals(listOf(5.toTerm()), domain.toList())
        }

        test("IntRange contains every value between its bounds, inclusive") {
            val domain = FlagDomain.IntRange(1, 3)
            assertTrue(0.toTerm() !in domain)
            assertTrue(1.toTerm() in domain)
            assertTrue(2.toTerm() in domain)
            assertTrue(3.toTerm() in domain)
            assertTrue(4.toTerm() !in domain)
        }
    })
