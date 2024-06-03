package it.unibo.tuprolog.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class TestIdentifier {
    @Test
    fun testObjectId() {
        testIdentityHash({ Any() }, { Any() })
    }

    @Test
    fun testStringId() {
        testIdentityHash({ "hello" }, { "HELLO".lowercase() })
    }

    @Test
    fun testIntId() {
        testIdentityHash({ 1 }, { "1".toInt() })
    }

    private fun <T : Any> testIdentityHash(
        provider1: () -> T,
        provider2: () -> T,
    ) {
        val obj1 = box(provider1())
        val id1 = obj1.identifier
        val obj2 = box(provider2())
        val id2 = obj2.identifier
        assertEquals(id1, obj1.identifier)
        if (obj1 === obj2) {
            assertEquals(id1, id2)
        } else {
            assertNotEquals(id1, id2)
        }
    }
}
