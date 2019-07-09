package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.ConsImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.ConsUtils
import kotlin.test.Test

/**
 * Test class for [Cons] companion object
 *
 * @author Enrico
 */
internal class ConsTest {

    @Test
    fun consOfWorksAsExpected() {
        val correctInstances = ConsUtils.mixedConsInstances(::ConsImpl)
        val toBeTested = ConsUtils.mixedConsInstances { head, tail -> Cons.of(head, tail) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun consSingletonWorksAsExpected() {
        val onlyElement = Var.anonymous()

        val correctInstance = ConsImpl(onlyElement, Empty.list())
        val toBeTested = Cons.singleton(onlyElement)

        assertEqualities(correctInstance, toBeTested)
    }
} 
