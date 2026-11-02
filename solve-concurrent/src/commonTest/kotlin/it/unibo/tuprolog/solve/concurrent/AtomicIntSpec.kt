package it.unibo.tuprolog.solve.concurrent

import io.kotest.core.spec.style.FunSpec
import kotlin.test.assertEquals

class AtomicIntSpec :
    FunSpec({
        test("starts-at-the-given-value") {
            assertEquals(0, AtomicInt.zero().value)
            assertEquals(5, AtomicInt.of(5).value)
        }

        test("updates-in-place") {
            val counter = AtomicInt.zero()
            counter += 3
            assertEquals(3, counter.value)
            counter -= 1
            assertEquals(2, counter.value)
            assertEquals(3, counter.incAndGet())
            assertEquals(5, counter.incAndGet(2))
            assertEquals(4, counter.decAndGet())
            assertEquals(1, counter.decAndGet(3))
            counter.value = 10
            assertEquals(10, counter.value)
        }
    })
