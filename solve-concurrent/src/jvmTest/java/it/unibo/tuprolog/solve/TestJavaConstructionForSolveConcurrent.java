package it.unibo.tuprolog.solve;

import org.junit.Test;

public class TestJavaConstructionForSolveConcurrent extends TestJavaConstruction {
    public TestJavaConstructionForSolveConcurrent() {
        super(SolveConcurrentTest.expectations);
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
