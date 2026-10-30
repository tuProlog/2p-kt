package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.impl.FactImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.FactUtils

/**
 * Test class for [Fact] companion object
 *
 * @author Enrico
 */
class FactSpec :
    FunSpec({
        val correctInstances = FactUtils.mixedFacts.map(::FactImpl)

        test("fact-of-creates-correct-instances") {
            onCorrespondingItems(correctInstances, FactUtils.mixedFacts.map { Fact.of(it) }, ::assertEqualities)
        }
    })
