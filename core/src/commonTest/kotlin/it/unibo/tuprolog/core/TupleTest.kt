package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.TupleImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import it.unibo.tuprolog.core.testutils.TupleUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

/**
 * Test class for [Tuple] companion object
 *
 * @author Enrico
 */
internal class TupleTest {
    private val correctInstances = TupleUtils.tupleInstances(::TupleImpl)

    @Test
    fun wrapIfNeededWithNoArgs() {
        assertSame(Truth.TRUE, Tuple.wrapIfNeeded())
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

    @Test
    fun tupleOfLeftRight() {
        val toBeTested = TupleUtils.tupleInstances { left, right -> Tuple.of(left, right) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun tupleOfLeftRightOthers() {
        val first = Var.of("A")
        val second = Atom.of("b")
        val third = Truth.FALSE
        val fourth = Truth.TRUE
        val correctInstance = TupleImpl(first, TupleImpl(second, TupleImpl(third, fourth)))

        val others = Tuple.of(third, fourth)
        val toBeTested = Tuple.of(first, second, others)

        assertEqualities(correctInstance, toBeTested)
    }

    @Test
    fun tupleOfList() {
        val toBeTested = TupleUtils.tupleInstancesElementLists.map { Tuple.of(it.toList()) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun tupleOfListShouldComplainWithLowerThanTwoElements() {
        assertFailsWith<IllegalArgumentException> { Tuple.of(listOf()) }
        assertFailsWith<IllegalArgumentException> { Tuple.of(listOf(Atom.of("a"))) }
    }

    @Test
    fun tupleOfIterable() {
        val toBeTested = TupleUtils.tupleInstancesElementLists.map { Tuple.of(it.toList().asIterable()) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun bigTuplesDoNotProvokeStackOverflow() {
        val nums = (0..100_000).toList()
        val tuple = Tuple.of(nums.map { Integer.of(it) })

        assertEquals(nums.joinToString(", ", "(", ")"), tuple.toString())

        val otherTuple = tuple.freshCopy()

        assertEquals(tuple.hashCode(), otherTuple.hashCode())
        assertEquals(tuple, otherTuple)
    }
}
