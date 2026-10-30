package it.unibo.tuprolog.core.exception

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Var
import kotlin.test.assertEquals
import kotlin.test.assertSame

class SubstitutionExceptionSpec :
    FunSpec({
        test("message/for-a-failure") {
            assertEquals(
                "A unifier was expected, while a failed substitution was provided instead",
                SubstitutionException().message,
            )
        }

        test("message/for-a-unifier") {
            val unifier = Substitution.of(Var.of("X"), Atom.of("a"))
            assertEquals(
                "A unifier was expected, while $unifier was provided instead",
                SubstitutionException(unifier).message,
            )
        }

        test("application/keeps-the-term-and-the-cause") {
            val cause = IllegalStateException()
            val exception = SubstitutionApplicationException(Atom.of("a"), cause)
            assertEquals(Atom.of("a"), exception.term)
            assertSame(cause, exception.cause)
            assertEquals("Could not apply ${Substitution.failed()} to a", exception.message)
        }
    })
