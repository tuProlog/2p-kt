package it.unibo.tuprolog.theory.impl

import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.PrototypeTheoryCreationTest
import kotlin.test.Test

internal class MutableListedTheoryCreationTest {

    private val prototype: PrototypeTheoryCreationTest =
        PrototypeTheoryCreationTest(
            MutableTheory.Companion::emptyListed,
            { MutableTheory.listedOf(*it) },
            MutableTheory.Companion::listedOf,
            MutableTheory.Companion::listedOf,
            MutableTheory.Companion::listedOf
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
