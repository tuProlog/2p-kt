package it.unibo.tuprolog.solve;

import org.junit.jupiter.api.Test;

public class ConcurrentJavaConstructionSpec extends JavaConstructionContract {
    public ConcurrentJavaConstructionSpec() {
        super(SolveConcurrentExpectations.expectations);
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
