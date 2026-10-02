package it.unibo.tuprolog.solve;

import org.junit.Test;

public class TestJavaConstructionForSolveProlog extends TestJavaConstruction {
    public TestJavaConstructionForSolveProlog() {
        super(SolvePrologTest.expectations);
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
