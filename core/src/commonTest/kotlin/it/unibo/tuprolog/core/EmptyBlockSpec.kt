package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.impl.EmptyBlockImpl

/**
 * Test class for [EmptyBlock] companion object
 *
 * @author Enrico
 */
class EmptyBlockSpec :
    FunSpec({
        test("empty-block-companion-returns-empty-set-impl") {
            EmptyBlock() shouldBe EmptyBlockImpl()
        }
    })
