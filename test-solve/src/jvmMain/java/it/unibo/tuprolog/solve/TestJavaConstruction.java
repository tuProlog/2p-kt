package it.unibo.tuprolog.solve;

import it.unibo.tuprolog.solve.Expectations;
import it.unibo.tuprolog.solve.Solver;
import it.unibo.tuprolog.solve.SolverFactory;
import org.junit.Assert;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class TestJavaConstruction {
    private final Expectations expectations;

    public TestJavaConstruction(Expectations expectations) {
        this.expectations = expectations;
    }

    private void test(Predicate<Expectations> shouldWork, Supplier<SolverFactory> constructor) {
        if (shouldWork.test(expectations)) {
            Assert.assertNotNull(constructor.get());
        } else {
            Assert.assertThrows(IllegalStateException.class, constructor::get);
        }
    }

    @SuppressWarnings("deprecation")
    public void testClassicFactory() {
        test(Expectations::getClassicShouldWork, Solver::classic);
    }

    @SuppressWarnings("deprecation")
    public void testStreamsFactory() {
        test(Expectations::getStreamsShouldWork, Solver::streams);
    }

    public void testProblogFactory() {
        test(Expectations::getProblogShouldWork, Solver::problog);
    }

    public void testPrologFactory() {
        test(Expectations::getPrologShouldWork, Solver::prolog);
    }
}
