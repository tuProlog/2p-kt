package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.impl.ConsImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.ConsUtils

/**
 * Test class for [Cons] companion object
 *
 * @author Enrico
 */
class ConsSpec :
    FunSpec({
        withTests(nameFn = { "of/${it.id}" }, ConsUtils.mixedConsCases) { case ->
            assertEqualities(case.cons(::ConsImpl), case.cons { head, tail -> Cons.of(head, tail) })
        }

        test("cons-singleton-works-as-expected") {
            val onlyElement = Var.anonymous()

            val correctInstance = ConsImpl(onlyElement, Empty.list())
            val toBeTested = Cons.singleton(onlyElement)

            assertEqualities(correctInstance, toBeTested)
        }
    })
