package it.unibo.tuprolog.solve.problog.probabilistic

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.parsing.parseAsStruct
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import it.unibo.tuprolog.theory.parsing.parseAsTheory

class ProblogTossingCoinsExamplesSpec :
    FunSpec({
        // https://dtai.cs.kuleuven.be/problog/tutorial/basic/01_coins.html#basic-problog-probabilistic-facts-and-clauses
        test("basic") {
            TestUtils.assertQueryWithSolutions(
                """
                    0.5::heads1.
                    0.6::heads2.
                    twoHeads :- heads1, heads2.
                """.parseAsTheory(ProblogSolverFactory.defaultBuiltins.operators),
                listOf(
                    QueryWithSolutions(
                        "heads1".parseAsStruct(),
                        listOf(ExpectedSolution("heads1".parseAsStruct(), 0.5)),
                    ),
                    QueryWithSolutions(
                        "heads2".parseAsStruct(),
                        listOf(ExpectedSolution("heads2".parseAsStruct(), 0.6)),
                    ),
                    QueryWithSolutions(
                        "twoHeads".parseAsStruct(),
                        listOf(ExpectedSolution("twoHeads".parseAsStruct(), 0.3)),
                    ),
                ),
            )
        }

        // https://dtai.cs.kuleuven.be/problog/tutorial/basic/01_coins.html#noisy-or-multiple-rules-for-the-same-head
        test("noisy-or") {
            TestUtils.assertQueryWithSolutions(
                """
                    0.5::heads1.
                    0.6::heads2.
                    someHeads :- heads1.
                    someHeads :- heads2.
                """.parseAsTheory(ProblogSolverFactory.defaultBuiltins.operators),
                QueryWithSolutions(
                    "someHeads".parseAsStruct(),
                    listOf(ExpectedSolution("someHeads".parseAsStruct(), 0.8)),
                ),
            )
        }

        // https://dtai.cs.kuleuven.be/problog/tutorial/basic/01_coins.html#first-order
        test("first-order") {
            TestUtils.assertQueryWithSolutions(
                """
                    0.6::lands_heads(_).
                    coin(c1).
                    coin(c2).
                    coin(c3).
                    coin(c4).
                    heads(C) :- coin(C), lands_heads(C).
                    someHeads :- heads(_).
                """.parseAsTheory(ProblogSolverFactory.defaultBuiltins.operators),
                QueryWithSolutions(
                    "someHeads".parseAsStruct(),
                    listOf(ExpectedSolution("someHeads".parseAsStruct(), 0.9744)),
                ),
            )
        }

        // https://dtai.cs.kuleuven.be/problog/tutorial/basic/01_coins.html#probabilistic-clauses
        test("probabilistic-clauses") {
            TestUtils.assertQueryWithSolutions(
                """
                    0.6::heads(C) :- coin(C).
                    coin(c1).
                    coin(c2).
                    coin(c3).
                    coin(c4).
                    someHeads :- heads(_).
                """.parseAsTheory(ProblogSolverFactory.defaultBuiltins.operators),
                QueryWithSolutions(
                    "someHeads".parseAsStruct(),
                    listOf(ExpectedSolution("someHeads".parseAsStruct(), 0.9744)),
                ),
            )
        }
    })
