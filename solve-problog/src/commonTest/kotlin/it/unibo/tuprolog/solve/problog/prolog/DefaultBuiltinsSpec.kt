package it.unibo.tuprolog.solve.problog.prolog

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.problog.lib.ProblogLib
import kotlin.test.assertEquals

class DefaultBuiltinsSpec :
    FunSpec({
        test("self-equality") {
            assertEquals(ProblogLib, ProblogLib)
        }

        test("item-equality") {
            assertEquals(Runtime.of(ProblogLib), Runtime.of(ProblogLib))
        }
    })
