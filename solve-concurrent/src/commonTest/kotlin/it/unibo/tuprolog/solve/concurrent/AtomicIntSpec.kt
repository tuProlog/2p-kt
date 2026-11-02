package it.unibo.tuprolog.solve.concurrent

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class AtomicIntSpec :
    FunSpec({
        test("starts-at-the-given-value") {
            AtomicInt.zero().value shouldBe 0
            AtomicInt.of(5).value shouldBe 5
        }

        test("updates-in-place") {
            val counter = AtomicInt.zero()
            counter += 3
            counter.value shouldBe 3
            counter -= 1
            counter.value shouldBe 2
            counter.incAndGet() shouldBe 3
            counter.incAndGet(2) shouldBe 5
            counter.decAndGet() shouldBe 4
            counter.decAndGet(3) shouldBe 1
            counter.value = 10
            counter.value shouldBe 10
        }
    })
