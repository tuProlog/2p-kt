package it.unibo.tuprolog.solve.problog

import it.unibo.tuprolog.core.parsing.parseAsStruct
import it.unibo.tuprolog.theory.parsing.parseAsTheory

import kotlin.test.Test

class ProblogRollingDiceExamplesTest {

    /***
     * https://dtai.cs.kuleuven.be/problog/tutorial/basic/03_dice.html#annotated-disjunctions-dealing-with-multi-valued-variables
     * */
    @Test
    fun testAnnotatedDisjunctions() {
        TestUtils.assertQueryWithSolutions(
            """
                1/6::one1; 1/6::two1; 1/6::three1; 1/6::four1; 1/6::five1; 1/6::six1.
                0.15::one2; 0.15::two2; 0.15::three2; 0.15::four2; 0.15::five2; 0.25::six2.
                twoSix :- six1, six2.
                someSix :- six1.
                someSix :- six2.
            """.parseAsTheory(ProblogProbSolverFactory.defaultBuiltins.operators),
            listOf(
                QueryWithSolutions(
                    "six1".parseAsStruct(),
                    listOf(ExpectedSolution("six1".parseAsStruct(), 0.16666667))
                ),
                QueryWithSolutions(
                    "six2".parseAsStruct(),
                    listOf(ExpectedSolution("six2".parseAsStruct(), 0.25))
                ),
                QueryWithSolutions(
                    "someSix".parseAsStruct(),
                    listOf(ExpectedSolution("someSix".parseAsStruct(), 0.375))
                ),
                QueryWithSolutions(
                    "twoSix".parseAsStruct(),
                    listOf(ExpectedSolution("twoSix".parseAsStruct(), 0.041666667))
                ),
            )
        )
    }

    /***
     * https://dtai.cs.kuleuven.be/problog/tutorial/basic/03_dice.html#negation-as-failure
     * */
    @Test
    fun testNegationAsFailure() {
        TestUtils.assertQueryWithSolutions(
            """
                1/6::one(1); 1/6::two(1); 1/6::three(1); 1/6::four(1); 1/6::five(1); 1/6::six(1).
                0.15::one(2); 0.15::two(2); 0.15::three(2); 0.15::four(2); 0.15::five(2); 0.25::six(2).
                odd(X) :- one(X).
                odd(X) :- three(X).
                odd(X) :- five(X).
                even(X) :- \+ odd(X).
            """.parseAsTheory(ProblogProbSolverFactory.defaultBuiltins.operators),
            listOf(
                QueryWithSolutions(
                    "odd(1)".parseAsStruct(),
                    listOf(ExpectedSolution("odd(1)".parseAsStruct(), 0.5))
                ),
                QueryWithSolutions(
                    "even(1)".parseAsStruct(),
                    listOf(ExpectedSolution("even(1)".parseAsStruct(), 0.5))
                ),
                QueryWithSolutions(
                    "odd(2)".parseAsStruct(),
                    listOf(ExpectedSolution("odd(2)".parseAsStruct(), 0.45))
                ),
                QueryWithSolutions(
                    "even(2)".parseAsStruct(),
                    listOf(ExpectedSolution("even(2)".parseAsStruct(), 0.55))
                ),
            )
        )
    }

    /***
    * https://dtai.cs.kuleuven.be/problog/tutorial/basic/03_dice.html#negation-as-failure
    * */
    @Test
    fun testNegationAsFailure2() {
        TestUtils.assertQueryWithSolutions(
            """
                1/6::one(1); 1/6::two(1); 1/6::three(1); 1/6::four(1); 1/6::five(1); 1/6::six(1).
                0.15::one(2); 0.15::two(2); 0.15::three(2); 0.15::four(2); 0.15::five(2); 0.25::six(2).
                odd(X) :- one(X).
                odd(X) :- three(X).
                odd(X) :- five(X).
                even(X) :- \+ odd(X).
            """.parseAsTheory(ProblogProbSolverFactory.defaultBuiltins.operators),
            listOf(
                QueryWithSolutions(
                    "odd(_)".parseAsStruct(),
                    listOf(
                        ExpectedSolution("odd(1)".parseAsStruct(), 0.5),
                        ExpectedSolution("odd(2)".parseAsStruct(), 0.45)
                    )
                ),
                QueryWithSolutions(
                    "even(_)".parseAsStruct(),
                    emptyList()
                ),
            )
        )
    }

    /***
     * https://dtai.cs.kuleuven.be/problog/tutorial/basic/03_dice.html#arithmetic-expressions
     * */
    @Test
    fun testArithmeticExpressions() {
        TestUtils.assertQueryWithSolutions(
            """
                1/6::dice(1,D); 1/6::dice(2,D); 1/6::dice(3,D); 1/6::dice(4,D); 1/6::dice(5,D); 1/6::dice(6,D) :- die(D).
                die(1).
                die(2).     
                sum(S) :- dice(A,1), dice(B,2), S is A+B.
                odd(D) :- dice(1,D).
                odd(D) :- dice(3,D).
                odd(D) :- dice(5,D).
                even(D) :- \+ odd(D).     
                evidence(even(1)).
                evidence(odd(2)).
            """.parseAsTheory(ProblogProbSolverFactory.defaultBuiltins.operators),
            QueryWithSolutions(
                "sum(_)".parseAsStruct(),
                listOf(
                    ExpectedSolution("sum(3)".parseAsStruct(), 0.11111111),
                    ExpectedSolution("sum(5)".parseAsStruct(), 0.22222222),
                    ExpectedSolution("sum(7)".parseAsStruct(), 0.33333333),
                    ExpectedSolution("sum(9)".parseAsStruct(), 0.22222222),
                    ExpectedSolution("sum(11)".parseAsStruct(), 0.11111111),
                )
            )
        )
    }

    /***
     * https://dtai.cs.kuleuven.be/problog/tutorial/basic/03_dice.html#arithmetic-expressions
     * */
    @Test
    fun testArithmeticExpressions2() {
        TestUtils.assertQueryWithSolutions(
            """
                1/6::dice(1,D); 1/6::dice(2,D); 1/6::dice(3,D); 1/6::dice(4,D); 1/6::dice(5,D); 1/6::dice(6,D) :- die(D).
                die(1).
                die(2).
                die(3).
                outcome(A,B,C) :- dice(A,1), dice(B,2), dice(C,3).
                increasing :- outcome(A,B,C), A<B, B<C.
                sum(S) :- outcome(A,B,C), S is A+B+C.
                evidence(increasing).
            """.parseAsTheory(ProblogProbSolverFactory.defaultBuiltins.operators),
            QueryWithSolutions(
                "sum(_)".parseAsStruct(),
                listOf(
                    ExpectedSolution("sum(6)".parseAsStruct(), 0.05),
                    ExpectedSolution("sum(7)".parseAsStruct(), 0.05),
                    ExpectedSolution("sum(8)".parseAsStruct(), 0.1),
                    ExpectedSolution("sum(9)".parseAsStruct(), 0.15),
                    ExpectedSolution("sum(10)".parseAsStruct(), 0.15),
                    ExpectedSolution("sum(11)".parseAsStruct(), 0.15),
                    ExpectedSolution("sum(12)".parseAsStruct(), 0.15),
                    ExpectedSolution("sum(13)".parseAsStruct(), 0.1),
                    ExpectedSolution("sum(14)".parseAsStruct(), 0.05),
                    ExpectedSolution("sum(15)".parseAsStruct(), 0.05),
                )
            )
        )
    }

    /***
     * https://dtai.cs.kuleuven.be/problog/tutorial/basic/03_dice.html#built-in-predicate-between-3
     * */
//    @Test
//    fun testBetweenPredicate() {
//        TestUtils.assertQueryWithSolutions(
//            """
//                1/6::dice(1,1); 1/6::dice(2,1); 1/6::dice(3,1); 1/6::dice(4,1); 1/6::dice(5,1); 1/6::dice(6,1).
//                1/6::dice(1,D); 1/6::dice(2,D); 1/6::dice(3,D); 1/6::dice(4,D); 1/6::dice(5,D); 1/6::dice(6,D) :- D > 1, P is D-1, continue(P).
//                stop(N) :- dice(6,N).
//                continue(N) :- dice(X,N), X < 6.
//            """.parseAsTheory(ProblogProbSolverFactory.defaultBuiltins.operators),
//            listOf(
//                QueryWithSolutions(
//                    "stop(1)".parseAsStruct(),
//                    listOf(ExpectedSolution("stop(1)".parseAsStruct(), 0.16666667))
//                ),
//                QueryWithSolutions(
//                    "stop(2)".parseAsStruct(),
//                    listOf(ExpectedSolution("stop(2)".parseAsStruct(), 0.13888889))
//                ),
//                QueryWithSolutions(
//                    "stop(3)".parseAsStruct(),
//                    listOf(ExpectedSolution("stop(3)".parseAsStruct(), 0.11574074))
//                ),
//                QueryWithSolutions(
//                    "stop(4)".parseAsStruct(),
//                    listOf(ExpectedSolution("stop(4)".parseAsStruct(), 0.096450617))
//                ),
//                QueryWithSolutions(
//                    "stop(5)".parseAsStruct(),
//                    listOf(ExpectedSolution("stop(5)".parseAsStruct(), 0.080375514))
//                ),
//            )
//        )
//    }

    /***
     * https://dtai.cs.kuleuven.be/problog/tutorial/basic/03_dice.html#built-in-predicate-between-3
     * */
//    @Test
//    fun testRecursionAndLists() {
//        TestUtils.assertQueryWithSolutions(
//            """
//                1/3::dice(1,D); 1/3::dice(2,D); 1/3::dice(3,D) :- die(D).
//                die(X) :- between(1,3,X).
//                roll(L) :-
//                   next(1,[1],L).
//                next(N,Seen,Rev) :-
//                   dice(Now,N),
//                   member(Now,Seen),
//                   reverse(Seen,[],Rev).
//                next(N,Seen,List) :-
//                   dice(Now,N),
//                   \+ member(Now,Seen),
//                   next(Now,[Now|Seen],List).
//                member(X,[X|_]).
//                member(X,[_|Z]) :-
//                   member(X,Z).
//                reverse([],L,L).
//                reverse([H|T],A,L) :-
//                   reverse(T,[H|A],L).
//            """.parseAsTheory(ProblogProbSolverFactory.defaultBuiltins.operators),
//            listOf(
//                QueryWithSolutions(
//                    "roll(_)".parseAsStruct(),
//                    listOf(
//                        ExpectedSolution("roll([1, 2, 3])".parseAsStruct(), 0.11111111),
//                        ExpectedSolution("roll([1, 2])".parseAsStruct(), 0.22222222),
//                        ExpectedSolution("roll([1, 3, 2])".parseAsStruct(), 0.11111111),
//                        ExpectedSolution("roll([1, 3])".parseAsStruct(), 0.22222222),
//                        ExpectedSolution("roll([1])".parseAsStruct(), 0.33333333)
//                    )
//                ),
//            )
//        )
//    }

}
