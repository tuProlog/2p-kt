package it.unibo.tuprolog.theory

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var

/**
 * Test class for [RetractResult] and subclasses
 *
 * @author Enrico
 */
fun retractResultTests(
    emptyTheoryGenerator: () -> Theory,
    theoryGenerator: (Iterable<Clause>) -> Theory,
) = funSpec {
    val clause1 = Clause.of(Struct.of("a", Var.anonymous()), Struct.of("b", Var.anonymous()))

    val clause2 = Clause.of(Struct.of("p", Atom.of("john")))

    val clauses = listOf(clause1, clause2)

    val theory: Theory = theoryGenerator(clauses)

    val toTestSuccess = RetractResult.Success(theory, clauses)

    val toTestFailure = RetractResult.Failure(theory)

    test("success-theory-correct") {
        toTestSuccess.theory shouldBe theory
    }

    test("success-clauses-list-correct") {
        toTestSuccess.clauses shouldBe clauses
    }

    test("success-first-clause-correct") {
        toTestSuccess.firstClause shouldBe clauses.first()
    }

    test("success-first-clause-with-empty-clause-list-throws-exception") {
        shouldThrow<NoSuchElementException> {
            RetractResult.Success(theory, emptyList()).firstClause
        }
    }

    test("fail-theory-correct") {
        toTestFailure.theory shouldBe theory
    }
}
