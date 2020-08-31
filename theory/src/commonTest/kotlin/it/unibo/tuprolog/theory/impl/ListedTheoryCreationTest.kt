package it.unibo.tuprolog.theory.impl

import it.unibo.tuprolog.theory.PrototypeTheoryCreationTest
import it.unibo.tuprolog.theory.Theory
import kotlin.test.Test

internal class ListedTheoryCreationTest {

    private val prototype: PrototypeTheoryCreationTest =
        PrototypeTheoryCreationTest(
            Theory.Companion::emptyListed,
            { Theory.listedOf(*it) },
            Theory.Companion::listedOf,
            Theory.Companion::listedOf,
            Theory.Companion::listedOf
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
