package it.unibo.tuprolog.core.parsing.test

import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.dsl.prolog
import org.gciatto.kt.math.BigInteger
import kotlin.test.Test
import kotlin.test.assertEquals

class DoubleChecksOnEqualities {

    @Test
    fun testNumsAreEquals1() {
        assertEquals(Integer.of(1), prolog {
            1.toTerm()
        })
    }

    @Test
    fun testRealsInitialization() {
        assertEquals(Real.of(3.1), Real.of("3.1"))
    }

    @Test
    fun testNumsAreEquals2() {
        assertEquals(Integer.of(1), prolog {
            numOf(1)
        })
    }

    @Test
    fun testNumsAreEquals3() {
        assertEquals(Integer.of(1), prolog {
            numOf(1L)
        })
    }

    @Test
    fun testNumsAreEquals4() {
        assertEquals(Integer.of(1), prolog {
            numOf(BigInteger.ONE)
        })
    }

    @Test
    fun testNumsAreEquals5() {
        assertEquals(Integer.of(1), prolog {
            numOf(BigInteger.TEN / BigInteger.TEN)
        })
    }

    @Test
    fun testListsAreEquals1() {
        assertEquals(Cons.singleton(Integer.of(1)), prolog {
            listOf(1)
        })
    }

    @Test
    fun testListsAreEquals2() {
        assertEquals(Cons.singleton(Integer.of(1)), prolog {
            listOf(numOf(1))
        })
    }

    @Test
    fun testListsAreEquals3() {
        assertEquals(Cons.singleton(Integer.of(1)), prolog {
            consOf(1, emptyList())
        })
    }

    @Test
    fun testListsAreEquals4() {
        assertEquals(Cons.singleton(Integer.of(1)), prolog {
            consOf(numOf(1), emptyList())
        })
    }

    @Test
    fun testListsAreEquals5() {
        assertEquals(Cons.singleton(Integer.of(1)), prolog {
            consOf(Integer.of(1), emptyList())
        })
    }

    @Test
    fun testStructsAreEquals() {
        assertEquals(Struct.of("+", Integer.of(1), Integer.of(2)), prolog {
            1.toTerm() + 2
        })
    }
}