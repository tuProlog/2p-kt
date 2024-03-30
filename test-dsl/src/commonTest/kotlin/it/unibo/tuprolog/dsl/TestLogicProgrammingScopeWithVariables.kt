package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Var
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("LocalVariableName", "ktlint:standard:property-naming")
class TestLogicProgrammingScopeWithVariables :
    AbstractLogicProgrammingScopeTest<LogicProgrammingScopeWithVariables<*>>() {
    override fun createLogicProgrammingScope(): LogicProgrammingScopeWithVariables<*> = LogicProgrammingScope.empty()

    private fun LogicProgrammingScopeWithVariables<*>.testLetter(
        letter: Char,
        property: () -> Var,
    ) {
        val expected = varOf(letter.toString())
        val actual = property()
        assertEquals(expected, actual)
    }

    @Test
    fun testVarUnderscore() =
        logicProgramming {
            val expected = anonymous()
            val actual = `_`
            assertAreDifferentUnderscores(expected, actual)
        }

    @Test
    fun testVarA() =
        logicProgramming {
            testLetter('A') { A }
            val A: Var by this
            assertEquals(varOf("A"), A)
        }

    @Test
    fun testVarB() =
        logicProgramming {
            testLetter('B') { B }
            val B: Var by this
            assertEquals(varOf("B"), B)
        }

    @Test
    fun testVarC() =
        logicProgramming {
            testLetter('C') { C }
            val C: Var by this
            assertEquals(varOf("C"), C)
        }

    @Test
    fun testVarD() =
        logicProgramming {
            testLetter('D') { D }
            val D: Var by this
            assertEquals(varOf("D"), D)
        }

    @Test
    fun testVarE() =
        logicProgramming {
            testLetter('E') { E }
            val E: Var by this
            assertEquals(varOf("E"), E)
        }

    @Test
    fun testVarF() =
        logicProgramming {
            testLetter('F') { F }
            val F: Var by this
            assertEquals(varOf("F"), F)
        }

    @Test
    fun testVarG() =
        logicProgramming {
            testLetter('G') { G }
            val G: Var by this
            assertEquals(varOf("G"), G)
        }

    @Test
    fun testVarH() =
        logicProgramming {
            testLetter('H') { H }
            val H: Var by this
            assertEquals(varOf("H"), H)
        }

    @Test
    fun testVarI() =
        logicProgramming {
            testLetter('I') { I }
            val I: Var by this
            assertEquals(varOf("I"), I)
        }

    @Test
    fun testVarJ() =
        logicProgramming {
            testLetter('J') { J }
            val J: Var by this
            assertEquals(varOf("J"), J)
        }

    @Test
    fun testVarK() =
        logicProgramming {
            testLetter('K') { K }
            val K: Var by this
            assertEquals(varOf("K"), K)
        }

    @Test
    fun testVarL() =
        logicProgramming {
            testLetter('L') { L }
            val L: Var by this
            assertEquals(varOf("L"), L)
        }

    @Test
    fun testVarM() =
        logicProgramming {
            testLetter('M') { M }
            val M: Var by this
            assertEquals(varOf("M"), M)
        }

    @Test
    fun testVarN() =
        logicProgramming {
            testLetter('N') { N }
            val N: Var by this
            assertEquals(varOf("N"), N)
        }

    @Test
    fun testVarO() =
        logicProgramming {
            testLetter('O') { O }
            val O: Var by this
            assertEquals(varOf("O"), O)
        }

    @Test
    fun testVarP() =
        logicProgramming {
            testLetter('P') { P }
            val P: Var by this
            assertEquals(varOf("P"), P)
        }

    @Test
    fun testVarQ() =
        logicProgramming {
            testLetter('Q') { Q }
            val Q: Var by this
            assertEquals(varOf("Q"), Q)
        }

    @Test
    fun testVarR() =
        logicProgramming {
            testLetter('R') { R }
            val R: Var by this
            assertEquals(varOf("R"), R)
        }

    @Test
    fun testVarS() =
        logicProgramming {
            testLetter('S') { S }
            val S: Var by this
            assertEquals(varOf("S"), S)
        }

    @Test
    fun testVarT() =
        logicProgramming {
            testLetter('T') { T }
            val T: Var by this
            assertEquals(varOf("T"), T)
        }

    @Test
    fun testVarU() =
        logicProgramming {
            testLetter('U') { U }
            val U: Var by this
            assertEquals(varOf("U"), U)
        }

    @Test
    fun testVarV() =
        logicProgramming {
            testLetter('V') { V }
            val V: Var by this
            assertEquals(varOf("V"), V)
        }

    @Test
    fun testVarW() =
        logicProgramming {
            testLetter('W') { W }
            val W: Var by this
            assertEquals(varOf("W"), W)
        }

    @Test
    fun testVarX() =
        logicProgramming {
            testLetter('X') { X }
            val X: Var by this
            assertEquals(varOf("X"), X)
        }

    @Test
    fun testVarY() =
        logicProgramming {
            testLetter('Y') { Y }
            val Y: Var by this
            assertEquals(varOf("Y"), Y)
        }

    @Test
    fun testVarZ() =
        logicProgramming {
            testLetter('Z') { Z }
            val Z: Var by this
            assertEquals(varOf("Z"), Z)
        }
}
