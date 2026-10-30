package it.unibo.tuprolog.solve.problog.probabilistic

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.parsing.parseAsStruct
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import it.unibo.tuprolog.solve.problog.probabilistic.TestUtils.queriesOn
import it.unibo.tuprolog.theory.parsing.parseAsTheory

class ProblogTossingCoinsExamplesSpec :
    FunSpec({
        // https://dtai.cs.kuleuven.be/problog/tutorial/basic/01_coins.html#basic-problog-probabilistic-facts-and-clauses
        queriesOn(
            "basic",
            {
                """
                    0.5::heads1.
                    0.6::heads2.
                    twoHeads :- heads1, heads2.
                """.parseAsTheory(ProblogSolverFactory.defaultBuiltins.operators)
            },
            listOf(
                QueryCase(
                    "heads1",
                    "heads1".parseAsStruct(),
                    listOf(ExpectedSolution("heads1".parseAsStruct(), 0.5)),
                ),
                QueryCase(
                    "heads2",
                    "heads2".parseAsStruct(),
                    listOf(ExpectedSolution("heads2".parseAsStruct(), 0.6)),
                ),
                QueryCase(
                    "two-heads",
                    "twoHeads".parseAsStruct(),
                    listOf(ExpectedSolution("twoHeads".parseAsStruct(), 0.3)),
                ),
            ),
        )

        // https://dtai.cs.kuleuven.be/problog/tutorial/basic/01_coins.html#noisy-or-multiple-rules-for-the-same-head
        queriesOn(
            "noisy-or",
            {
                """
                    0.5::heads1.
                    0.6::heads2.
                    someHeads :- heads1.
                    someHeads :- heads2.
                """.parseAsTheory(ProblogSolverFactory.defaultBuiltins.operators)
            },
            listOf(
                QueryCase(
                    "some-heads",
                    "someHeads".parseAsStruct(),
                    listOf(ExpectedSolution("someHeads".parseAsStruct(), 0.8)),
                ),
            ),
        )

        // https://dtai.cs.kuleuven.be/problog/tutorial/basic/01_coins.html#first-order
        queriesOn(
            "first-order",
            {
                """
                    0.6::lands_heads(_).
                    coin(c1).
                    coin(c2).
                    coin(c3).
                    coin(c4).
                    heads(C) :- coin(C), lands_heads(C).
                    someHeads :- heads(_).
                """.parseAsTheory(ProblogSolverFactory.defaultBuiltins.operators)
            },
            listOf(
                QueryCase(
                    "some-heads",
                    "someHeads".parseAsStruct(),
                    listOf(ExpectedSolution("someHeads".parseAsStruct(), 0.9744)),
                ),
            ),
        )

        // https://dtai.cs.kuleuven.be/problog/tutorial/basic/01_coins.html#probabilistic-clauses
        queriesOn(
            "probabilistic-clauses",
            {
                """
                    0.6::heads(C) :- coin(C).
                    coin(c1).
                    coin(c2).
                    coin(c3).
                    coin(c4).
                    someHeads :- heads(_).
                """.parseAsTheory(ProblogSolverFactory.defaultBuiltins.operators)
            },
            listOf(
                QueryCase(
                    "some-heads",
                    "someHeads".parseAsStruct(),
                    listOf(ExpectedSolution("someHeads".parseAsStruct(), 0.9744)),
                ),
            ),
        )
    })
