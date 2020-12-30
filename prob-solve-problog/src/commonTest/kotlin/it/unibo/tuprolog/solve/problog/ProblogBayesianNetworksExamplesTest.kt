package it.unibo.tuprolog.solve.problog

import it.unibo.tuprolog.core.parsing.parseAsStruct
import it.unibo.tuprolog.theory.parsing.parseAsTheory
import kotlin.test.Test

class ProblogBayesianNetworksExamplesTest {

    /***
     * https://dtai.cs.kuleuven.be/problog/tutorial/basic/02_bayes.html#probabilistic-facts
     * */
    @Test
    fun testProbabilisticFacts() {
        TestUtils.assertQueryWithSolutions(
            """
                0.7::burglary.
                0.2::earthquake.
                0.9::p_alarm1.
                0.8::p_alarm2.
                0.1::p_alarm3.
                alarm :- burglary, earthquake, p_alarm1.
                alarm :- burglary, \+earthquake, p_alarm2.
                alarm :- \+burglary, earthquake, p_alarm3.
                evidence(alarm,true).
            """.parseAsTheory(ProblogProbSolverFactory.defaultBuiltins.operators),
            listOf(
                QueryWithSolutions(
                    "burglary".parseAsStruct(),
                    listOf(ExpectedSolution("burglary".parseAsStruct(), 0.98965517))
                ),
                QueryWithSolutions(
                    "earthquake".parseAsStruct(),
                    listOf(ExpectedSolution("earthquake".parseAsStruct(), 0.22758621))
                ),
            )
        )
    }

    /***
     * https://dtai.cs.kuleuven.be/problog/tutorial/basic/02_bayes.html#probabilistic-clauses
     * */
    @Test
    fun testProbabilisticClauses() {
        TestUtils.assertQueryWithSolutions(
            """
                0.7::burglary.
                0.2::earthquake.         
                0.9::alarm :- burglary, earthquake.
                0.8::alarm :- burglary, \+earthquake.
                0.1::alarm :- \+burglary, earthquake.        
                evidence(alarm,true).
            """.parseAsTheory(ProblogProbSolverFactory.defaultBuiltins.operators),
            listOf(
                QueryWithSolutions(
                    "burglary".parseAsStruct(),
                    listOf(ExpectedSolution("burglary".parseAsStruct(), 0.98193926))
                ),
                QueryWithSolutions(
                    "earthquake".parseAsStruct(),
                    listOf(ExpectedSolution("earthquake".parseAsStruct(), 0.22685136))
                ),
            )
        )
    }

    /***
     * https://dtai.cs.kuleuven.be/problog/tutorial/basic/02_bayes.html#first-order
     * */
    @Test
    fun testFirstOrder() {
        TestUtils.assertQueryWithSolutions(
            """
                person(john).
                person(mary).
                0.7::burglary.
                0.2::earthquake.
                0.9::alarm :- burglary, earthquake.
                0.8::alarm :- burglary, \+earthquake.
                0.1::alarm :- \+burglary, earthquake.
                0.8::calls(X) :- alarm, person(X).
                0.1::calls(X) :- \+alarm, person(X).
                evidence(calls(john),true).
                evidence(calls(mary),true).
            """.parseAsTheory(ProblogProbSolverFactory.defaultBuiltins.operators),
            listOf(
                QueryWithSolutions(
                    "burglary".parseAsStruct(),
                    listOf(ExpectedSolution("burglary".parseAsStruct(), 0.98965517))
                ),
                QueryWithSolutions(
                    "earthquake".parseAsStruct(),
                    listOf(ExpectedSolution("earthquake".parseAsStruct(), 0.22758621))
                ),
            )
        )
    }

    /***
     * https://dtai.cs.kuleuven.be/problog/tutorial/basic/02_bayes.html#annotated-disjunctions-dealing-with-multi-valued-variables
     * */
    @Test
    fun testAnnotatedDisjunctions() {
        TestUtils.assertQueryWithSolutions(
            """
                person(john).
                person(mary). 
                0.7::burglary.
                0.01::earthquake(heavy); 0.19::earthquake(mild); 0.8::earthquake(none).   
                0.90::alarm :-   burglary, earthquake(heavy).
                0.85::alarm :-   burglary, earthquake(mild).
                0.80::alarm :-   burglary, earthquake(none).
                0.10::alarm :- \+burglary, earthquake(mild).
                0.30::alarm :- \+burglary, earthquake(heavy).            
                0.8::calls(X) :- alarm, person(X).
                0.1::calls(X) :- \+alarm, person(X).       
                evidence(calls(john),true).
                evidence(calls(mary),true).
            """.parseAsTheory(ProblogProbSolverFactory.defaultBuiltins.operators),
            listOf(
                QueryWithSolutions(
                    "burglary".parseAsStruct(),
                    listOf(ExpectedSolution("burglary".parseAsStruct(), 0.98965517))
                ),
                QueryWithSolutions(
                    "earthquake(_)".parseAsStruct(),
                    listOf(
                        ExpectedSolution("earthquake(heavy)".parseAsStruct(), 0.012476167),
                        ExpectedSolution("earthquake(mild)".parseAsStruct(), 0.20644476),
                        ExpectedSolution("earthquake(none)".parseAsStruct(), 0.78107907),
                    )
                ),
            )
        )
    }
}
