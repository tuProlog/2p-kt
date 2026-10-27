package it.unibo.tuprolog.solve;

import org.junit.jupiter.api.Test;

public class PrologJavaConstructionSpec extends JavaConstructionContract {
    public PrologJavaConstructionSpec() {
        super(SolvePrologExpectations.expectations);
    }

    @Override
    @Test
    public void testPrologFactory() {
        super.testPrologFactory();
    }

    @Override
    @Test
    public void testProblogFactory() {
        super.testProblogFactory();
    }

    @Override
    @Test
    public void testConcurrentFactory() {
        super.testConcurrentFactory();
    }
}
