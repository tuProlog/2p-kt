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
        val toBeTested = CoupleUtils.coupleInstancesUnfoldedLists.map { LogicList.from(it) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }
}
