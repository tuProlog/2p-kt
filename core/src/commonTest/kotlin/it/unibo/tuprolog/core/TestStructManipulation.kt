package it.unibo.tuprolog.core

import kotlin.test.Test
import kotlin.test.assertEquals

class TestStructManipulation {
    val f1 = Struct.of("f", Integer.ONE)

    @Test
    fun testAddLast() {
        assertEquals(
            Struct.of("f", Integer.ONE, Integer.of(2)),
            f1.addLast(Integer.of(2)),
        )
    }

    @Test
    fun testAppend() {
        assertEquals(
            Struct.of("f", Integer.ONE, Integer.of(2)),
            f1.append(Integer.of(2)),
        )
    }

    @Test
    fun testAddFirst() {
        assertEquals(
            Struct.of("f", Integer.of(2), Integer.ONE),
            f1.addFirst(Integer.of(2)),
        )
    }

    @Test
    fun testSetArgs() {
        assertEquals(
            Struct.of("f", Integer.of(2), Integer.of(3)),
            f1.setArgs(Integer.of(2), Integer.of(3)),
        )
    }

    @Test
    fun testSetFunctor() {
        assertEquals(
            Struct.of("g", Integer.ONE),
            f1.setFunctor("g"),
        )
    }

    @Test
    fun testInsertAt() {
        assertEquals(
            Struct.of("f", Integer.of(2), Integer.ONE),
            f1.insertAt(0, Integer.of(2)),
        )
    }
}
