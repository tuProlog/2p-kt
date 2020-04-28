package it.unibo.tuprolog.theory

import kotlin.test.BeforeTest
import kotlin.test.Test

class IndexedIssueClauseIndexingTest {

    private val prototype = PrototypeProperIndexingTest(
        ClauseDatabase.Companion::issueOf
    )

    @BeforeTest
    fun init() {}

    @Test
    fun correctIndexingOverDedicatedClauseDatabase() {
        prototype.correctIndexingOverDedicatedClauseDatabase()
    }

    @Test
    fun correctIndexingOnInitializedClauseDatabaseWithoutStructurallyEqualsClauses() {
        prototype.correctIndexingOnInitializedClauseDatabaseWithoutStructurallyEqualsClauses()
    }

    @Test
    fun correctIndexingOnInitializedClauseDatabaseWithStructurallyEqualsClauses() {
        prototype.correctIndexingOnInitializedClauseDatabaseWithStructurallyEqualsClauses()
    }

    @Test
    fun correctIndexingOnInitializedClauseDatabaseWithStructurallyEqualsClausesAndClausesRepetition() {
        prototype.correctIndexingOnInitializedClauseDatabaseWithStructurallyEqualsClausesAndClausesRepetition()
    }

    @Test
    fun correctIndexingAfterClauseDatabasesConcatenation() {
        prototype.correctIndexingAfterClauseDatabasesConcatenation()
    }

    @Test
    fun correctIndexingAfterAssertionA() {
        prototype.correctIndexingAfterAssertionA()
    }

    @Test
    fun correctIndexingAfterAssertionZ() {
        prototype.correctIndexingAfterAssertionZ()
    }

    @Test
    fun correctIndexingAfterSingleClauseRetracting() {
        prototype.correctIndexingAfterSingleClauseRetracting()
    }

    @Test
    fun correctIndexingAfterMultipleClausesRetracting() {
        prototype.correctIndexingAfterMultipleClausesRetracting()
    }

}