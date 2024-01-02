package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.FactImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.FactUtils
import kotlin.test.Test

/**
 * Test class for [Fact] companion object
 *
 * @author Enrico
 */
internal class FactTest {
    private val correctInstances = FactUtils.mixedFacts.map(::FactImpl)

    @Test
    fun factOfCreatesCorrectInstances() {
        onCorrespondingItems(correctInstances, FactUtils.mixedFacts.map { Fact.of(it) }, ::assertEqualities)
    }
}
