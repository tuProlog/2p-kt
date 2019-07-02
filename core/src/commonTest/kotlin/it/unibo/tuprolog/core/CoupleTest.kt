package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.CoupleImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.CoupleUtils
import kotlin.test.Test

/**
 * Test class for [Couple] companion object
 *
 * @author Enrico
 */
internal class CoupleTest {

    @Test
    fun coupleOfWorksAsExpected() {
        val correctInstances = CoupleUtils.coupleInstances(::CoupleImpl)
        val toBeTested = CoupleUtils.coupleInstances { head, tail -> Couple.of(head, tail) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun coupleLastWorksAsExpected() {
        val onlyElement = Var.anonymous()

        val correctInstance = CoupleImpl(onlyElement, Empty.list())
        val toBeTested = Couple.last(onlyElement)

        assertEqualities(correctInstance, toBeTested)
    }
} 
