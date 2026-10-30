package it.unibo.tuprolog.datalog

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.datalog.exception.DatalogViolationException
import it.unibo.tuprolog.theory.Theory
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.fail

class DatalogSpec :
    FunSpec({
        fun assertTheoryIsBroken(
            theory: Theory,
            error: DatalogViolationException,
        ) {
            assertFalse(theory.isDatalog)
            try {
                theory.ensureIsDatalog()
                fail("Expected DatalogViolationException, while none was thrown")
            } catch (e: DatalogViolationException) {
                assertEquals(error, e)
            }
        }

        test("compound-terms-detection") {
            Instances.theoryWithCompoundTerms.let {
                val rule = it.rules.first()
                assertFalse(rule.hasNoCompound)
                val error: DatalogViolationException? =
                    try {
                        rule.ensureHasNoCompound()
                        null
                    } catch (e: DatalogViolationException) {
                        e
                    }
                assertTheoryIsBroken(it, error!!)
            }
        }

        test("head-variables-outside-non-negated-literals-detection") {
            Instances.theoryWithFreeVariablesInTheHead.let {
                val rule = it.rules.first()
                assertFalse(rule.allHeadVariablesInNonNegatedLiterals)
                val error: DatalogViolationException? =
                    try {
                        rule.ensureAllHeadVariablesInNonNegatedLiterals()
                        null
                    } catch (e: DatalogViolationException) {
                        e
                    }
                assertTheoryIsBroken(it, error!!)
            }
        }

        test("free-variables-in-negated-literals-detection") {
            Instances.theoryWithFreeVariablesInNegatedBodyLiterals.let {
                val rule = it.rules.first()
                assertFalse(rule.allNegatedLiteralsVariablesInNonNegatedLiteralsToo)
                val error: DatalogViolationException? =
                    try {
                        rule.ensureAllNegatedLiteralsVariablesInNonNegatedLiteralsToo()
                        null
                    } catch (e: DatalogViolationException) {
                        e
                    }
                assertTheoryIsBroken(it, error!!)
            }
        }

        test("recursion-detection") {
            Instances.theoryWithRecursion.let {
                assertFalse(it.isNonRecursive)
                val error: DatalogViolationException? =
                    try {
                        it.ensureIsNonRecursive()
                        null
                    } catch (e: DatalogViolationException) {
                        e
                    }
                assertTheoryIsBroken(it, error!!)
            }
        }

        test("datalog-detection") {
            Instances.datalogTheory.let {
                println(it.callGraph)
                it.ensureIsDatalog()
                assertTrue(it.isDatalog)
            }
        }
    })
