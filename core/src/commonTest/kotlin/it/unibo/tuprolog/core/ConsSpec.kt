package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.impl.ConsImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.ConsUtils

/**
 * Test class for [Cons] companion object
 *
 * @author Enrico
 */
class ConsSpec :
    FunSpec({
        test("cons-of-works-as-expected") {
            val correctInstances = ConsUtils.mixedConsInstances(::ConsImpl)
            val toBeTested = ConsUtils.mixedConsInstances { head, tail -> Cons.of(head, tail) }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        test("cons-singleton-works-as-expected") {
            val onlyElement = Var.anonymous()

            val correctInstance = ConsImpl(onlyElement, Empty.list())
            val toBeTested = Cons.singleton(onlyElement)

            assertEqualities(correctInstance, toBeTested)
        }
    })
