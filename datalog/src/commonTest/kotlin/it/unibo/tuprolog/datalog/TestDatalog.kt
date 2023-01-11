package it.unibo.tuprolog.datalog

import it.unibo.tuprolog.datalog.exception.DatalogViolationException
import it.unibo.tuprolog.theory.Theory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.fail

class TestDatalog {

    private fun assertTheoryIsBroken(theory: Theory, error: DatalogViolationException) {
        assertFalse(theory.isDatalog)
        try {
            theory.ensureIsDatalog()
            fail("Expected DatalogViolationException, while none was thrown")
        } catch (e: DatalogViolationException) {
            assertEquals(error, e)
        }
    }

    @Test
    fun testCompoundTermsDetection() {
        Instances.theoryWithCompoundTerms.let {
            val rule = it.rules.first()
            assertFalse(rule.hasNoCompound)
            val error: DatalogViolationException? = try {
                rule.ensureHasNoCompound()
                null
            } catch (e: DatalogViolationException) {
                e
            }
            assertTheoryIsBroken(it, error!!)
        }
    }

    @Test
    fun testHeadVariablesOutsideNonNegatedLiteralsDetection() {
        Instances.theoryWithFreeVariablesInTheHead.let {
            val rule = it.rules.first()
            assertFalse(rule.allHeadVariablesInNonNegatedLiterals)
            val error: DatalogViolationException? = try {
                rule.ensureAllHeadVariablesInNonNegatedLiterals()
                null
            } catch (e: DatalogViolationException) {
                e
            }
            assertTheoryIsBroken(it, error!!)
        }
    }

    @Test
    fun testFreeVariablesInNegatedLiteralsDetection() {
        Instances.theoryWithFreeVariablesInNegatedBodyLiterals.let {
            val rule = it.rules.first()
            assertFalse(rule.allNegatedLiteralsVariablesInNonNegatedLiteralsToo)
            val error: DatalogViolationException? = try {
                rule.ensureAllNegatedLiteralsVariablesInNonNegatedLiteralsToo()
                null
            } catch (e: DatalogViolationException) {
                e
            }
            assertTheoryIsBroken(it, error!!)
        }
    }

    @Test
    fun testRecursionDetection() {
        Instances.theoryWithRecursion.let {
            assertFalse(it.isNonRecursive)
            val error: DatalogViolationException? = try {
                it.ensureIsNonRecursive()
                null
            } catch (e: DatalogViolationException) {
                e
            }
            assertTheoryIsBroken(it, error!!)
        }
    }

    @Test
    fun testDatalogDetection() {
        Instances.datalogTheory.let {
            println(it.callGraph)
            it.ensureIsDatalog()
            assertTrue(it.isDatalog)
        }
    }
}
