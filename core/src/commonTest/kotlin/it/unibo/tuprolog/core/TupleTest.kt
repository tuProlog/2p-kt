package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

/**
 * Test class for [Tuple] companion object
 *
 * @author Enrico
 */
internal class TupleTest {

    @Test
    fun wrapIfNeededWithNoArgs() {
        assertSame(Truth.`true`(), Tuple.wrapIfNeeded())
    }

    @Test
    fun wrapIfNeededWithOneArgument() {
        val something = Atom.of("hey")
        assertSame(something, Tuple.wrapIfNeeded(something))
    }

    @Test
    fun wrapIfNeededWithTwoOrMoreArgumentsReturnsTupleInstance() {
        val something1 = Atom.of("hello")
        val something2 = Atom.of("world")

        val toBeTested = Tuple.wrapIfNeeded(something1, something2)

        TermTypeAssertionUtils.assertIsTuple(toBeTested)
        assertEquals("(hello, world)", toBeTested.toString())
    }

    @Test
    fun wrapIfNeededReturnsDefaultIfNoArgumentsProvided() {
        val myDefault = Atom.of("myDefault")

        assertSame(myDefault, Tuple.wrapIfNeeded { myDefault })
    }

    // TODO: 03/07/2019 ofTesting
}
