@file:Suppress("LocalVariableName", "ktlint:standard:property-naming")

package it.unibo.tuprolog.dsl

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Var
import kotlin.test.assertEquals

class LogicProgrammingScopeWithVariablesSpec :
    FunSpec({
        with(LogicProgrammingScopeTester<LogicProgrammingScopeWithVariables<*>> { LogicProgrammingScope.empty() }) {
            fun LogicProgrammingScopeWithVariables<*>.testLetter(
                letter: Char,
                property: () -> Var,
            ) {
                val expected = varOf(letter.toString())
                val actual = property()
                assertEquals(expected, actual)
            }

            test("var-underscore") {
                logicProgramming {
                    val expected = anonymous()
                    val actual = `_`
                    assertAreDifferentUnderscores(expected, actual)
                }
            }

            test("var-a") {
                logicProgramming {
                    testLetter('A') { A }
                    val A: Var by this
                    assertEquals(varOf("A"), A)
                }
            }

            test("var-b") {
                logicProgramming {
                    testLetter('B') { B }
                    val B: Var by this
                    assertEquals(varOf("B"), B)
                }
            }

            test("var-c") {
                logicProgramming {
                    testLetter('C') { C }
                    val C: Var by this
                    assertEquals(varOf("C"), C)
                }
            }

            test("var-d") {
                logicProgramming {
                    testLetter('D') { D }
                    val D: Var by this
                    assertEquals(varOf("D"), D)
                }
            }

            test("var-e") {
                logicProgramming {
                    testLetter('E') { E }
                    val E: Var by this
                    assertEquals(varOf("E"), E)
                }
            }

            test("var-f") {
                logicProgramming {
                    testLetter('F') { F }
                    val F: Var by this
                    assertEquals(varOf("F"), F)
                }
            }

            test("var-g") {
                logicProgramming {
                    testLetter('G') { G }
                    val G: Var by this
                    assertEquals(varOf("G"), G)
                }
            }

            test("var-h") {
                logicProgramming {
                    testLetter('H') { H }
                    val H: Var by this
                    assertEquals(varOf("H"), H)
                }
            }

            test("var-i") {
                logicProgramming {
                    testLetter('I') { I }
                    val I: Var by this
                    assertEquals(varOf("I"), I)
                }
            }

            test("var-j") {
                logicProgramming {
                    testLetter('J') { J }
                    val J: Var by this
                    assertEquals(varOf("J"), J)
                }
            }

            test("var-k") {
                logicProgramming {
                    testLetter('K') { K }
                    val K: Var by this
                    assertEquals(varOf("K"), K)
                }
            }

            test("var-l") {
                logicProgramming {
                    testLetter('L') { L }
                    val L: Var by this
                    assertEquals(varOf("L"), L)
                }
            }

            test("var-m") {
                logicProgramming {
                    testLetter('M') { M }
                    val M: Var by this
                    assertEquals(varOf("M"), M)
                }
            }

            test("var-n") {
                logicProgramming {
                    testLetter('N') { N }
                    val N: Var by this
                    assertEquals(varOf("N"), N)
                }
            }

            test("var-o") {
                logicProgramming {
                    testLetter('O') { O }
                    val O: Var by this
                    assertEquals(varOf("O"), O)
                }
            }

            test("var-p") {
                logicProgramming {
                    testLetter('P') { P }
                    val P: Var by this
                    assertEquals(varOf("P"), P)
                }
            }

            test("var-q") {
                logicProgramming {
                    testLetter('Q') { Q }
                    val Q: Var by this
                    assertEquals(varOf("Q"), Q)
                }
            }

            test("var-r") {
                logicProgramming {
                    testLetter('R') { R }
                    val R: Var by this
                    assertEquals(varOf("R"), R)
                }
            }

            test("var-s") {
                logicProgramming {
                    testLetter('S') { S }
                    val S: Var by this
                    assertEquals(varOf("S"), S)
                }
            }

            test("var-t") {
                logicProgramming {
                    testLetter('T') { T }
                    val T: Var by this
                    assertEquals(varOf("T"), T)
                }
            }

            test("var-u") {
                logicProgramming {
                    testLetter('U') { U }
                    val U: Var by this
                    assertEquals(varOf("U"), U)
                }
            }

            test("var-v") {
                logicProgramming {
                    testLetter('V') { V }
                    val V: Var by this
                    assertEquals(varOf("V"), V)
                }
            }

            test("var-w") {
                logicProgramming {
                    testLetter('W') { W }
                    val W: Var by this
                    assertEquals(varOf("W"), W)
                }
            }

            test("var-x") {
                logicProgramming {
                    testLetter('X') { X }
                    val X: Var by this
                    assertEquals(varOf("X"), X)
                }
            }

            test("var-y") {
                logicProgramming {
                    testLetter('Y') { Y }
                    val Y: Var by this
                    assertEquals(varOf("Y"), Y)
                }
            }

            test("var-z") {
                logicProgramming {
                    testLetter('Z') { Z }
                    val Z: Var by this
                    assertEquals(varOf("Z"), Z)
                }
            }
        }
    })
