package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.impl.EmptyBlockImpl
import it.unibo.tuprolog.core.impl.EmptyListImpl

/**
 * Test class for [Empty] and its companion object
 *
 * @author Enrico
 */
class EmptySpec :
    FunSpec({
        test("list-method-should-return-empty-list") {
            Empty.list() shouldBe EmptyListImpl()
        }

        test("block-method-should-return-empty-set") {
            Empty.block() shouldBe EmptyBlockImpl()
        }
    })
