package it.unibo.tuprolog.utils

import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals

class TestNameReflection {
    private val myClassFullName = "it.unibo.tuprolog.utils.MyClass"
    private val myClassName = myClassFullName.substringAfterLast('.')
    private val innerClassFullName = "it.unibo.tuprolog.utils.MyClass.InnerClass"
    private val innerClassName = innerClassFullName.substringAfterLast('.')

    private fun assertNameEquals(
        expected: String,
        klass: KClass<*>,
    ) {
        assertEquals(expected, klass.name)
    }

    private fun assertFullNameEquals(
        expected: String,
        klass: KClass<*>,
    ) {
        try {
            assertEquals(expected, klass.fullName)
        } catch (e: IllegalStateException) {
            assertEquals("Cannot reflect full name of class ${klass.name} on JS", e.message)
        }
    }

    @Test
    fun testClassFullName() = assertFullNameEquals(myClassFullName, MyClass::class)

    @Test
    fun testClassName() = assertNameEquals(myClassName, MyClass::class)

    @Test
    fun testInnerClassFullName() = assertFullNameEquals(innerClassFullName, MyClass.InnerClass::class)

    @Test
    fun testInnerClassName() = assertNameEquals(innerClassName, MyClass.InnerClass::class)

    @Test
    fun testInstanceFullName() = assertFullNameEquals(myClassFullName, MyClass()::class)

    @Test
    fun testInstanceName() = assertNameEquals(myClassName, MyClass()::class)

    @Test
    fun testInnerInstanceFullName() = assertFullNameEquals(innerClassFullName, MyClass.InnerClass()::class)

    @Test
    fun testInnerInstanceName() = assertNameEquals(innerClassName, MyClass.InnerClass()::class)
}
