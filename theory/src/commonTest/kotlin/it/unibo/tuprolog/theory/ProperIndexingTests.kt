package it.unibo.tuprolog.theory

import io.kotest.core.spec.style.funSpec
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

fun properIndexingTests(theoryGenerator: (Iterable<Clause>) -> Theory) =
    funSpec {
        val mixedClausesTheory =
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

        fun <R> withFreshTheory(
            theory: Theory = theoryGenerator(mixedClausesTheory),
            action: FreshTheoryScope.() -> R,
        ): R = FreshTheoryScope(theory).action()

        val expectedIndexingOverF1 =
            listOf(
                Fact.of(Struct.of("f", Atom.of("a"))),
                Fact.of(Struct.of("f", Atom.of("b"))),
                Fact.of(Struct.of("f", Atom.of("c"))),
                Fact.of(Struct.of("f", Atom.of("a"))),
                Fact.of(Struct.of("f", Atom.of("b"))),
            )

        val expectedIndexingOverF2 =
            listOf(
                Fact.of(Struct.of("f", Atom.of("a"), Atom.of("b"))),
                Fact.of(Struct.of("f", Atom.of("b"), Atom.of("a"))),
                Fact.of(Struct.of("f", Atom.of("b"), Var.of("X"))),
                Fact.of(Struct.of("f", Var.of("X"), Atom.of("a"))),
                Fact.of(Struct.of("f", Var.of("X"), Var.of("Y"))),
            )

        val expectedIndexingOverG1 =
            listOf(
                Fact.of(Struct.of("g", Numeric.of(1))),
                Fact.of(Struct.of("g", Numeric.of(2))),
                Fact.of(Struct.of("g", Var.of("X"))),
                Fact.of(Struct.of("g", Var.of("Y"))),
                Fact.of(Struct.of("g", Numeric.of(1))),
            )

        val expectedIndexingOverG2 =
            listOf(
                Fact.of(Struct.of("g", Var.of("X"), Var.of("Y"))),
                Fact.of(Struct.of("g", Var.of("X"), Atom.of("a"))),
                Fact.of(Struct.of("g", Atom.of("a"), Var.of("X"))),
                Fact.of(Struct.of("g", Atom.of("b"), Atom.of("a"))),
            )

        val newF1AtomClause = Fact.of(Struct.of("f", Numeric.of(0)))

        val newF1VarClause = Fact.of(Struct.of("f", Var.of("Z")))

        val newF2AtomClause = Fact.of(Struct.of("f", Numeric.of(0), Numeric.of(0)))

        val newF2VarClause = Fact.of(Struct.of("f", Var.of("Z"), Var.of("Z")))

        val newF2MixedClause = Fact.of(Struct.of("f", Var.of("Z"), Numeric.of(0)))

        val anonymousF1Clause = Fact.of(Struct.of("f", Var.anonymous()))

        val anonymousF2Clause = Fact.of(Struct.of("f", Var.anonymous(), Var.anonymous()))

        val anonymousG1Clause = Fact.of(Struct.of("g", Var.anonymous()))

        val anonymousG2Clause = Fact.of(Struct.of("g", Var.anonymous(), Var.anonymous()))

        test("corner-case-in-clause-retrieval") {
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

        test("correct-indexing-over-dedicated-theory-for-f1-family") {
            withFreshTheory {
                val generatedIndexingOverF1Family =
                    theory.clauses.toList().filter { anonymousF1Clause matches it }

                assertClausesHaveSameLengthAndContent(expectedIndexingOverF1, generatedIndexingOverF1Family)
            }
        }

        test("correct-indexing-over-dedicated-theory-for-f2-family") {
            withFreshTheory {
                val generatedIndexingOverF2Family =
                    theory.clauses.toList().filter { anonymousF2Clause matches it }

                assertClausesHaveSameLengthAndContent(expectedIndexingOverF2, generatedIndexingOverF2Family)
            }
        }

        test("correct-indexing-over-dedicated-theory-g1-family") {
            withFreshTheory {
                val generatedIndexingOverG1Family =
                    theory.clauses.toList().filter { anonymousG1Clause matches it }

                assertClausesHaveSameLengthAndContent(expectedIndexingOverG1, generatedIndexingOverG1Family)
            }
        }

        test("correct-indexing-over-dedicated-theory-g2-family") {
            withFreshTheory {
                val generatedIndexingOverG2Family =
                    theory.clauses.toList().filter { anonymousG2Clause matches it }

                assertClausesHaveSameLengthAndContent(expectedIndexingOverG2, generatedIndexingOverG2Family)
            }
        }

        test("correct-indexing-after-theories-concatenation-for-f1-family") {
            withFreshTheory {
                val doubledDatabase = (theory + theory)
                val generatedIndexingOverDoubledDatabaseForF1Family =
                    doubledDatabase.clauses.toList().filter { anonymousF1Clause matches it }

                assertClausesHaveSameLengthAndContent(
                    expectedIndexingOverF1 + expectedIndexingOverF1,
                    generatedIndexingOverDoubledDatabaseForF1Family,
                )
            }
        }

        test("correct-indexing-after-theories-concatenation-for-f2-family") {
            withFreshTheory {
                val generatedIndexingOverDoubledDatabaseForF2Family =
                    (theory + theory).clauses.toList().filter { anonymousF2Clause matches it }

                assertClausesHaveSameLengthAndContent(
                    expectedIndexingOverF2 + expectedIndexingOverF2,
                    generatedIndexingOverDoubledDatabaseForF2Family,
                )
            }
        }

        test("correct-indexing-after-theories-concatenation-for-g1-family") {
            withFreshTheory {
                val generatedIndexingOverDoubledDatabaseForG1Family =
                    (theory + theory).clauses.toList().filter { anonymousG1Clause matches it }

                assertClausesHaveSameLengthAndContent(
                    expectedIndexingOverG1 + expectedIndexingOverG1,
                    generatedIndexingOverDoubledDatabaseForG1Family,
                )
            }
        }

        test("correct-indexing-after-theories-concatenation-for-g2-family") {
            withFreshTheory {
                val generatedIndexingOverDoubledDatabaseForG2Family =
                    (theory + theory).clauses.toList().filter { anonymousG2Clause matches it }

                assertClausesHaveSameLengthAndContent(
                    expectedIndexingOverG2 + expectedIndexingOverG2,
                    generatedIndexingOverDoubledDatabaseForG2Family,
                )
            }
        }

        test("correct-indexing-after-one-arity-atom-clause-assertion-a") {
            withFreshTheory {
                val generatedIndexingAfterAssertionA =
                    theory
                        .assertA(newF1AtomClause)
                        .clauses
                        .toList()
                        .filter { anonymousF1Clause matches it }

                assertTermsAreEqual(newF1AtomClause, generatedIndexingAfterAssertionA.first())
                assertClausesHaveSameLengthAndContent(
                    listOf(newF1AtomClause) + expectedIndexingOverF1,
                    generatedIndexingAfterAssertionA,
                )
            }
        }

        test("correct-indexing-after-one-arity-variable-clause-assertion-a") {
            withFreshTheory {
                val generatedIndexingAfterAssertionA =
                    theory
                        .assertA(newF1VarClause)
                        .clauses
                        .toList()
                        .filter { anonymousF1Clause matches it }

                assertTermsAreEqual(newF1VarClause, generatedIndexingAfterAssertionA.first())
                assertClausesHaveSameLengthAndContent(
                    listOf(newF1VarClause) + expectedIndexingOverF1,
                    generatedIndexingAfterAssertionA,
                )
            }
        }

        test("correct-indexing-after-two-arity-atom-clause-assertion-a") {
            withFreshTheory {
                val generatedIndexingAfterAssertionA =
                    theory
                        .assertA(newF2AtomClause)
                        .clauses
                        .toList()
                        .filter { anonymousF2Clause matches it }

                assertTermsAreEqual(newF2AtomClause, generatedIndexingAfterAssertionA.first())
                assertClausesHaveSameLengthAndContent(
                    listOf(newF2AtomClause) + expectedIndexingOverF2,
                    generatedIndexingAfterAssertionA,
                )
            }
        }

        test("correct-indexing-after-two-arity-var-clause-assertion-a") {
            withFreshTheory {
                val generatedIndexingAfterAssertionA =
                    theory
                        .assertA(newF2VarClause)
                        .clauses
                        .toList()
                        .filter { anonymousF2Clause matches it }

                assertTermsAreEqual(newF2VarClause, generatedIndexingAfterAssertionA.first())
                assertClausesHaveSameLengthAndContent(
                    listOf(newF2VarClause) + expectedIndexingOverF2,
                    generatedIndexingAfterAssertionA,
                )
            }
        }

        test("correct-indexing-after-two-arity-mixed-clause-assertion-a") {
            withFreshTheory {
                val generatedIndexingAfterAssertionA =
                    theory
                        .assertA(newF2MixedClause)
                        .clauses
                        .toList()
                        .filter { anonymousF2Clause matches it }

                assertTermsAreEqual(newF2MixedClause, generatedIndexingAfterAssertionA.first())
                assertClausesHaveSameLengthAndContent(
                    listOf(newF2MixedClause) + expectedIndexingOverF2,
                    generatedIndexingAfterAssertionA,
                )
            }
        }

        test("correct-indexing-after-one-arity-atom-clause-assertion-z") {
            withFreshTheory {
                val generatedIndexingAfterAssertionA =
                    theory
                        .assertZ(newF1AtomClause)
                        .clauses
                        .toList()
                        .filter { anonymousF1Clause matches it }

                assertTermsAreEqual(newF1AtomClause, generatedIndexingAfterAssertionA.asReversed().first())
                assertClausesHaveSameLengthAndContent(
                    expectedIndexingOverF1 + listOf(newF1AtomClause),
                    generatedIndexingAfterAssertionA,
                )
            }
        }

        test("correct-indexing-after-one-arity-variable-clause-assertion-z") {
            withFreshTheory {
                val generatedIndexingAfterAssertionA =
                    theory
                        .assertZ(newF1VarClause)
                        .clauses
                        .toList()
                        .filter { anonymousF1Clause matches it }

                assertTermsAreEqual(newF1VarClause, generatedIndexingAfterAssertionA.asReversed().first())
                assertClausesHaveSameLengthAndContent(
                    expectedIndexingOverF1 + listOf(newF1VarClause),
                    generatedIndexingAfterAssertionA,
                )
            }
        }

        test("correct-indexing-after-two-arity-atom-clause-assertion-z") {
            withFreshTheory {
                val generatedIndexingAfterAssertionA =
                    theory
                        .assertZ(newF2AtomClause)
                        .clauses
                        .toList()
                        .filter { anonymousF2Clause matches it }

                assertTermsAreEqual(newF2AtomClause, generatedIndexingAfterAssertionA.asReversed().first())
                assertClausesHaveSameLengthAndContent(
                    expectedIndexingOverF2 + listOf(newF2AtomClause),
                    generatedIndexingAfterAssertionA,
                )
            }
        }

        test("correct-indexing-after-two-arity-variable-clause-assertion-z") {
            withFreshTheory {
                val generatedIndexingAfterAssertionA =
                    theory
                        .assertZ(newF2VarClause)
                        .clauses
                        .toList()
                        .filter { anonymousF2Clause matches it }

                assertTermsAreEqual(newF2VarClause, generatedIndexingAfterAssertionA.asReversed().first())
                assertClausesHaveSameLengthAndContent(
                    expectedIndexingOverF2 + listOf(newF2VarClause),
                    generatedIndexingAfterAssertionA,
                )
            }
        }

        test("correct-indexing-after-two-arity-mixed-clause-assertion-z") {
            withFreshTheory {
                val generatedIndexingAfterAssertionA =
                    theory
                        .assertZ(newF2MixedClause)
                        .clauses
                        .toList()
                        .filter { anonymousF2Clause matches it }

                assertTermsAreEqual(newF2MixedClause, generatedIndexingAfterAssertionA.asReversed().first())
                assertClausesHaveSameLengthAndContent(
                    expectedIndexingOverF2 + listOf(newF2MixedClause),
                    generatedIndexingAfterAssertionA,
                )
            }
        }
    }

private data class FreshTheoryScope(
    val theory: Theory,
)
