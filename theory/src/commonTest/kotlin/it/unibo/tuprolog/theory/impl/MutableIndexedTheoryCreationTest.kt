package it.unibo.tuprolog.theory.impl

import it.unibo.tuprolog.collections.impl.Factories
import it.unibo.tuprolog.theory.PrototypeTheoryCreationTest
import kotlin.test.Test

internal class MutableIndexedTheoryCreationTest {

    private val prototype: PrototypeTheoryCreationTest =
        PrototypeTheoryCreationTest(
            Factories::emptyMutableIndexedTheory,
            Factories::mutableIndexedTheoryOf,
            Factories::mutableIndexedTheoryOf,
            Factories::mutableIndexedTheoryOf,
            Factories::mutableIndexedTheoryOf
        )

    @Test
    fun emptyCreatesEmptyTheory() {
        prototype.emptyCreatesEmptyTheory()
    }

    @Test
    fun ofVarargClauseCreatesCorrectInstance() {
        prototype.ofVarargClauseCreatesCorrectInstance()
    }

    @Test
    fun ofVarargScopeToClauseCreatesCorrectInstance() {
        prototype.ofVarargScopeToClauseCreatesCorrectInstance()
    }

    @Test
    fun ofIterableClauseCreatesCorrectInstance() {
        prototype.ofIterableClauseCreatesCorrectInstance()
    }

    @Test
    fun ofSequenceClauseCreatesCorrectInstance() {
        prototype.ofSequenceClauseCreatesCorrectInstance()
    }
}
