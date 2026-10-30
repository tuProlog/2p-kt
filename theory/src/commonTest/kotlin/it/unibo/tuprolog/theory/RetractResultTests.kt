package it.unibo.tuprolog.theory

import io.kotest.core.spec.style.funSpec
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

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
        assertEquals(theory, toTestSuccess.theory)
    }

    test("success-clauses-list-correct") {
        assertEquals(clauses, toTestSuccess.clauses)
    }

    test("success-first-clause-correct") {
        assertEquals(clauses.first(), toTestSuccess.firstClause)
    }

    test("success-first-clause-with-empty-clause-list-throws-exception") {
        assertFailsWith<NoSuchElementException> {
            RetractResult.Success(theory, emptyList()).firstClause
        }
    }

    test("fail-theory-correct") {
        assertEquals(theory, toTestFailure.theory)
    }
}
