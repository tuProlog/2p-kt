package it.unibo.tuprolog.solve.directives

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class DirectiveSelectorSpec :
    FunSpec({
        test("priority-is-its-own-variable") {
            DirectiveSelector.Priority.name shouldBe "Priority"
            DirectiveSelector.Priority shouldNotBe DirectiveSelector.Goal
        }
    })
