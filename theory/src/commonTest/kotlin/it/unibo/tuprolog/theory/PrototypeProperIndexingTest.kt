package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.testutils.ClauseAssertionUtils.assertClausesHaveSameLengthAndContent
import it.unibo.tuprolog.testutils.ClauseAssertionUtils.assertTermsAreEqual
import it.unibo.tuprolog.unify.Unificator.Companion.matches


class PrototypeProperIndexingTest(
    private val clauseDatabaseGenerator: (Iterable<Clause>) -> ClauseDatabase
) {

    private val mixedClausesTheory =
        listOf(
            Fact.of(Struct.of("f", Atom.of("a"))),
            Fact.of(Struct.of("f", Atom.of("b"))),
            Fact.of(Struct.of("f", Atom.of("c"))),
            Fact.of(Struct.of("f", Atom.of("a"))),
            Fact.of(Struct.of("g", Numeric.of(1))),
            Fact.of(Struct.of("g", Numeric.of(2))),
            Fact.of(Struct.of("f", Atom.of("a"), Atom.of("b"))),
            Fact.of(Struct.of("g", Var.of("X"))),
            Fact.of(Struct.of("g", Var.of("Y"))),
            Fact.of(Struct.of("f", Atom.of("b"))),
            Fact.of(Struct.of("f", Atom.of("b"), Atom.of("a"))),
            Fact.of(Struct.of("f", Atom.of("b"), Var.of("X"))),
            Fact.of(Struct.of("f", Var.of("X"), Atom.of("a"))),
            Fact.of(Struct.of("f", Var.of("X"), Var.of("Y"))),
            Fact.of(Struct.of("g", Var.of("X"), Var.of("Y"))),
            Fact.of(Struct.of("g", Var.of("X"), Atom.of("a"))),
            Fact.of(Struct.of("g", Atom.of("a"), Var.of("X"))),
            Fact.of(Struct.of("g", Atom.of("b"), Atom.of("a"))),
            Fact.of(Struct.of("g", Numeric.of(1)))
        )

    private val clauseDatabase = clauseDatabaseGenerator(mixedClausesTheory)

    private val expectedIndexingOverF1 =
        listOf(
            Fact.of(Struct.of("f", Atom.of("a"))),
            Fact.of(Struct.of("f", Atom.of("b"))),
            Fact.of(Struct.of("f", Atom.of("c"))),
            Fact.of(Struct.of("f", Atom.of("a"))),
            Fact.of(Struct.of("f", Atom.of("b")))
        )

    private val expectedIndexingOverF2 =
        listOf(
            Fact.of(Struct.of("f", Atom.of("a"), Atom.of("b"))),
            Fact.of(Struct.of("f", Atom.of("b"), Atom.of("a"))),
            Fact.of(Struct.of("f", Atom.of("b"), Var.of("X"))),
            Fact.of(Struct.of("f", Var.of("X"), Atom.of("a"))),
            Fact.of(Struct.of("f", Var.of("X"), Var.of("Y")))
        )

    private val expectedIndexingOverG1 =
        listOf(
            Fact.of(Struct.of("g", Numeric.of(1))),
            Fact.of(Struct.of("g", Numeric.of(2))),
            Fact.of(Struct.of("g", Var.of("X"))),
            Fact.of(Struct.of("g", Var.of("Y"))),
            Fact.of(Struct.of("g", Numeric.of(1)))
        )

    private val expectedIndexingOverG2 =
        listOf(
            Fact.of(Struct.of("g", Var.of("X"), Var.of("Y"))),
            Fact.of(Struct.of("g", Var.of("X"), Atom.of("a"))),
            Fact.of(Struct.of("g", Atom.of("a"), Var.of("X"))),
            Fact.of(Struct.of("g", Atom.of("b"), Atom.of("a")))
        )

    private val newF1AtomClause = Fact.of(Struct.of("f", Numeric.of(0)))
    private val newF1VarClause = Fact.of(Struct.of("f", Var.of("Z")))
    private val newF2AtomClause = Fact.of(Struct.of("f", Numeric.of(0), Numeric.of(0)))
    private val newF2VarClause = Fact.of(Struct.of("f", Var.of("Z"), Var.of("Z")))
    private val newF2MixedClause = Fact.of(Struct.of("f", Var.of("Z"), Numeric.of(0)))

    private val knownG1AtomClause = Fact.of(Struct.of("g", Numeric.of(2)))
    private val knownG1VarClause = Fact.of(Struct.of("g", Var.of("X")))
    private val knownG2AtomClause = Fact.of(Struct.of("g", Atom.of("b"), Atom.of("a")))
    private val knownG2VarClause = Fact.of(Struct.of("g", Var.of("X"), Var.of("Y")))
    private val knownG2MixedClause = Fact.of(Struct.of("g", Var.of("X"), Atom.of("a")))

    private val anonymousF1Clause = Fact.of(Struct.of("f", Var.anonymous()))
    private val anonymousF2Clause = Fact.of(Struct.of("f", Var.anonymous(), Var.anonymous()))
    private val anonymousG1Clause = Fact.of(Struct.of("g", Var.anonymous()))
    private val anonymousG2Clause = Fact.of(Struct.of("g", Var.anonymous(), Var.anonymous()))

    fun correctIndexingOverDedicatedClauseDatabaseForF1Family() {
        val generatedIndexingOverF1Family =
            clauseDatabase.clauses.toList().filter { it matches anonymousF1Clause }

        assertClausesHaveSameLengthAndContent(expectedIndexingOverF1, generatedIndexingOverF1Family)
    }

    fun correctIndexingOverDedicatedClauseDatabaseForF2Family() {
        val generatedIndexingOverF2Family =
            clauseDatabase.clauses.toList().filter { it matches anonymousF2Clause }

        assertClausesHaveSameLengthAndContent(expectedIndexingOverF2, generatedIndexingOverF2Family)
    }

    fun correctIndexingOverDedicatedClauseDatabaseG1Family() {
        val generatedIndexingOverG1Family =
            clauseDatabase.clauses.toList().filter { it matches anonymousG1Clause }

        assertClausesHaveSameLengthAndContent(expectedIndexingOverG1, generatedIndexingOverG1Family)
    }

    fun correctIndexingOverDedicatedClauseDatabaseG2Family() {
        val generatedIndexingOverG2Family =
            clauseDatabase.clauses.toList().filter { it matches anonymousG2Clause }

        assertClausesHaveSameLengthAndContent(expectedIndexingOverG2, generatedIndexingOverG2Family)
    }

    fun correctIndexingAfterClauseDatabasesConcatenationForF1Family() {
        val generatedIndexingOverDoubledDatabaseForF1Family =
            (clauseDatabase + clauseDatabase).clauses.toList().filter { it matches anonymousF1Clause }

        assertClausesHaveSameLengthAndContent(expectedIndexingOverF1 + expectedIndexingOverF1, generatedIndexingOverDoubledDatabaseForF1Family)
    }

    fun correctIndexingAfterClauseDatabasesConcatenationForF2Family() {
        val generatedIndexingOverDoubledDatabaseForF2Family =
            (clauseDatabase + clauseDatabase).clauses.toList().filter { it matches anonymousF2Clause }

        assertClausesHaveSameLengthAndContent(expectedIndexingOverF2 + expectedIndexingOverF2, generatedIndexingOverDoubledDatabaseForF2Family)
    }

    fun correctIndexingAfterClauseDatabasesConcatenationForG1Family() {
        val generatedIndexingOverDoubledDatabaseForG1Family =
            (clauseDatabase + clauseDatabase).clauses.toList().filter { it matches anonymousG1Clause }

        assertClausesHaveSameLengthAndContent(expectedIndexingOverG1 + expectedIndexingOverG1, generatedIndexingOverDoubledDatabaseForG1Family)
    }

    fun correctIndexingAfterClauseDatabasesConcatenationForG2Family() {
        val generatedIndexingOverDoubledDatabaseForG2Family =
            (clauseDatabase + clauseDatabase).clauses.toList().filter { it matches anonymousG2Clause }

        assertClausesHaveSameLengthAndContent(expectedIndexingOverG2 + expectedIndexingOverG2, generatedIndexingOverDoubledDatabaseForG2Family)
    }

    fun correctIndexingAfterOneArityAtomClauseAssertionA() {
        val generatedIndexingAfterAssertionA =
            clauseDatabase.assertA(newF1AtomClause)
                .clauses
                .toList()
                .filter { it matches anonymousF1Clause }

        assertTermsAreEqual(newF1AtomClause, generatedIndexingAfterAssertionA.first())
        assertClausesHaveSameLengthAndContent(listOf(newF1AtomClause) + expectedIndexingOverF1, generatedIndexingAfterAssertionA)
    }

    fun correctIndexingAfterOneArityVariableClauseAssertionA() {
        val generatedIndexingAfterAssertionA =
            clauseDatabase.assertA(newF1VarClause)
                .clauses
                .toList()
                .filter { it matches anonymousF1Clause }

        assertTermsAreEqual(newF1VarClause, generatedIndexingAfterAssertionA.first())
        assertClausesHaveSameLengthAndContent(listOf(newF1VarClause) + expectedIndexingOverF1, generatedIndexingAfterAssertionA)
    }

    fun correctIndexingAfterTwoArityAtomClauseAssertionA() {
        val generatedIndexingAfterAssertionA =
            clauseDatabase.assertA(newF2AtomClause)
                .clauses
                .toList()
                .filter { it matches anonymousF2Clause }

        assertTermsAreEqual(newF2AtomClause, generatedIndexingAfterAssertionA.first())
        assertClausesHaveSameLengthAndContent(listOf(newF2AtomClause) + expectedIndexingOverF2, generatedIndexingAfterAssertionA)
    }

    fun correctIndexingAfterTwoArityVarClauseAssertionA() {
        val generatedIndexingAfterAssertionA =
            clauseDatabase.assertA(newF2VarClause)
                .clauses
                .toList()
                .filter { it matches anonymousF2Clause }

        assertTermsAreEqual(newF2VarClause, generatedIndexingAfterAssertionA.first())
        assertClausesHaveSameLengthAndContent(listOf(newF2VarClause) + expectedIndexingOverF2, generatedIndexingAfterAssertionA)
    }

    fun correctIndexingAfterTwoArityMixedClauseAssertionA() {
        val generatedIndexingAfterAssertionA =
            clauseDatabase.assertA(newF2MixedClause)
                .clauses
                .toList()
                .filter { it matches anonymousF2Clause }

        assertTermsAreEqual(newF2MixedClause, generatedIndexingAfterAssertionA.first())
        assertClausesHaveSameLengthAndContent(listOf(newF2MixedClause) + expectedIndexingOverF2, generatedIndexingAfterAssertionA)
    }

    fun correctIndexingAfterOneArityAtomClauseAssertionZ() {
        val generatedIndexingAfterAssertionA =
            clauseDatabase.assertZ(newF1AtomClause)
                .clauses
                .toList()
                .filter { it matches anonymousF1Clause }

        assertTermsAreEqual(newF1AtomClause, generatedIndexingAfterAssertionA.asReversed().first())
        assertClausesHaveSameLengthAndContent(expectedIndexingOverF1 + listOf(newF1AtomClause), generatedIndexingAfterAssertionA)
    }

    fun correctIndexingAfterOneArityVariableClauseAssertionZ() {
        val generatedIndexingAfterAssertionA =
            clauseDatabase.assertZ(newF1VarClause)
                .clauses
                .toList()
                .filter { it matches anonymousF1Clause }

        assertTermsAreEqual(newF1VarClause, generatedIndexingAfterAssertionA.asReversed().first())
        assertClausesHaveSameLengthAndContent(expectedIndexingOverF1 + listOf(newF1VarClause), generatedIndexingAfterAssertionA)
    }

    fun correctIndexingAfterTwoArityAtomClauseAssertionZ() {
        val generatedIndexingAfterAssertionA =
            clauseDatabase.assertZ(newF2AtomClause)
                .clauses
                .toList()
                .filter { it matches anonymousF2Clause }

        assertTermsAreEqual(newF2AtomClause, generatedIndexingAfterAssertionA.asReversed().first())
        assertClausesHaveSameLengthAndContent(expectedIndexingOverF2 + listOf(newF2AtomClause), generatedIndexingAfterAssertionA)
    }

    fun correctIndexingAfterTwoArityVariableClauseAssertionZ() {
        val generatedIndexingAfterAssertionA =
            clauseDatabase.assertZ(newF2VarClause)
                .clauses
                .toList()
                .filter { it matches anonymousF2Clause }

        assertTermsAreEqual(newF2VarClause, generatedIndexingAfterAssertionA.asReversed().first())
        assertClausesHaveSameLengthAndContent(expectedIndexingOverF2 + listOf(newF2VarClause), generatedIndexingAfterAssertionA)
    }

    fun correctIndexingAfterTwoArityMixedClauseAssertionZ() {
        val generatedIndexingAfterAssertionA =
            clauseDatabase.assertZ(newF2MixedClause)
                .clauses
                .toList()
                .filter { it matches anonymousF2Clause }

        assertTermsAreEqual(newF2MixedClause, generatedIndexingAfterAssertionA.asReversed().first())
        assertClausesHaveSameLengthAndContent(expectedIndexingOverF2 + listOf(newF2MixedClause), generatedIndexingAfterAssertionA)
    }


}