package it.unibo.tuprolog.core

import kotlin.test.*

class TestVar {

    val varPattern = """^[_A-Z][a-zA-Z0-9_]*$""".toRegex()

    val varsUnderTest: Array<String> = arrayOf("A", "X", "_", "_X", "_1", "X1", "X_1", "a", "x")

    @Test
    fun creation() {
        varsUnderTest.forEach {
            val var1 = Var.of(it)
            val var2 = Var.of(it)

            assertEquals(var1, var2)
            assertNotSame(var1, var2)
            assertEquals(var1.name, var2.name)
            assertNotEquals(var1.completeName, var2.completeName)
            assertTrue(var1 structurallyEquals var2)
            assertFalse(var1 strictlyEquals var2)
        }
    }

    @Test
    fun nameWellFormed() {
        varsUnderTest.forEach {
            val var1 = Var.of(it)

            if (var1.name.matches(varPattern)) {
                assertTrue(var1.isNameWellFormed)
            } else {
                assertFalse(var1.isNameWellFormed)
            }
        }
    }

    @Test
    fun cloneAsAtom() {

        varsUnderTest.forEach { value ->
            val var1 = Var.of(value)
            val var2 = var1.freshCopy()

            assertEquals(var1, var2)
            assertNotSame(var1, var2)
            assertEquals(var1.name, var2.name)
            assertNotEquals(var1.completeName, var2.completeName)
            assertTrue(var1 structurallyEquals var2)
            assertFalse(var1 strictlyEquals var2)
        }
    }

    @Test
    fun typeTesting() {
        varsUnderTest.map { Var.of(it) }.forEach {

            assertTrue(it is Var)
            assertTrue(it is Term)

            assertTrue(it.isVariable)
            assertTrue(it.isVariable)
            assertFalse(it.isBound)

            if (it.name == "_") {
                assertTrue(it.isAnonymous)
            } else {
                assertFalse(it.isAnonymous)
            }



            assertFalse(it is Numeric)
            assertFalse(it is Struct)
            assertFalse(it is Clause)
            assertFalse(it is Couple)

            assertFalse(it.isNumber)
            assertFalse(it.isReal)
            assertFalse(it.isInt)
            assertFalse(it.isClause)
            assertFalse(it.isDirective)
            assertFalse(it.isFact)
            assertFalse(it.isRule)
            assertFalse(it.isCouple)
            assertFalse(it.isStruct)
            assertFalse(it.isAtom)
        }
    }

    @Test
    fun toStringRepresentation() {
        varsUnderTest.forEach {
            if (it.matches(varPattern)) {
                assertEquals(it, Var.of(it).toString())
            } else {
                assertEquals("¿$it?", Var.of(it).toString())
            }
        }

    }

    @Test
    fun anonymous() {
        varsUnderTest.forEach {
            val var1 = Var.of(it)

            if (it == "_") {
                assertTrue(var1.isAnonymous)
            } else {
                assertFalse(var1.isAnonymous)
            }
        }

    }

}