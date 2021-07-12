package it.unibo.tuprolog.solve.problog.probabilistic

import it.unibo.tuprolog.core.parsing.parseAsStruct
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import it.unibo.tuprolog.theory.parsing.parseAsTheory
import kotlin.test.Test

class TestGenericExamples {

    /***
     * https://dtai.cs.kuleuven.be/problog/tutorial/various/01_montyhall.html
     */
    @Test
    fun testMontyHallPuzzle() {
        TestUtils.assertQueryWithSolutions(
            """
                1/3::prize(1) ; 1/3::prize(2) ; 1/3::prize(3). 
                select_door(1).   
                member(X,[X|T]).
                member(X,[H|T]) :- member(X,T). 
                0.5::open_door(A) ; 0.5::open_door(B) :-
                  member(A, [1,2,3]),
                  member(B, [1,2,3]),
                  A < B,
                  \+ prize(A), \+ prize(B),
                  \+ select_door(A), \+ select_door(B).
                open_door(A) :-
                  member(A, [1,2,3]),
                  member(B, [1,2,3]),
                  \+ prize(A), prize(B),
                  \+ select_door(A), \+ select_door(B).
                win_keep :-
                  select_door(A),
                  prize(A).
                win_switch :-
                  member(A, [1,2,3]),
                  \+ select_door(A),
                  prize(A),
                  \+ open_door(A).
            """.parseAsTheory(ProblogSolverFactory.defaultBuiltins.operators),
            listOf(
                QueryWithSolutions(
                    "prize(X)".parseAsStruct(),
                    listOf(
                        ExpectedSolution("prize(1)".parseAsStruct(), 0.33333333),
                        ExpectedSolution("prize(2)".parseAsStruct(), 0.33333333),
                        ExpectedSolution("prize(3)".parseAsStruct(), 0.33333333),
                    )
                ),
                QueryWithSolutions(
                    "select_door(X)".parseAsStruct(),
                    listOf(ExpectedSolution("select_door(1)".parseAsStruct(), 1.0))
                ),
                QueryWithSolutions(
                    "win_keep".parseAsStruct(),
                    listOf(ExpectedSolution("win_keep".parseAsStruct(), 0.33333333))
                ),
                QueryWithSolutions(
                    "win_switch".parseAsStruct(),
                    listOf(ExpectedSolution("win_switch".parseAsStruct(), 0.66666667))
                ),
            )
        )
    }

    /***
     * https://dtai.cs.kuleuven.be/problog/tutorial/various/01_montyhall.html
     */
    @Test
    fun testMontyHallPuzzle2() {
        TestUtils.assertQueryWithSolutions(
            """
               1/3::picked_car.
                picked_goat:- not(picked_car).
                1/2::picked_goat1:- picked_goat.
                picked_goat2:- picked_goat, not(picked_goat1).        
                1/2::revealed_goat1:- picked_car.
                revealed_goat1:- picked_goat2.
                1/2::revealed_goat2:- picked_car.
                revealed_goat2:- picked_goat1.
                revealed_goat:- revealed_goat1 ; revealed_goat2.        
                switched_gets_car:-picked_goat, revealed_goat.
                switched_gets_goat:- not(switched_gets_car).
            """.parseAsTheory(ProblogSolverFactory.defaultBuiltins.operators),
            listOf(
                QueryWithSolutions(
                    "switched_gets_goat".parseAsStruct(),
                    listOf(ExpectedSolution("switched_gets_goat".parseAsStruct(), 0.33333333))
                ),
                QueryWithSolutions(
                    "switched_gets_car".parseAsStruct(),
                    listOf(ExpectedSolution("switched_gets_car".parseAsStruct(), 0.66666667))
                ),
            )
        )
    }
}
