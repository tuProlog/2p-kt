package it.unibo.tuprolog.datalog

import io.kotest.assertions.fail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.datalog.exception.DatalogViolationException
import it.unibo.tuprolog.theory.Theory

class DatalogSpec :
    FunSpec({
        fun assertTheoryIsBroken(
            theory: Theory,
            error: DatalogViolationException,
        ) {
            theory.isDatalog.shouldBeFalse()
            try {
                theory.ensureIsDatalog()
                fail("Expected DatalogViolationException, while none was thrown")
            } catch (e: DatalogViolationException) {
                e shouldBe error
            }
        }

        test("compound-terms-detection") {
            Instances.theoryWithCompoundTerms.let {
                val rule = it.rules.first()
                rule.hasNoCompound.shouldBeFalse()
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
                rule.allHeadVariablesInNonNegatedLiterals.shouldBeFalse()
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
                rule.allNegatedLiteralsVariablesInNonNegatedLiteralsToo.shouldBeFalse()
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
                it.isNonRecursive.shouldBeFalse()
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
                it.isDatalog.shouldBeTrue()
            }
        }
    })
