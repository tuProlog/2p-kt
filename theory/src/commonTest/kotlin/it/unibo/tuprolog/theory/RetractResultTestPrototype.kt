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
internal class RetractResultTestPrototype(
    private val emptyClauseDbGenerator: () -> ClauseDatabase,
    private val clauseDbGenerator: (Iterable<Clause>) -> ClauseDatabase
) {

    private val clause1 = Clause.of(Struct.of("a", Var.anonymous()), Struct.of("b", Var.anonymous()))
    private val clause2 = Clause.of(Struct.of("p", Atom.of("john")))

    private val clauses = listOf(clause1, clause2)

    private val clauseDatabase: ClauseDatabase = clauseDbGenerator(clauses)

    private val toTestSuccess = RetractResult.Success(clauseDatabase, clauses)
    private val toTestFailure = RetractResult.Failure(clauseDatabase)

    fun successClauseDatabaseCorrect() {
        assertEquals(clauseDatabase, toTestSuccess.clauseDatabase)
    }

    fun successClausesListCorrect() {
        assertEquals(clauses, toTestSuccess.clauses)
    }

    fun successFirstClauseCorrect() {
        assertEquals(clauses.first(), toTestSuccess.firstClause)
    }

    fun successFirstClauseWithEmptyClauseListThrowsException() {
        assertFailsWith<NoSuchElementException> {
            RetractResult.Success(clauseDatabase, emptyList()).firstClause
        }
    }

    fun failClauseDatabaseCorrect() {
        assertEquals(clauseDatabase, toTestFailure.clauseDatabase)
    }
}
