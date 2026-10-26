package it.unibo.tuprolog.solve;

import org.junit.jupiter.api.Assertions;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class TestJavaConstruction {
    private final Expectations expectations;

    public TestJavaConstruction(Expectations expectations) {
        this.expectations = expectations;
    }

    private void test(Predicate<Expectations> shouldWork, Supplier<SolverFactory> constructor) {
        if (shouldWork.test(expectations)) {
            Assertions.assertNotNull(constructor.get());
        } else {
            Assertions.assertThrows(IllegalStateException.class, constructor::get);
        }
    }

    public void testPrologFactory() {
        test(Expectations::getPrologShouldWork, Solver::prolog);
    }

    public void testProblogFactory() {
        test(Expectations::getProblogShouldWork, Solver::problog);
    }

    public void testConcurrentFactory() {
        test(Expectations::getConcurrentShouldWork, Solver::concurrent);
    }
}
