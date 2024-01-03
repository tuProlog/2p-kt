package it.unibo.tuprolog.theory

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
internal class PrototypeRetractResultTest(
    private val emptyTheoryGenerator: () -> Theory,
    private val theoryGenerator: (Iterable<Clause>) -> Theory,
) {
    private val clause1 = Clause.of(Struct.of("a", Var.anonymous()), Struct.of("b", Var.anonymous()))
    private val clause2 = Clause.of(Struct.of("p", Atom.of("john")))

    private val clauses = listOf(clause1, clause2)

    private val theory: Theory = theoryGenerator(clauses)

    private val toTestSuccess = RetractResult.Success(theory, clauses)
    private val toTestFailure = RetractResult.Failure(theory)

    fun successTheoryCorrect() {
        assertEquals(theory, toTestSuccess.theory)
    }

    fun successClausesListCorrect() {
        assertEquals(clauses, toTestSuccess.clauses)
    }

    fun successFirstClauseCorrect() {
        assertEquals(clauses.first(), toTestSuccess.firstClause)
    }

    fun successFirstClauseWithEmptyClauseListThrowsException() {
        assertFailsWith<NoSuchElementException> {
            RetractResult.Success(theory, emptyList()).firstClause
        }
    }

    fun failTheoryCorrect() {
        assertEquals(theory, toTestFailure.theory)
    }
}
