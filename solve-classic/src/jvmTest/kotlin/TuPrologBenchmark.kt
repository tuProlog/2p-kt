import alice.tuprolog.Prolog
import alice.tuprolog.Theory
import it.unibo.tuprolog.solve.TimeInstant
import it.unibo.tuprolog.solve.currentTimeInstant
import org.junit.Ignore
import org.junit.Test

/**
 * Benchmark class for classic version of *tuProlog* (the old predecessor of this project)
 *
 * @author Enrico
 */
internal class TuPrologBenchmark {

    @Test
    @Ignore
    fun `8-QueensBenchmark`() {
        val queensTheory = Theory.parseWithStandardOperators(
            """
                top:-queens(8,Qs),fail.
                top.

                queens(N,Qs) :-
                    range(1,N,Ns),
                    queens(Ns,[],Qs).

                queens([],Qs,Qs).
                queens(UnplacedQs,SafeQs,Qs) :-
                    select(UnplacedQs,UnplacedQs1,Q),
                    not_attack(SafeQs,Q),
                    queens(UnplacedQs1,[Q|SafeQs],Qs).

                not_attack(Xs,X) :-
                    not_attack(Xs,X,1).

                not_attack([],_,_) :- !.
                not_attack([Y|Ys],X,N) :-
                    X =\= Y+N, X =\= Y-N,
                    N1 is N+1,
                    not_attack(Ys,X,N1).

                select([X|Xs],Xs,X).
                select([Y|Ys],[Y|Zs],X) :- select(Ys,Zs,X).

                range(N,N,[N]) :- !.
                range(M,N,[M|Ns]) :-
                    M < N,
                    M1 is M+1,
                    range(M1,N,Ns).
                """.trimIndent()
        )

        val numberOfExecutions = 10 // it will take approximately 5s
//        val numberOfExecutions = 50 // it will take approximately 21s
//        val numberOfExecutions = 100 // it will take approximately 42s
//        val numberOfExecutions = 1000 // it will take approximately 6m 3s

        val timeList = mutableListOf<Long>()

        var engine: Prolog
        var initialTime: TimeInstant

        repeat(numberOfExecutions) {
            println(it)

            engine = Prolog()
            engine.theory = queensTheory

            initialTime = currentTimeInstant()
            engine.solve("top.")
            timeList.add(currentTimeInstant() - initialTime)
        }

        println("\n8-Queens: ${timeList.sum() / timeList.size} ms to solve in an average of $numberOfExecutions executions")
        println(timeList)
    }
}
