package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.testutils.ClauseAssertionUtils.assertClausesHaveSameLengthAndContent
import it.unibo.tuprolog.testutils.ClauseAssertionUtils.assertTermsAreEqual
import it.unibo.tuprolog.unify.Unificator.Companion.matches

class PrototypeProperIndexingTest(
    private val theoryGenerator: (Iterable<Clause>) -> Theory,
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
            Fact.of(Struct.of("g", Numeric.of(1))),
        )

    private data class FreshTheoryScope(
        val theory: Theory,
    )

    private fun <R> withFreshTheory(
        theory: Theory = theoryGenerator(mixedClausesTheory),
        action: FreshTheoryScope.() -> R,
    ): R = FreshTheoryScope(theory).action()

    private val expectedIndexingOverF1 =
        listOf(
            Fact.of(Struct.of("f", Atom.of("a"))),
            Fact.of(Struct.of("f", Atom.of("b"))),
            Fact.of(Struct.of("f", Atom.of("c"))),
            Fact.of(Struct.of("f", Atom.of("a"))),
            Fact.of(Struct.of("f", Atom.of("b"))),
        )

    private val expectedIndexingOverF2 =
        listOf(
            Fact.of(Struct.of("f", Atom.of("a"), Atom.of("b"))),
            Fact.of(Struct.of("f", Atom.of("b"), Atom.of("a"))),
            Fact.of(Struct.of("f", Atom.of("b"), Var.of("X"))),
            Fact.of(Struct.of("f", Var.of("X"), Atom.of("a"))),
            Fact.of(Struct.of("f", Var.of("X"), Var.of("Y"))),
        )

    private val expectedIndexingOverG1 =
        listOf(
            Fact.of(Struct.of("g", Numeric.of(1))),
            Fact.of(Struct.of("g", Numeric.of(2))),
            Fact.of(Struct.of("g", Var.of("X"))),
            Fact.of(Struct.of("g", Var.of("Y"))),
            Fact.of(Struct.of("g", Numeric.of(1))),
        )

    private val expectedIndexingOverG2 =
        listOf(
            Fact.of(Struct.of("g", Var.of("X"), Var.of("Y"))),
            Fact.of(Struct.of("g", Var.of("X"), Atom.of("a"))),
            Fact.of(Struct.of("g", Atom.of("a"), Var.of("X"))),
            Fact.of(Struct.of("g", Atom.of("b"), Atom.of("a"))),
        )

    private val newF1AtomClause = Fact.of(Struct.of("f", Numeric.of(0)))
    private val newF1VarClause = Fact.of(Struct.of("f", Var.of("Z")))
    private val newF2AtomClause = Fact.of(Struct.of("f", Numeric.of(0), Numeric.of(0)))
    private val newF2VarClause = Fact.of(Struct.of("f", Var.of("Z"), Var.of("Z")))
    private val newF2MixedClause = Fact.of(Struct.of("f", Var.of("Z"), Numeric.of(0)))

    private val anonymousF1Clause = Fact.of(Struct.of("f", Var.anonymous()))
    private val anonymousF2Clause = Fact.of(Struct.of("f", Var.anonymous(), Var.anonymous()))
    private val anonymousG1Clause = Fact.of(Struct.of("g", Var.anonymous()))
    private val anonymousG2Clause = Fact.of(Struct.of("g", Var.anonymous(), Var.anonymous()))

    fun testCornerCaseInClauseRetrieval() {
        // f(f(f(1), 2), 3).
        val fact =
            Fact.of(
                Struct.of(
                    "f",
                    Struct.of(
                        "f",
                        Struct.of("f", Integer.of(1)),
                        Integer.of(2),
                    ),
                    Integer.of(3),
                ),
            )
        withFreshTheory(theoryGenerator(listOf(fact))) {
            assertClausesHaveSameLengthAndContent(
                sequenceOf(fact),
                theory[Fact.of(Struct.of("f", Var.anonymous(), Var.anonymous()))],
            )
            assertClausesHaveSameLengthAndContent(
                sequenceOf(fact),
                theory[Fact.of(Struct.of("f", Struct.of("f", Var.anonymous(), Var.anonymous()), Var.anonymous()))],
            )
            assertClausesHaveSameLengthAndContent(
                sequenceOf(fact),
                theory[
                    Fact.of(
                        Struct.of(
                            "f",
                            Struct.of("f", Struct.of("f", Var.anonymous()), Var.anonymous()),
                            Var.anonymous(),
                        ),
                    ),
                ],
            )
        }
    }

    fun correctIndexingOverDedicatedTheoryForF1Family() {
        withFreshTheory {
            val generatedIndexingOverF1Family =
                theory.clauses.toList().filter { it matches anonymousF1Clause }

            assertClausesHaveSameLengthAndContent(expectedIndexingOverF1, generatedIndexingOverF1Family)
        }
    }

    fun correctIndexingOverDedicatedTheoryForF2Family() {
        withFreshTheory {
            val generatedIndexingOverF2Family =
                theory.clauses.toList().filter { it matches anonymousF2Clause }

            assertClausesHaveSameLengthAndContent(expectedIndexingOverF2, generatedIndexingOverF2Family)
        }
    }

    fun correctIndexingOverDedicatedTheoryG1Family() {
        withFreshTheory {
            val generatedIndexingOverG1Family =
                theory.clauses.toList().filter { it matches anonymousG1Clause }

            assertClausesHaveSameLengthAndContent(expectedIndexingOverG1, generatedIndexingOverG1Family)
        }
    }

    fun correctIndexingOverDedicatedTheoryG2Family() {
        withFreshTheory {
            val generatedIndexingOverG2Family =
                theory.clauses.toList().filter { it matches anonymousG2Clause }

            assertClausesHaveSameLengthAndContent(expectedIndexingOverG2, generatedIndexingOverG2Family)
        }
    }

    fun correctIndexingAfterTheoriesConcatenationForF1Family() {
        withFreshTheory {
            val doubledDatabase = (theory + theory)
            val generatedIndexingOverDoubledDatabaseForF1Family =
                doubledDatabase.clauses.toList().filter { it matches anonymousF1Clause }

            assertClausesHaveSameLengthAndContent(
                expectedIndexingOverF1 + expectedIndexingOverF1,
                generatedIndexingOverDoubledDatabaseForF1Family,
            )
        }
    }

    fun correctIndexingAfterTheoriesConcatenationForF2Family() {
        withFreshTheory {
            val generatedIndexingOverDoubledDatabaseForF2Family =
                (theory + theory).clauses.toList().filter { it matches anonymousF2Clause }

            assertClausesHaveSameLengthAndContent(
                expectedIndexingOverF2 + expectedIndexingOverF2,
                generatedIndexingOverDoubledDatabaseForF2Family,
            )
        }
    }

    fun correctIndexingAfterTheoriesConcatenationForG1Family() {
        withFreshTheory {
            val generatedIndexingOverDoubledDatabaseForG1Family =
                (theory + theory).clauses.toList().filter { it matches anonymousG1Clause }

            assertClausesHaveSameLengthAndContent(
                expectedIndexingOverG1 + expectedIndexingOverG1,
                generatedIndexingOverDoubledDatabaseForG1Family,
            )
        }
    }

    fun correctIndexingAfterTheoriesConcatenationForG2Family() {
        withFreshTheory {
            val generatedIndexingOverDoubledDatabaseForG2Family =
                (theory + theory).clauses.toList().filter { it matches anonymousG2Clause }

            assertClausesHaveSameLengthAndContent(
                expectedIndexingOverG2 + expectedIndexingOverG2,
                generatedIndexingOverDoubledDatabaseForG2Family,
            )
        }
    }

    fun correctIndexingAfterOneArityAtomClauseAssertionA() {
        withFreshTheory {
            val generatedIndexingAfterAssertionA =
                theory
                    .assertA(newF1AtomClause)
                    .clauses
                    .toList()
                    .filter { it matches anonymousF1Clause }

            assertTermsAreEqual(newF1AtomClause, generatedIndexingAfterAssertionA.first())
            assertClausesHaveSameLengthAndContent(
                listOf(newF1AtomClause) + expectedIndexingOverF1,
                generatedIndexingAfterAssertionA,
            )
        }
    }

    fun correctIndexingAfterOneArityVariableClauseAssertionA() {
        withFreshTheory {
            val generatedIndexingAfterAssertionA =
                theory
                    .assertA(newF1VarClause)
                    .clauses
                    .toList()
                    .filter { it matches anonymousF1Clause }

            assertTermsAreEqual(newF1VarClause, generatedIndexingAfterAssertionA.first())
            assertClausesHaveSameLengthAndContent(
                listOf(newF1VarClause) + expectedIndexingOverF1,
                generatedIndexingAfterAssertionA,
            )
        }
    }

    fun correctIndexingAfterTwoArityAtomClauseAssertionA() {
        withFreshTheory {
            val generatedIndexingAfterAssertionA =
                theory
                    .assertA(newF2AtomClause)
                    .clauses
                    .toList()
                    .filter { it matches anonymousF2Clause }

            assertTermsAreEqual(newF2AtomClause, generatedIndexingAfterAssertionA.first())
            assertClausesHaveSameLengthAndContent(
                listOf(newF2AtomClause) + expectedIndexingOverF2,
                generatedIndexingAfterAssertionA,
            )
        }
    }

    fun correctIndexingAfterTwoArityVarClauseAssertionA() {
        withFreshTheory {
            val generatedIndexingAfterAssertionA =
                theory
                    .assertA(newF2VarClause)
                    .clauses
                    .toList()
                    .filter { it matches anonymousF2Clause }

            assertTermsAreEqual(newF2VarClause, generatedIndexingAfterAssertionA.first())
            assertClausesHaveSameLengthAndContent(
                listOf(newF2VarClause) + expectedIndexingOverF2,
                generatedIndexingAfterAssertionA,
            )
        }
    }

    fun correctIndexingAfterTwoArityMixedClauseAssertionA() {
        withFreshTheory {
            val generatedIndexingAfterAssertionA =
                theory
                    .assertA(newF2MixedClause)
                    .clauses
                    .toList()
                    .filter { it matches anonymousF2Clause }

            assertTermsAreEqual(newF2MixedClause, generatedIndexingAfterAssertionA.first())
            assertClausesHaveSameLengthAndContent(
                listOf(newF2MixedClause) + expectedIndexingOverF2,
                generatedIndexingAfterAssertionA,
            )
        }
    }

    fun correctIndexingAfterOneArityAtomClauseAssertionZ() {
        withFreshTheory {
            val generatedIndexingAfterAssertionA =
                theory
                    .assertZ(newF1AtomClause)
                    .clauses
                    .toList()
                    .filter { it matches anonymousF1Clause }

            assertTermsAreEqual(newF1AtomClause, generatedIndexingAfterAssertionA.asReversed().first())
            assertClausesHaveSameLengthAndContent(
                expectedIndexingOverF1 + listOf(newF1AtomClause),
                generatedIndexingAfterAssertionA,
            )
        }
    }

    fun correctIndexingAfterOneArityVariableClauseAssertionZ() {
        withFreshTheory {
            val generatedIndexingAfterAssertionA =
                theory
                    .assertZ(newF1VarClause)
                    .clauses
                    .toList()
                    .filter { it matches anonymousF1Clause }

            assertTermsAreEqual(newF1VarClause, generatedIndexingAfterAssertionA.asReversed().first())
            assertClausesHaveSameLengthAndContent(
                expectedIndexingOverF1 + listOf(newF1VarClause),
                generatedIndexingAfterAssertionA,
            )
        }
    }

    fun correctIndexingAfterTwoArityAtomClauseAssertionZ() {
        withFreshTheory {
            val generatedIndexingAfterAssertionA =
                theory
                    .assertZ(newF2AtomClause)
                    .clauses
                    .toList()
                    .filter { it matches anonymousF2Clause }

            assertTermsAreEqual(newF2AtomClause, generatedIndexingAfterAssertionA.asReversed().first())
            assertClausesHaveSameLengthAndContent(
                expectedIndexingOverF2 + listOf(newF2AtomClause),
                generatedIndexingAfterAssertionA,
            )
        }
    }

    fun correctIndexingAfterTwoArityVariableClauseAssertionZ() {
        withFreshTheory {
            val generatedIndexingAfterAssertionA =
                theory
                    .assertZ(newF2VarClause)
                    .clauses
                    .toList()
                    .filter { it matches anonymousF2Clause }

            assertTermsAreEqual(newF2VarClause, generatedIndexingAfterAssertionA.asReversed().first())
            assertClausesHaveSameLengthAndContent(
                expectedIndexingOverF2 + listOf(newF2VarClause),
                generatedIndexingAfterAssertionA,
            )
        }
    }

    fun correctIndexingAfterTwoArityMixedClauseAssertionZ() {
        withFreshTheory {
            val generatedIndexingAfterAssertionA =
                theory
                    .assertZ(newF2MixedClause)
                    .clauses
                    .toList()
                    .filter { it matches anonymousF2Clause }

            assertTermsAreEqual(newF2MixedClause, generatedIndexingAfterAssertionA.asReversed().first())
            assertClausesHaveSameLengthAndContent(
                expectedIndexingOverF2 + listOf(newF2MixedClause),
                generatedIndexingAfterAssertionA,
            )
        }
    }
}
