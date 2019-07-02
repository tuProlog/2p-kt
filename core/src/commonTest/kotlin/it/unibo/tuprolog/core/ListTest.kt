package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.CoupleImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.CoupleUtils
import kotlin.test.Test
import kotlin.test.assertSame
import it.unibo.tuprolog.core.List as LogicList

/**
 * Test class for [List] companion object
 *
 * @author Enrico
 */
internal class ListTest {

    private val correctInstances: List<LogicList> = CoupleUtils.coupleInstances(::CoupleImpl)

    @Test
    fun emptyReturnsEmptyList() {
        assertEqualities(Empty.list(), LogicList.empty())
        assertSame(Empty.list(), LogicList.empty())
    }

    @Test
    fun fromListOfTermsWithoutLastSpecified() {
        val toBeTested = CoupleUtils.coupleInstancesElementLists.map { LogicList.from(it) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun fromListOfTermsSpecifyingLast() {
        val toBeTested = CoupleUtils.coupleInstancesElementLists.map { LogicList.from(it.dropLast(1), it.last()) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun fromEmptyListWithoutLastSpecified() {
        val toBeTested = LogicList.from(emptyList())

        assertEqualities(Empty.list(), toBeTested)
    }

    @Test
    fun fromEmptyListSpecifyingLast() {
        val correctInstance = CoupleImpl(Truth.`true`(), Empty.list())

        val toBeTested = LogicList.from(emptyList(), Truth.`true`())

        assertEqualities(correctInstance, toBeTested)
    }

    @Test
    fun fromIterableOfTermsWithoutLastSpecified() {
        val toBeTested = CoupleUtils.coupleInstancesElementLists.map { LogicList.from(it as Iterable<Term>) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun fromIterableOfTermsSpecifyingLast() {
        val toBeTested = CoupleUtils.coupleInstancesElementLists.map { LogicList.from(it.dropLast(1) as Iterable<Term>, it.last()) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun fromEmptyIterableWithoutLastSpecified() {
        val toBeTested = LogicList.from(emptyList<Term>() as Iterable<Term>)

        assertEqualities(Empty.list(), toBeTested)
    }

    @Test
    fun fromEmptyIterableSpecifyingLast() {
        val correctInstance = CoupleImpl(Truth.`true`(), Empty.list())

        val toBeTested = LogicList.from(emptyList<Term>() as Iterable<Term>, Truth.`true`())

        assertEqualities(correctInstance, toBeTested)
    }

    @Test
    fun fromSequenceOfTermsWithoutLastSpecified() {
        val toBeTested = CoupleUtils.coupleInstancesElementLists.map { LogicList.from(it.asSequence()) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun fromSequenceOfTermsSpecifyingLast() {
        val toBeTested = CoupleUtils.coupleInstancesElementLists.map { LogicList.from(it.dropLast(1).asSequence(), it.last()) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun fromEmptySequenceWithoutLastSpecified() {
        val toBeTested = LogicList.from(emptyList<Term>().asSequence())

        assertEqualities(Empty.list(), toBeTested)
    }

    @Test
    fun fromEmptySequenceSpecifyingLast() {
        val correctInstance = CoupleImpl(Truth.`true`(), Empty.list())

        val toBeTested = LogicList.from(emptyList<Term>().asSequence(), Truth.`true`())

        assertEqualities(correctInstance, toBeTested)
    }

    @Test
    fun ofNoVarargTerms() {
        assertEqualities(Empty.list(), LogicList.of())
    }

    @Test
    fun ofVarargTerms() {
        val toBeTested = CoupleUtils.coupleInstancesElementLists.map { LogicList.of(*it.toTypedArray()) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun ofIterableOfTerms() {
        val toBeTested = CoupleUtils.coupleInstancesElementLists.map { LogicList.of(it as Iterable<Term>) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun ofEmptyIterable() {
        val toBeTested = LogicList.of(emptyList<Term>() as Iterable<Term>)

        assertEqualities(Empty.list(), toBeTested)
    }
}
