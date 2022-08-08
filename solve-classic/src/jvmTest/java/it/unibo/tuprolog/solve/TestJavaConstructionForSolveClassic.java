package it.unibo.tuprolog.solve;

import org.junit.Test;

public class TestJavaConstructionForSolveClassic extends TestJavaConstruction {
    public TestJavaConstructionForSolveClassic() {
        super(SolveClassicTest.expectations);
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
