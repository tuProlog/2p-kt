package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.impl.EmptyListImpl
import kotlin.test.assertEquals

/**
 * Test class for [EmptyList] companion object
 *
 * @author Enrico
 */
class EmptyListSpec :
    FunSpec({
        test("empty-list-companion-returns-empty-list-impl") {
            assertEquals(EmptyListImpl(), EmptyList())
        }
    })
