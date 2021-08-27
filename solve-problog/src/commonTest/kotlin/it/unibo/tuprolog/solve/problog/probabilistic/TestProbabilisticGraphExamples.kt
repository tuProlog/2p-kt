package it.unibo.tuprolog.solve.problog.probabilistic

import it.unibo.tuprolog.core.parsing.parseAsStruct
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import it.unibo.tuprolog.theory.parsing.parseAsTheory
import kotlin.test.Test

class TestProbabilisticGraphExamples {

    /***
     * https://dtai.cs.kuleuven.be/problog/tutorial/basic/04_pgraph.html
     */
    @Test
    fun testProbabilisticGraph() {
        TestUtils.assertQueryWithSolutions(
            """
                0.6::edge(1,2).
                0.1::edge(1,3).
                0.4::edge(2,5).
                0.3::edge(2,6).
                0.3::edge(3,4).
                0.8::edge(4,5).
                0.2::edge(5,6).
                
                path(X,Y) :- edge(X,Y).
                path(X,Y) :- edge(X,Z), Y \== Z, path(Z,Y).
                
                query(path(1,5)).
                query(path(1,6)).
            """.parseAsTheory(ProblogSolverFactory.defaultBuiltins.operators),
            listOf(
                QueryWithSolutions(
                    "path(1,5)".parseAsStruct(),
                    listOf(ExpectedSolution("path(1,5)".parseAsStruct(), 0.25824))
                ),
                QueryWithSolutions(
                    "path(1,6)".parseAsStruct(),
                    listOf(ExpectedSolution("path(1,6)".parseAsStruct(), 0.2167296))
                ),
            )
        )
    }
}
