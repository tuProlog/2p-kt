package it.unibo.tuprolog.solve.libs.oop

import org.junit.Test
import kotlin.reflect.KParameter
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.fail

class TestKotlinReflectionWorkaroundsAreStillNecessary {

    private inline fun <reified T : Throwable> assertExceptionIsThrown(action: () -> Unit) {
        try {
            action()
            println("\t... works fine.")
            fail("A Kotlin bug has been fixed, please remove this test")
        } catch (e: Throwable) {
            println("\t... throws ${e::class}: ${e.message}.")
            e.printStackTrace()
            assertTrue("Expected exception type ${T::class}, actual: ${e::class}") { e is T }
        }
    }

    @Test
    fun testIntInstanceMethods() {
        val x = 42
        for (method in Int::class.members.filter { it.parameters.size == 1 && it.parameters[0].kind == KParameter.Kind.INSTANCE }) {
            assertExceptionIsThrown<Error> {
                println("Testing $method ...")
                method.call(x)
            }
        }
    }

    @Test
    fun testLongInstanceMethods() {
        val x = 42L
        for (method in Long::class.members.filter { it.parameters.size == 1 && it.parameters[0].kind == KParameter.Kind.INSTANCE }) {
            assertExceptionIsThrown<Error> {
                println("Testing $method ...")
                method.call(x)
            }
        }
    }

    @Test
    fun testCharInstanceMethods() {
        val x = 'b'
        for (method in Char::class.members.filter { it.parameters.size == 1 && it.parameters[0].kind == KParameter.Kind.INSTANCE }) {
            assertExceptionIsThrown<Error> {
                println("Testing $method ...")
                method.call(x)
            }
        }
    }

    @Test
    fun testShortInstanceMethods() {
        val x = 42.toShort()
        for (method in Char::class.members.filter { it.parameters.size == 1 && it.parameters[0].kind == KParameter.Kind.INSTANCE }) {
            assertExceptionIsThrown<Throwable> {
                println("Testing $method ...")
                method.call(x)
            }
        }
    }

    @Test
    fun testDoubleInstanceMethods() {
        val x = 42.0
        for (method in Double::class.members.filter { it.parameters.size == 1 && it.parameters[0].kind == KParameter.Kind.INSTANCE }) {
            assertExceptionIsThrown<Throwable> {
                println("Testing $method ...")
                method.call(x)
            }
        }
    }

    @Test
    fun testFloatInstanceMethods() {
        val x = 42f
        for (method in Float::class.members.filter { it.parameters.size == 1 && it.parameters[0].kind == KParameter.Kind.INSTANCE }) {
            assertExceptionIsThrown<Throwable> {
                println("Testing $method ...")
                method.call(x)
            }
        }
    }

    @Test
    fun testInnerClasses() {
        assertFalse(A.B::class.isInner)
    }
}
