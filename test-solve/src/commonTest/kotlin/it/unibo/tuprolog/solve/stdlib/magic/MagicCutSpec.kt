package it.unibo.tuprolog.solve.stdlib.magic

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldNotBeInstanceOf
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Var

class MagicCutSpec :
    FunSpec({
        test("magic-cut-representation") {
            MagicCut.toString() shouldBe MagicCut.FUNCTOR
        }

        test("magic-cut-cannot-be-created") {
            Atom.of("!").shouldNotBeInstanceOf<MagicCut>()
            Atom.of("¡").shouldNotBeInstanceOf<MagicCut>()
        }

        test("magic-cut-equality-with-ordinary-cut") {
            Atom.of("!") shouldBe MagicCut
            MagicCut shouldBe Atom.of("!")
            MagicCut.hashCode() shouldBe Atom.of("!").hashCode()
        }

        test("coping-magic-cut-preserves-is") {
            MagicCut.freshCopy() shouldBe MagicCut
            MagicCut.freshCopy(Scope.empty()) shouldBe MagicCut
            MagicCut.freshCopy(Scope.of(Var.anonymous())) shouldBe MagicCut
            MagicCut.freshCopy(Scope.of(Var.anonymous(), Var.anonymous())) shouldBe MagicCut
        }
    })
