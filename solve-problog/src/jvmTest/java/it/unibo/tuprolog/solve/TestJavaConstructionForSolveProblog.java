package it.unibo.tuprolog.solve;

import org.junit.Test;

public class TestJavaConstructionForSolveProblog extends TestJavaConstruction {
    public TestJavaConstructionForSolveProblog() {
        super(SolveProblogTest.expectations);
    }

    @Override
    @Test
    public void testClassicFactory() {
        super.testClassicFactory();
    }

    @Override
    @Test
    public void testStreamsFactory() {
        super.testStreamsFactory();
    }

    @Override
    @Test
    public void testProblogFactory() {
        super.testProblogFactory();
    }

    @Override
    @Test
    public void testPrologFactory() {
        super.testPrologFactory();
    }
}
