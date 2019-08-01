package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.IndicatorImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.IndicatorUtils
import kotlin.test.Test

/**
 * Test class for [Indicator] companion object
 *
 * @author Enrico
 */
internal class IndicatorTest {

    private val correctInstances = IndicatorUtils.mixedIndicators.map { (name, arity) -> IndicatorImpl(name, arity) }

    @Test
    fun ofNameAndArityCreatesCorrectInstance() {
        val toBeTested = IndicatorUtils.mixedIndicators.map { (name, arity) -> Indicator.of(name, arity) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

}
