package it.unibo.tuprolog.core.parsing.test

import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.dsl.logicProgramming
import org.gciatto.kt.math.BigInteger
import kotlin.test.Test
import kotlin.test.assertEquals

class DoubleChecksOnEqualities {
    @Test
    fun testNumsAreEquals1() {
        assertEquals(
            Integer.of(1),
            logicProgramming {
                1.toTerm()
            },
        )
    }

    @Test
    fun testRealsInitialization() {
        assertEquals(
            Real.of("3.100000000000000088817841970012523233890533447265625"),
            logicProgramming {
                numOf(3.1)
            },
        )
    }

    @Test
    fun testNumsAreEquals2() {
        assertEquals(
            Integer.of(1),
            logicProgramming {
                numOf(1)
            },
        )
    }

    @Test
    fun testNumsAreEquals3() {
        assertEquals(
            Integer.of(1),
            logicProgramming {
                numOf(1L)
            },
        )
    }

    @Test
    fun testNumsAreEquals4() {
        assertEquals(
            Integer.of(1),
            logicProgramming {
                numOf(BigInteger.ONE)
            },
        )
    }

    @Test
    fun testNumsAreEquals5() {
        assertEquals(
            Integer.of(1),
            logicProgramming {
                numOf(BigInteger.TEN / BigInteger.TEN)
            },
        )
    }

    @Test
    fun testListsAreEquals1() {
        assertEquals(
            Cons.singleton(Integer.of(1)),
            logicProgramming {
                logicListOf(1)
            },
        )
    }

    @Test
    fun testListsAreEquals2() {
        assertEquals(
            Cons.singleton(Integer.of(1)),
            logicProgramming {
                logicListOf(numOf(1))
            },
        )
    }

    @Test
    fun testListsAreEquals3() {
        assertEquals(
            Cons.singleton(Integer.of(1)),
            logicProgramming {
                consOf(1, emptyLogicList)
            },
        )
    }

    @Test
    fun testListsAreEquals4() {
        assertEquals(
            Cons.singleton(Integer.of(1)),
            logicProgramming {
                consOf(numOf(1), emptyLogicList)
            },
        )
    }

    @Test
    fun testListsAreEquals5() {
        assertEquals(
            Cons.singleton(Integer.of(1)),
            logicProgramming {
                consOf(Integer.of(1), emptyLogicList)
            },
        )
    }

    @Test
    fun testStructsAreEquals() {
        assertEquals(
            Struct.of("+", Integer.of(1), Integer.of(2)),
            logicProgramming {
                1.toTerm() + 2
            },
        )
    }
}
