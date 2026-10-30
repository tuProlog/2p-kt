package it.unibo.tuprolog.core.parsing.test

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.dsl.logicProgramming
import org.gciatto.kt.math.BigInteger
import kotlin.test.assertEquals

class DoubleChecksOnEqualitiesSpec :
    FunSpec({
        test("nums-are-equals1") {
            assertEquals(
                Integer.of(1),
                logicProgramming {
                    1.toTerm()
                },
            )
        }

        test("reals-initialization") {
            assertEquals(
                Real.of("3.100000000000000088817841970012523233890533447265625"),
                logicProgramming {
                    numOf(3.1)
                },
            )
        }

        test("nums-are-equals2") {
            assertEquals(
                Integer.of(1),
                logicProgramming {
                    numOf(1)
                },
            )
        }

        test("nums-are-equals3") {
            assertEquals(
                Integer.of(1),
                logicProgramming {
                    numOf(1L)
                },
            )
        }

        test("nums-are-equals4") {
            assertEquals(
                Integer.of(1),
                logicProgramming {
                    numOf(BigInteger.ONE)
                },
            )
        }

        test("nums-are-equals5") {
            assertEquals(
                Integer.of(1),
                logicProgramming {
                    numOf(BigInteger.TEN / BigInteger.TEN)
                },
            )
        }

        test("lists-are-equals1") {
            assertEquals(
                Cons.singleton(Integer.of(1)),
                logicProgramming {
                    logicListOf(1)
                },
            )
        }

        test("lists-are-equals2") {
            assertEquals(
                Cons.singleton(Integer.of(1)),
                logicProgramming {
                    logicListOf(numOf(1))
                },
            )
        }

        test("lists-are-equals3") {
            assertEquals(
                Cons.singleton(Integer.of(1)),
                logicProgramming {
                    consOf(1, emptyLogicList)
                },
            )
        }

        test("lists-are-equals4") {
            assertEquals(
                Cons.singleton(Integer.of(1)),
                logicProgramming {
                    consOf(numOf(1), emptyLogicList)
                },
            )
        }

        test("lists-are-equals5") {
            assertEquals(
                Cons.singleton(Integer.of(1)),
                logicProgramming {
                    consOf(Integer.of(1), emptyLogicList)
                },
            )
        }

        test("structs-are-equals") {
            assertEquals(
                Struct.of("+", Integer.of(1), Integer.of(2)),
                logicProgramming {
                    1.toTerm() + 2
                },
            )
        }
    })
