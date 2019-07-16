package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Test class for [RetractResult] and subclasses
 *
 * @author Enrico
 */
internal class RetractResultTest {

    private val clause1 = Clause.of(Struct.of("a", Var.anonymous()), Struct.of("b", Var.anonymous()))
    private val clause2 = Clause.of(Struct.of("p", Atom.of("john")))

    private val clauses = listOf(clause1, clause2)

    private val clauseDatabase: ClauseDatabase = ClauseDatabase.of(clauses)

    private val toTestSuccess = RetractResult.Success(clauseDatabase, clauses)
    private val toTestFailure = RetractResult.Failure(clauseDatabase)

    @Test
    fun successClauseDatabaseCorrect() {
        assertEquals(clauseDatabase, toTestSuccess.clauseDatabase)
    }

    @Test
    fun successClausesListCorrect() {
        assertEquals(clauses, toTestSuccess.clauses)
    }

    @Test
    fun successFirstClauseCorrect() {
        assertEquals(clauses.first(), toTestSuccess.firstClause)
    }

    @Test
    fun successFirstClauseWithEmptyClauseListThrowsException() {
        assertFailsWith<NoSuchElementException> { RetractResult.Success(clauseDatabase, emptyList()).firstClause }
    }

    @Test
    fun failClauseDatabaseCorrect() {
        assertEquals(clauseDatabase, toTestFailure.clauseDatabase)
    }
}
