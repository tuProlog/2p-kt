package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.EmptyListImpl
import it.unibo.tuprolog.core.impl.EmptySetImpl
import kotlin.test.Test
import kotlin.test.assertSame

/**
 * Test class for [Empty] and its companion object
 *
 * @author Enrico
 */
class EmptyTest {

    @Test
    fun listMethodShouldReturnEmptyList() {
        assertSame(EmptyListImpl, Empty.list())
    }

    @Test
    fun setMethodShouldReturnEmptySet() {
        assertSame(EmptySetImpl, Empty.set())
    }
}
