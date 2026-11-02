package it.unibo.tuprolog.solve.problog.prolog

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.problog.lib.ProblogLib

class DefaultBuiltinsSpec :
    FunSpec({
        test("self-equality") {
            ProblogLib shouldBe ProblogLib
        }

        test("item-equality") {
            Runtime.of(ProblogLib) shouldBe Runtime.of(ProblogLib)
        }
    })
