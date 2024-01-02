package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.ConsUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import it.unibo.tuprolog.core.List as LogicList

/**
 * Test class for [List] companion object
 *
 * @author Enrico
 */
internal class ListTest {
    private val emptyTerminatedInstances = ConsUtils.onlyConsEmptyListTerminated(Cons.Companion::of)
    private val pipedListInstances = ConsUtils.onlyConsPipeTerminated(Cons.Companion::of)

    private val emptyTerminatedElementLists = ConsUtils.onlyConsEmptyListTerminatedElementLists
    private val pipeTerminatedElementLists = ConsUtils.onlyConsPipeTerminatedElementLists

    @Test
    fun emptyReturnsEmptyList() {
        assertEqualities(Empty.list(), LogicList.empty())
        assertEquals(Empty.list(), LogicList.empty())
    }

    @Test
    fun ofNoVarargTerms() {
        assertEqualities(Empty.list(), LogicList.of())
        assertEquals(Empty.list(), LogicList.of())
    }

    @Test
    fun ofVarargTerms() {
        val toBeTested = emptyTerminatedElementLists.map { LogicList.of(*it.toTypedArray()) }

        onCorrespondingItems(emptyTerminatedInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun ofEmptyIterable() {
        val toBeTested = LogicList.of(emptyList<Term>().asIterable())

        assertEqualities(Empty.list(), toBeTested)
        assertEquals(Empty.list(), toBeTested)
    }

    @Test
    fun ofIterableOfTerms() {
        val toBeTested = emptyTerminatedElementLists.map { LogicList.of(it.asIterable()) }
        onCorrespondingItems(emptyTerminatedInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun fromEmptyIterableWithoutLastSpecifiedBehavesLikeOfMethod() {
        val toBeTested = LogicList.from(emptyList<Term>().asIterable())

        assertEqualities(Empty.list(), toBeTested)
        assertEquals(Empty.list(), toBeTested)
    }

    @Test
    fun fromEmptyIterableSpecifyingLastAsEmptyList() {
        val toBeTested = LogicList.from(emptyList<Term>().asIterable(), Empty.list())

        assertEqualities(Empty.list(), toBeTested)
        assertEquals(Empty.list(), toBeTested)
    }

    @Test
    fun fromEmptyIterableSpecifyingLast() {
        assertFailsWith<IllegalArgumentException> { LogicList.from(emptyList<Term>().asIterable(), Truth.FALSE) }
    }

    @Test
    fun fromIterableWithoutLastSpecifiedDoesNotBehaveLikeOfMethod() {
        val toBeTested = pipeTerminatedElementLists.map { LogicList.from(it.asIterable()) }

        onCorrespondingItems(pipedListInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun fromIterableSpecifyingLast() {
        val toBeTested = pipeTerminatedElementLists.map { LogicList.from(it) }

        onCorrespondingItems(pipedListInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun fromEmptySequenceWithoutLastSpecifiedBehavesLikeOfMethod() {
        val toBeTested = LogicList.from(emptyList<Term>().asSequence())

        assertEqualities(Empty.list(), toBeTested)
        assertEquals(Empty.list(), toBeTested)
    }

    @Test
    fun fromEmptySequenceSpecifyingLastAsEmptyList() {
        val toBeTested = LogicList.from(emptyList<Term>().asSequence(), Empty.list())

        assertEqualities(Empty.list(), toBeTested)
        assertEquals(Empty.list(), toBeTested)
    }

    @Test
    fun fromEmptySequenceSpecifyingLast() {
        assertFailsWith<IllegalArgumentException> { LogicList.from(emptyList<Term>().asSequence(), Truth.FALSE) }
    }

    @Test
    fun fromSequenceWithoutLastSpecifiedDoesNotBehaveLikeOfMethod() {
        val toBeTested = pipeTerminatedElementLists.map { LogicList.from(it.asSequence()) }

        onCorrespondingItems(pipedListInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun fromSequenceSpecifyingLast() {
        val toBeTested = pipeTerminatedElementLists.map { LogicList.from(it) }

        onCorrespondingItems(pipedListInstances, toBeTested, ::assertEqualities)
    }
}
