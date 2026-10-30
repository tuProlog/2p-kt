package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import kotlin.test.assertEquals

class StructManipulationSpec :
    FunSpec({
        val f1 = Struct.of("f", Integer.ONE)

        test("add-last") {
            assertEquals(
                Struct.of("f", Integer.ONE, Integer.of(2)),
                f1.addLast(Integer.of(2)),
            )
        }

        test("append") {
            assertEquals(
                Struct.of("f", Integer.ONE, Integer.of(2)),
                f1.append(Integer.of(2)),
            )
        }

        test("add-first") {
            assertEquals(
                Struct.of("f", Integer.of(2), Integer.ONE),
                f1.addFirst(Integer.of(2)),
            )
        }

        test("set-args") {
            assertEquals(
                Struct.of("f", Integer.of(2), Integer.of(3)),
                f1.setArgs(Integer.of(2), Integer.of(3)),
            )
        }

        test("set-functor") {
            assertEquals(
                Struct.of("g", Integer.ONE),
                f1.setFunctor("g"),
            )
        }

        test("insert-at") {
            assertEquals(
                Struct.of("f", Integer.of(2), Integer.ONE),
                f1.insertAt(0, Integer.of(2)),
            )
        }
    })
