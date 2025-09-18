package it.unibo.tuprolog.solve.libs.oop

import org.junit.Ignore
import org.junit.Test
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.test.assertTrue
import kotlin.test.fail

@Ignore
class TestKotlinReflectionWorkaroundsAreStillNecessary {
    @Suppress("PrintStackTrace")
    private inline fun <reified T : Throwable> assertExceptionIsThrown(action: () -> Unit) {
        try {
            action()
            println("\t... works fine.")
            fail("A Kotlin bug has been fixed, please remove this test")
        } catch (e: AssertionError) {
            throw e
        } catch (e: Throwable) {
            println("\t... throws ${e::class}: ${e.message}.")
            e.printStackTrace()
            assertTrue("Expected exception type ${T::class}, actual: ${e::class}") { e is T }
        }
    }

    private val KClass<*>.instanceMembersWithNoArg
        get() =
            members
                .asSequence()
                .filter { it.parameters.size == 1 && it.parameters[0].kind == KParameter.Kind.INSTANCE }
                .filterNot { it.name in setOf("hashCode", "toString") }

    @Test
    fun testIntInstanceMethods() {
        val x = 42
        for (method in Int::class.instanceMembersWithNoArg) {
            assertExceptionIsThrown<Error> {
                println("Testing $method ...")
                method.call(x)
            }
        }
    }

    @Test
    fun testLongInstanceMethods() {
        val x = 42L
        for (method in Long::class.instanceMembersWithNoArg) {
            assertExceptionIsThrown<Error> {
                println("Testing $method ...")
                method.call(x)
            }
        }
    }

    @Test
    fun testCharInstanceMethods() {
        val x = 'b'
        for (method in Char::class.instanceMembersWithNoArg) {
            assertExceptionIsThrown<Error> {
                println("Testing $method ...")
                method.call(x)
            }
        }
    }

    @Test
    fun testShortInstanceMethods() {
        val x = 42.toShort()
        for (method in Char::class.instanceMembersWithNoArg) {
            assertExceptionIsThrown<Throwable> {
                println("Testing $method ...")
                method.call(x)
            }
        }
    }

    @Test
    fun testDoubleInstanceMethods() {
        val x = 42.0
        for (method in Double::class.instanceMembersWithNoArg) {
            assertExceptionIsThrown<Throwable> {
                println("Testing $method ...")
                method.call(x)
            }
        }
    }

    @Test
    fun testFloatInstanceMethods() {
        val x = 42f
        for (method in Float::class.instanceMembersWithNoArg) {
            assertExceptionIsThrown<Throwable> {
                println("Testing $method ...")
                method.call(x)
            }
        }
    }
}
