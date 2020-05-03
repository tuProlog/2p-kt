package it.unibo.tuprolog.theory.impl

import it.unibo.tuprolog.theory.ClauseDatabase
import it.unibo.tuprolog.theory.PrototypeProperIndexingTest
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class IndexedClauseIndexingTest {

    private val prototype = PrototypeProperIndexingTest(
        ClauseDatabase.Companion::indexedOf
    )

    @BeforeTest
    fun init() {}

    @Test
    fun correctIndexingOverDedicatedClauseDatabaseForF1Family() {
        prototype.correctIndexingOverDedicatedClauseDatabaseForF1Family()
    }

    @Test
    fun correctIndexingOverDedicatedClauseDatabaseForF2Family() {
        prototype.correctIndexingOverDedicatedClauseDatabaseForF2Family()
    }

    @Test
    fun correctIndexingOverDedicatedClauseDatabaseG1Family() {
        prototype.correctIndexingOverDedicatedClauseDatabaseG1Family()
    }

    @Test
    fun correctIndexingOverDedicatedClauseDatabaseG2Family() {
        prototype.correctIndexingOverDedicatedClauseDatabaseG2Family()
    }

    @Test
    fun correctIndexingAfterClauseDatabasesConcatenationForF1Family() {
        prototype.correctIndexingAfterClauseDatabasesConcatenationForF1Family()
    }

    @Test
    fun correctIndexingAfterClauseDatabasesConcatenationForF2Family() {
        prototype.correctIndexingAfterClauseDatabasesConcatenationForF2Family()
    }

    @Test
    fun correctIndexingAfterClauseDatabasesConcatenationForG1Family() {
        prototype.correctIndexingAfterClauseDatabasesConcatenationForG1Family()
    }

    @Test
    fun correctIndexingAfterClauseDatabasesConcatenationForG2Family() {
        prototype.correctIndexingAfterClauseDatabasesConcatenationForG2Family()
    }

    @Test
    fun correctIndexingAfterOneArityAtomClauseAssertionA() {
        prototype.correctIndexingAfterOneArityAtomClauseAssertionA()
    }

    @Test
    fun correctIndexingAfterOneArityVariableClauseAssertionA() {
        prototype.correctIndexingAfterOneArityVariableClauseAssertionA()
    }

    @Test
    fun correctIndexingAfterTwoArityAtomClauseAssertionA() {
        prototype.correctIndexingAfterTwoArityAtomClauseAssertionA()
    }

    @Test
    fun correctIndexingAfterTwoArityVarClauseAssertionA() {
        prototype.correctIndexingAfterTwoArityVarClauseAssertionA()
    }

    @Test
    fun correctIndexingAfterTwoArityMixedClauseAssertionA() {
        prototype.correctIndexingAfterTwoArityMixedClauseAssertionA()
    }

    @Test
    fun correctIndexingAfterOneArityAtomClauseAssertionZ() {
        prototype.correctIndexingAfterOneArityAtomClauseAssertionZ()
    }

    @Test
    fun correctIndexingAfterOneArityVariableClauseAssertionZ() {
        prototype.correctIndexingAfterOneArityVariableClauseAssertionZ()
    }

    @Test
    fun correctIndexingAfterTwoArityAtomClauseAssertionZ() {
        prototype.correctIndexingAfterTwoArityAtomClauseAssertionZ()
    }

    @Test
    fun correctIndexingAfterTwoArityVariableClauseAssertionZ() {
        prototype.correctIndexingAfterTwoArityVariableClauseAssertionZ()
    }

    @Test
    fun correctIndexingAfterTwoArityMixedClauseAssertionZ() {
        prototype.correctIndexingAfterTwoArityMixedClauseAssertionZ()
    }

}