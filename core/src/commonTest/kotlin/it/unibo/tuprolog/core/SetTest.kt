package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.SetImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.SetUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

/**
 * Test class for [Set] companion object
 *
 * @author Enrico
 */
internal class SetTest {

    private val correctInstances = SetUtils.mixedSetsTupleWrapped.map(::SetImpl)

    @Test
    fun emptyReturnsEmptySet() {
        assertEqualities(Empty.set(), Set.empty())
        assertSame(Empty.set(), Set.empty())
    }

    @Test
    fun setOfNoVarargTerms() {
        assertEqualities(Empty.set(), Set.of())
    }

    @Test
    fun setOfVarargTerms() {
        val toBeTested = SetUtils.mixedSets.map { Set.of(*it) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun setOfEmptyIterableOfTerms() {
        assertEqualities(Empty.set(), Set.of(emptyList<Term>().asIterable()))
    }

    @Test
    fun setOfIterableOfTerms() {
        val toBeTested = SetUtils.mixedSets.map { Set.of(it.toList().asIterable()) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun setOfEmptySequenceOfTerms() {
        assertEqualities(Empty.set(), Set.of(emptySequence()))
    }

    @Test
    fun setOfSequenceOfTerms() {
        val toBeTested = SetUtils.mixedSets.map { Set.of(it.asSequence()) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun bigSetDoNotProvokeStackOverflow() {
        val nums = (0..100_000).toList()
        val set = Set.of(nums.map { Integer.of(it) })

        assertEquals(nums.joinToString(", ", "{", "}"), set.toString())

        val otherSet = set.freshCopy()

        assertEquals(set.hashCode(), otherSet.hashCode())
        assertEquals(set, otherSet)
    }
}
