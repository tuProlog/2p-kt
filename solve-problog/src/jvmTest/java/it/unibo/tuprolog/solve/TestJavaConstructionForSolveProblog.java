package it.unibo.tuprolog.solve;

import org.junit.Test;

public class TestJavaConstructionForSolveProblog extends TestJavaConstruction {
    public TestJavaConstructionForSolveProblog() {
        super(SolveProblogTest.expectations);
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
