package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.impl.FactImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.FactUtils

/**
 * Test class for [Fact] companion object
 *
 * @author Enrico
 */
class FactSpec :
    FunSpec({
        withTests(nameFn = { "of/${it.id}" }, FactUtils.mixedFactCases) { case ->
            assertEqualities(FactImpl(case.head), Fact.of(case.head))
        }
    })
