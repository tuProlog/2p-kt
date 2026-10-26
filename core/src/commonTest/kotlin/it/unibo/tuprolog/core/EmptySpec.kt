package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.impl.EmptyBlockImpl
import it.unibo.tuprolog.core.impl.EmptyListImpl
import kotlin.test.assertEquals

/**
 * Test class for [Empty] and its companion object
 *
 * @author Enrico
 */
class EmptySpec :
    FunSpec({
        test("list-method-should-return-empty-list") {
            assertEquals(EmptyListImpl(), Empty.list())
        }

        test("block-method-should-return-empty-set") {
            assertEquals(EmptyBlockImpl(), Empty.block())
        }
    })
