package it.unibo.tuprolog.solve.libs.oop

import org.junit.Test
import kotlin.test.assertEquals

class TestTypesRetrieval {
    @Test
    fun testTypeFactoryWithCustomTypes() {
        val factory = TypeFactory.default
        assertEquals(A::class, factory.typeFromName(A::class.fullName))
        assertEquals(A.B::class, factory.typeFromName(A.B::class.fullName))
        assertEquals(A.C::class, factory.typeFromName(A.C::class.fullName))
        assertEquals(Conversions::class, factory.typeFromName(Conversions::class.fullName))
    }
}
