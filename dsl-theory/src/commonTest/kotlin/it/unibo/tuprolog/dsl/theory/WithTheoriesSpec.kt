package it.unibo.tuprolog.dsl.theory

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeSameInstanceAs
import it.unibo.tuprolog.unify.Unificator
import it.unibo.tuprolog.dsl.unify.logicProgramming as unifyLogicProgramming

/** [withTheories], upgrading a `:dsl-unify` scope. */
class WithTheoriesSpec :
    FunSpec({
        test("keeps-the-unificator-of-the-scope") {
            val unificator = Unificator.naive()
            unifyLogicProgramming(unificator) { withTheories().unificator } shouldBeSameInstanceAs unificator
        }
    })
