package it.unibo.tuprolog.theory.impl

import it.unibo.tuprolog.collections.impl.Factories
import it.unibo.tuprolog.theory.PrototypeProperIndexingTest
import kotlin.test.Test

internal class ListedClauseIndexingTest {

    private val prototype = PrototypeProperIndexingTest(
        Factories::listedTheoryOf
    )

    @Test
    fun testCornerCaseInClauseRetrieval() {
        prototype.testCornerCaseInClauseRetrieval()
    }

    @Test
    fun correctIndexingOverDedicatedTheoryForF1Family() {
        prototype.correctIndexingOverDedicatedTheoryForF1Family()
    }

    @Test
    fun correctIndexingOverDedicatedTheoryForF2Family() {
        prototype.correctIndexingOverDedicatedTheoryForF2Family()
    }

    @Test
    fun correctIndexingOverDedicatedTheoryG1Family() {
        prototype.correctIndexingOverDedicatedTheoryG1Family()
    }

    @Test
    fun correctIndexingOverDedicatedTheoryG2Family() {
        prototype.correctIndexingOverDedicatedTheoryG2Family()
    }

    @Test
    fun correctIndexingAfterTheoriesConcatenationForF1Family() {
        prototype.correctIndexingAfterTheoriesConcatenationForF1Family()
    }

    @Test
    fun correctIndexingAfterTheoriesConcatenationForF2Family() {
        prototype.correctIndexingAfterTheoriesConcatenationForF2Family()
    }

    @Test
    fun correctIndexingAfterTheoriesConcatenationForG1Family() {
        prototype.correctIndexingAfterTheoriesConcatenationForG1Family()
    }

    @Test
    fun correctIndexingAfterTheoriesConcatenationForG2Family() {
        prototype.correctIndexingAfterTheoriesConcatenationForG2Family()
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
