package it.unibo.tuprolog.examples;

import it.unibo.tuprolog.core.Struct;
import it.unibo.tuprolog.core.Var;
import it.unibo.tuprolog.solve.Solution;
import it.unibo.tuprolog.solve.SolveOptions;
import it.unibo.tuprolog.solve.Solver;
import it.unibo.tuprolog.theory.Theory;
import it.unibo.tuprolog.theory.parsing.ClausesParser;
import org.junit.Test;

import java.util.Iterator;

import static it.unibo.tuprolog.solve.ProbExtensions.getProbability;
import static it.unibo.tuprolog.solve.ProbExtensions.probabilistic;
import static it.unibo.tuprolog.solve.problog.Operators.PROBLOG_OPERATORS;

public class ExampleProbLogSolverJvm {
    @Test
    public void examplifyProblogSolver() {
        ClausesParser clausesParser = ClausesParser.withOperators(PROBLOG_OPERATORS);

        Theory probabilisticTheory = clausesParser.parseTheory(
                "0.6::edge(1,2).\n" +
                "0.1::edge(1,3).\n" +
                "0.4::edge(2,5).\n" +
                "0.3::edge(2,6).\n" +
                "0.3::edge(3,4).\n" +
                "0.8::edge(4,5).\n" +
                "0.2::edge(5,6).\n" +
                "\n" +
                "path(X,Y) :- edge(X,Y).\n" +
                "path(X,Y) :- edge(X,Z), Y \\== Z, path(Z,Y)."
        );

        Solver problogSolver = Solver.problog().newBuilder().staticKb(probabilisticTheory).build();
        Struct goal = Struct.of("path", Var.of("From"), Var.of("To"));
        Iterator<Solution> si = problogSolver.solve(goal, probabilistic(SolveOptions.allLazily())).iterator();
        while (si.hasNext()) {
            Solution solution = si.next();
            if (solution.isYes()) {
                System.out.printf("yes: %s with probability %g\n", solution.getSolvedQuery(), getProbability(solution));
            }
        }
    }
}
