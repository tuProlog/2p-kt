package it.unibo.tuprolog.examples;

import it.unibo.tuprolog.core.Integer;
import it.unibo.tuprolog.core.Struct;
import it.unibo.tuprolog.core.Term;
import it.unibo.tuprolog.core.Var;
import it.unibo.tuprolog.solve.MutableSolver;
import it.unibo.tuprolog.solve.Solution;
import it.unibo.tuprolog.solve.Solver;
import it.unibo.tuprolog.theory.Theory;
import it.unibo.tuprolog.theory.parsing.ClausesReader;
import org.gciatto.kt.math.BigInteger;
import org.junit.Test;

import java.io.InputStream;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;

public class ReadEditSolveJvm {

    @Test
    public void main() {
        InputStream inputStream = ReadEditSolveJvm.class.getResourceAsStream("increment.pl");
        ClausesReader clausesReader = ClausesReader.withDefaultOperators();
        Theory theory = clausesReader.readTheory(inputStream);
        MutableSolver solver = Solver.prolog().newBuilder().staticKb(theory).buildMutable();
        Struct fact = Struct.of("diff", Integer.of(15));
        solver.assertZ(fact);
        Struct query = Struct.of("increment", Integer.of(15), Var.of("X"));
        Iterator<Solution> solutionIterator = solver.solve(query).iterator();
        while (solutionIterator.hasNext()) {
            Solution solution = solutionIterator.next();
            if (solution.isYes()) {
                Term value = solution.getSubstitution().getByName("X");
                BigInteger valueAsBigInteger = value.asInteger().getValue();
                int actualValue = valueAsBigInteger.toInt();
                assertEquals(30, actualValue);
            }
        }
    }

    @Test
    public void simple() {
        InputStream inputStream = ReadEditSolveJvm.class.getResourceAsStream("increment.pl");
        ClausesReader clausesReader = ClausesReader.withDefaultOperators();
        Theory theory = clausesReader.readTheory(inputStream);
        MutableSolver solver = Solver.prolog().newBuilder().staticKb(theory).buildMutable();
        Struct fact = Struct.of("diff", Integer.of(15));
        solver.assertZ(fact);
        Struct query = Struct.of("increment", Integer.of(15), Var.of("X"));
        Solution solution = solver.solveOnce(query);
        if (solution.isYes()) {
            Term value = solution.getSubstitution().getByName("X");
            BigInteger valueAsBigInteger = value.asInteger().getValue();
            int actualValue = valueAsBigInteger.toInt();
            assertEquals(30, actualValue);
        }
    }
}
