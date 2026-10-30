package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.impl.EmptyBlockImpl
import kotlin.test.assertEquals

/**
 * Test class for [EmptyBlock] companion object
 *
 * @author Enrico
 */
class EmptyBlockSpec :
    FunSpec({
        test("empty-block-companion-returns-empty-set-impl") {
            assertEquals(EmptyBlockImpl(), EmptyBlock())
        }
    })
