package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.testutils.TheoryUtils
import it.unibo.tuprolog.theory.impl.IndexedTheory
import it.unibo.tuprolog.unify.Unificator
import kotlin.test.assertEquals

/**
 * Test class for [Theory.Companion]
 *
 * @author Enrico
 */
internal class PrototypeTheoryCreationTest(
    private val emptyTheoryConstructor: () -> Theory,
    private val theoryConstructorFromArray: (Array<Clause>) -> Theory,
    private val theoryConstructorFromIterable: (Iterable<Clause>) -> Theory,
    private val theoryConstructorFromSequence: (Sequence<Clause>) -> Theory,
    private val theoryConstructorFromScopes: (Array<Scope.() -> Clause>) -> Theory
) {

    private val correctInstance = theoryConstructorFromIterable(TheoryUtils.wellFormedClauses)

    fun emptyCreatesEmptyTheory() {
        val toBeTested = emptyTheoryConstructor()

        assertEquals(IndexedTheory(Unificator.default, emptyList()), toBeTested)
    }

    fun ofVarargClauseCreatesCorrectInstance() {
        val toBeTested = theoryConstructorFromArray(TheoryUtils.wellFormedClauses.toTypedArray())

        assertEquals(correctInstance, toBeTested)
    }

    fun ofVarargScopeToClauseCreatesCorrectInstance() {
        val toBeTested = theoryConstructorFromScopes(
            TheoryUtils.wellFormedClauses
                .map<Clause, Scope.() -> Clause> { { clauseOf(it.head, it.body) } }
                .toTypedArray()
        )

        assertEquals(correctInstance, toBeTested)
    }

    fun ofIterableClauseCreatesCorrectInstance() {
        val toBeTested = theoryConstructorFromIterable(TheoryUtils.wellFormedClauses)

        assertEquals(correctInstance, toBeTested)
    }

    fun ofSequenceClauseCreatesCorrectInstance() {
        val toBeTested = theoryConstructorFromSequence(TheoryUtils.wellFormedClauses.asSequence())

        assertEquals(correctInstance, toBeTested)
    }
}
