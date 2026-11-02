package it.unibo.tuprolog.core.exception

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Var

class SubstitutionExceptionSpec :
    FunSpec({
        test("message/for-a-failure") {
            SubstitutionException().message shouldBe
                "A unifier was expected, while a failed substitution was provided instead"
        }

        test("message/for-a-unifier") {
            val unifier = Substitution.of(Var.of("X"), Atom.of("a"))
            SubstitutionException(unifier).message shouldBe
                "A unifier was expected, while $unifier was provided instead"
        }

        test("application/keeps-the-term-and-the-cause") {
            val cause = IllegalStateException()
            val exception = SubstitutionApplicationException(Atom.of("a"), cause)
            exception.term shouldBe Atom.of("a")
            exception.cause shouldBeSameInstanceAs cause
            exception.message shouldBe "Could not apply ${Substitution.failed()} to a"
        }
    })
