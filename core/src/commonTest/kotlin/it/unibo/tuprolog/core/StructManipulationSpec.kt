package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class StructManipulationSpec :
    FunSpec({
        val f1 = Struct.of("f", Integer.ONE)

        test("add-last") {
            f1.addLast(Integer.of(2)) shouldBe Struct.of("f", Integer.ONE, Integer.of(2))
        }

        test("append") {
            f1.append(Integer.of(2)) shouldBe Struct.of("f", Integer.ONE, Integer.of(2))
        }

        test("add-first") {
            f1.addFirst(Integer.of(2)) shouldBe Struct.of("f", Integer.of(2), Integer.ONE)
        }

        test("set-args") {
            f1.setArgs(Integer.of(2), Integer.of(3)) shouldBe Struct.of("f", Integer.of(2), Integer.of(3))
        }

        test("set-functor") {
            f1.setFunctor("g") shouldBe Struct.of("g", Integer.ONE)
        }

        test("insert-at") {
            f1.insertAt(0, Integer.of(2)) shouldBe Struct.of("f", Integer.of(2), Integer.ONE)
        }
    })
