package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.impl.EmptyListImpl

/**
 * Test class for [EmptyList] companion object
 *
 * @author Enrico
 */
class EmptyListSpec :
    FunSpec({
        test("empty-list-companion-returns-empty-list-impl") {
            EmptyList() shouldBe EmptyListImpl()
        }
    })
