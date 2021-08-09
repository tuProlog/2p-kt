package it.unibo.tuprolog.examples;

import it.unibo.tuprolog.core.Atom;
import it.unibo.tuprolog.core.Integer;
import it.unibo.tuprolog.core.Struct;
import it.unibo.tuprolog.core.Substitution;
import it.unibo.tuprolog.core.Term;
import it.unibo.tuprolog.core.Var;
import it.unibo.tuprolog.core.operators.OperatorSet;
import it.unibo.tuprolog.solve.ExecutionContext;
import it.unibo.tuprolog.solve.MutableSolver;
import it.unibo.tuprolog.solve.Solution;
import it.unibo.tuprolog.solve.Solver;
import it.unibo.tuprolog.solve.library.AliasedLibrary;
import it.unibo.tuprolog.solve.library.Library;
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper;
import it.unibo.tuprolog.solve.primitive.Solve;
import it.unibo.tuprolog.solve.primitive.TernaryRelation;
import it.unibo.tuprolog.solve.primitive.UnaryPredicate;
import it.unibo.tuprolog.theory.Theory;
import it.unibo.tuprolog.theory.parsing.ClausesParser;
import it.unibo.tuprolog.unify.Unificator;
import kotlin.sequences.Sequence;
import org.gciatto.kt.math.BigInteger;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static kotlin.collections.MapsKt.emptyMap;
import static kotlin.collections.MapsKt.mapOf;
import static kotlin.sequences.SequencesKt.generateSequence;
import static kotlin.sequences.SequencesKt.map;
import static kotlin.sequences.SequencesKt.takeWhile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PrimitiveExamplesJvm {

    private static final List<String> logs = new ArrayList<>();

    private static class LoggingPrimitive extends UnaryPredicate.Predicative<ExecutionContext> {
        public static final LoggingPrimitive INSTANCE = new LoggingPrimitive();

        private LoggingPrimitive() {
            super(("log_msg"));
        }

        @Override
        protected boolean compute(
                @NotNull Solve.Request<? extends ExecutionContext> request,
                @NotNull Term first
        ) {
            logs.add(String.format("[%s] %s", request.getContext().getLogicStackTrace().get(1), first));
            return true;
        }
    }

    @Test
    public void loggingPrimitiveExample() {
        final AliasedLibrary library = Library.aliased(
                OperatorSet.EMPTY,
                Theory.empty(),
                mapOf(LoggingPrimitive.INSTANCE.getDescriptionPair()),
                emptyMap(),
                "prolog.logging"
        );
        final Theory theory = ClausesParser.getWithDefaultOperators().parseTheory(
                "a(X) :- b(X).\n" +
                        "b(X) :- log_msg(X)."
        );
        MutableSolver solver = Solver.getClassic().mutableSolverOf(theory);
        solver.loadLibrary(library);

        logs.clear();

        Solution sol = solver.solveOnce(Struct.of("a", Atom.of("my_msg")));
        assertTrue(sol.isYes());
        assertEquals(1, logs.size());
        assertEquals("[b(my_msg)] my_msg", logs.get(0));
    }

    private static class IntRangePrimitive extends TernaryRelation.WithoutSideEffects<ExecutionContext> {

        public static final IntRangePrimitive INSTANCE = new IntRangePrimitive();

        private IntRangePrimitive() {
            super("int_range");
        }

        @NotNull
        @Override
        protected Sequence<Substitution> computeAllSubstitutions(
                @NotNull Solve.Request<? extends ExecutionContext> request,
                @NotNull Term first,
                @NotNull Term second,
                @NotNull Term third
        ) {
            PrimitiveWrapper.ensuringArgumentIsInteger(request,0);
            PrimitiveWrapper.ensuringArgumentIsInteger(request, 1);
            PrimitiveWrapper.ensuringArgumentIsVariable(request, 2);
            Integer min = first.castToInteger();
            Integer max = second.castToInteger();

            Sequence<BigInteger> range = generateSequence(min.getValue(), x -> x.plus(BigInteger.Companion.of(1)));
            range = takeWhile(range, x -> x.compareTo(max.getValue()) <= 0);
            return map(range, x -> Unificator.getDefault().mgu(third, Integer.of(x)));
        }
    }

    @Test
    public void intRangePrimitiveExample() {
        final AliasedLibrary library = Library.aliased(
                OperatorSet.EMPTY,
                Theory.empty(),
                mapOf(IntRangePrimitive.INSTANCE.getDescriptionPair()),
                emptyMap(),
                "prolog.ranges"
        );
        MutableSolver solver = Solver.getClassic().mutableSolverOf();
        solver.loadLibrary(library);

        List<Solution> sol = solver.solveList(Struct.of("int_range", Integer.of(1), Integer.of(5), Var.of("X")));
        assertEquals(5, sol.size());
        for (int i = 1; i <= 5; i++) {
            Solution s = sol.get(i - 1);
            assertTrue(s.isYes());
            assertEquals(Integer.of(i), s.getSubstitution().getByName("X"));
        }
    }
}
