package it.unibo.tuprolog.core.parsing.test

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.dsl.logicProgramming
import org.gciatto.kt.math.BigInteger

class DoubleChecksOnEqualitiesSpec :
    FunSpec({
        test("nums-are-equals1") {
            (
                logicProgramming {
                    1.toTerm()
                }
            ) shouldBe Integer.of(1)
        }

        test("reals-initialization") {
            (
                logicProgramming {
                    numOf(3.1)
                }
            ) shouldBe Real.of("3.100000000000000088817841970012523233890533447265625")
        }

        test("nums-are-equals2") {
            (
                logicProgramming {
                    numOf(1)
                }
            ) shouldBe Integer.of(1)
        }

        test("nums-are-equals3") {
            (
                logicProgramming {
                    numOf(1L)
                }
            ) shouldBe Integer.of(1)
        }

        test("nums-are-equals4") {
            (
                logicProgramming {
                    numOf(BigInteger.ONE)
                }
            ) shouldBe Integer.of(1)
        }

        test("nums-are-equals5") {
            (
                logicProgramming {
                    numOf(BigInteger.TEN / BigInteger.TEN)
                }
            ) shouldBe Integer.of(1)
        }

        test("lists-are-equals1") {
            (
                logicProgramming {
                    logicListOf(1)
                }
            ) shouldBe Cons.singleton(Integer.of(1))
        }

        test("lists-are-equals2") {
            (
                logicProgramming {
                    logicListOf(numOf(1))
                }
            ) shouldBe Cons.singleton(Integer.of(1))
        }

        test("lists-are-equals3") {
            (
                logicProgramming {
                    consOf(1, emptyLogicList)
                }
            ) shouldBe Cons.singleton(Integer.of(1))
        }

        test("lists-are-equals4") {
            (
                logicProgramming {
                    consOf(numOf(1), emptyLogicList)
                }
            ) shouldBe Cons.singleton(Integer.of(1))
        }

        test("lists-are-equals5") {
            (
                logicProgramming {
                    consOf(Integer.of(1), emptyLogicList)
                }
            ) shouldBe Cons.singleton(Integer.of(1))
        }

        test("structs-are-equals") {
            (
                logicProgramming {
                    1.toTerm() + 2
                }
            ) shouldBe Struct.of("+", Integer.of(1), Integer.of(2))
        }
    })
