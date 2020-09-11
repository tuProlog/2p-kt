package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.CustomTheories.ifThen1ToSolution
import it.unibo.tuprolog.solve.CustomTheories.ifThen2ToSolution
import it.unibo.tuprolog.solve.CustomTheories.ifThenElse1ToSolution
import it.unibo.tuprolog.solve.CustomTheories.ifThenElse2ToSolution
import it.unibo.tuprolog.solve.PrologStandardExampleTheories.callStandardExampleTheoryGoalsToSolution
import it.unibo.tuprolog.solve.PrologStandardExampleTheories.catchAndThrowTheoryExampleNotableGoalToSolution
import it.unibo.tuprolog.solve.PrologStandardExampleTheories.conjunctionStandardExampleTheoryNotableGoalToSolution
import it.unibo.tuprolog.solve.PrologStandardExampleTheories.ifThenElseStandardExampleNotableGoalToSolution
import it.unibo.tuprolog.solve.PrologStandardExampleTheories.ifThenStandardExampleTheoryNotableGoalToSolution
import it.unibo.tuprolog.solve.PrologStandardExampleTheories.notStandardExampleTheoryNotableGoalToSolution
import it.unibo.tuprolog.solve.PrologStandardExampleTheories.prologStandardExampleTheoryNotableGoalToSolution
import it.unibo.tuprolog.solve.PrologStandardExampleTheories.prologStandardExampleWithCutTheoryNotableGoalToSolution
import it.unibo.tuprolog.solve.TestingClauseTheories.callTestingGoalsToSolutions
import it.unibo.tuprolog.solve.TestingClauseTheories.catchTestingGoalsToSolutions
import it.unibo.tuprolog.solve.TestingClauseTheories.customRangeListGeneratorTheoryNotableGoalToSolution
import it.unibo.tuprolog.solve.TestingClauseTheories.customReverseListTheoryNotableGoalToSolution
import it.unibo.tuprolog.solve.TestingClauseTheories.cutConjunctionAndBacktrackingTheoryNotableGoalToSolutions
import it.unibo.tuprolog.solve.TestingClauseTheories.haltTestingGoalsToSolutions
import it.unibo.tuprolog.solve.TestingClauseTheories.infiniteComputationTheoryNotableGoalToSolution
import it.unibo.tuprolog.solve.TestingClauseTheories.simpleCutAndConjunctionTheoryNotableGoalToSolutions
import it.unibo.tuprolog.solve.TestingClauseTheories.simpleCutTheoryNotableGoalToSolutions
import it.unibo.tuprolog.solve.TestingClauseTheories.simpleFactTheoryNotableGoalToSolutions
import it.unibo.tuprolog.solve.TimeRelatedTheories.lessThan500MsGoalToSolution
import it.unibo.tuprolog.solve.TimeRelatedTheories.slightlyMoreThan600MsGoalToSolution
import it.unibo.tuprolog.solve.TimeRelatedTheories.slightlyMoreThan700MsGoalToSolution
import it.unibo.tuprolog.solve.TimeRelatedTheories.slightlyMoreThan500MsGoalToSolution

/** A prototype class for testing solver implementations */
interface TestSolver : SolverTest {

    companion object {
        fun prototype(solverFactory: SolverFactory): TestSolver =
            TestSolverImpl(solverFactory)
    }

    fun testUnknownFlag1()

    fun testUnknownFlag2()

    /** Test presence of correct built-ins */
    fun testBuiltinApi()

    fun testAssert()

    fun testAssertZ()

    fun testAssertA()

    fun testWrite()

    fun testStandardOutput()

    fun testFindAll()

    fun testSideEffectsPersistentAfterBacktracking1()

    /** Test `true` goal */
    fun testTrue()

    /** Test with [lessThan500MsGoalToSolution] */
    fun testTimeout1()

    /** Test with [slightlyMoreThan500MsGoalToSolution] */
    fun testTimeout2()

    /** Test with [slightlyMoreThan600MsGoalToSolution] */
    fun testTimeout3()

    /** Test with [slightlyMoreThan700MsGoalToSolution] */
    fun testTimeout4()

    /** Test with [ifThen1ToSolution] */
    fun testIfThen1()

    /** Test with [ifThenElse1ToSolution] */
    fun testIfThenElse1()

    /** Test with [ifThenElse2ToSolution] */
    fun testIfThenElse2()

    /** Test with [ifThen2ToSolution] */
    fun testIfThen2()

    /** Test with [simpleFactTheoryNotableGoalToSolutions] */
    fun testUnification()

    /** Test with [simpleCutTheoryNotableGoalToSolutions] */
    fun testSimpleCutAlternatives()

    /** Test with [simpleCutAndConjunctionTheoryNotableGoalToSolutions] */
    fun testCutAndConjunction()

    /** Test with [cutConjunctionAndBacktrackingTheoryNotableGoalToSolutions] */
    fun testCutConjunctionAndBacktracking()

    /** Test with [infiniteComputationTheoryNotableGoalToSolution] */
    fun testMaxDurationParameterAndTimeOutException()

    /** Test with [prologStandardExampleTheoryNotableGoalToSolution] */
    fun testPrologStandardSearchTreeExample()

    /** Test with [prologStandardExampleWithCutTheoryNotableGoalToSolution] */
    fun testPrologStandardSearchTreeWithCutExample()

    /** Test with [customReverseListTheoryNotableGoalToSolution] */
    fun testBacktrackingWithCustomReverseListImplementation()

    /** Test with [conjunctionStandardExampleTheoryNotableGoalToSolution] */
    fun testWithPrologStandardConjunctionExamples()

    /** A test with all goals used in conjunction with `true` or `fail` to test Conjunction properties */
    fun testConjunctionProperties()

    /** Call primitive testing with [callTestingGoalsToSolutions] and [callStandardExampleTheoryGoalsToSolution] */
    fun testCallPrimitive()

    /** A test in which all testing goals are called through the Call primitive */
    fun testCallPrimitiveTransparency()

    /** Call primitive testing with [catchTestingGoalsToSolutions] and [catchAndThrowTheoryExampleNotableGoalToSolution] */
    fun testCatchPrimitive()

    /** A test in which all testing goals are called through the Catch primitive */
    fun testCatchPrimitiveTransparency()

    /** Halt primitive testing with [haltTestingGoalsToSolutions] */
    fun testHaltPrimitive()

    /** Not rule testing with [notStandardExampleTheoryNotableGoalToSolution] */
    fun testNotPrimitive()

    /** A test in which all testing goals are called through the Not rule */
    fun testNotModularity()

    /** If-Then rule testing with [ifThenStandardExampleTheoryNotableGoalToSolution] */
    fun testIfThenRule()

    /** If-Then-Else rule testing with [ifThenElseStandardExampleNotableGoalToSolution] */
    fun testIfThenElseRule()

    /** Test with [customRangeListGeneratorTheoryNotableGoalToSolution] */
    fun testNumbersRangeListGeneration()
    fun testFailure()
    fun testBasicBacktracking1()
    fun testBasicBacktracking2()
    fun testBasicBacktracking3()
    fun testBasicBacktracking4()
    fun testConjunction()
    fun testConjunctionOfConjunctions()
    fun testConjunctionWithUnification()
    fun testDisjunction()
    fun testDisjunctionWithUnification()
    fun testMember()
    fun testAssertRules()
    fun testRetract()
    fun testNatural()
    fun testFunctor()
    fun testUniv()
    fun testAppend()
    fun testRetractAll()

    fun testTermGreaterThan()
    fun testTermGreaterThanOrEqual()
    fun testTermSame()
    fun testTermNotSame()
    fun testTermLowerThan()
    fun testTermLowerThanOrEqual()

    /** atom_chars/2 test */
    fun testAtomChars()

    /** atom_length test */
    fun testAtomLength()

    /** char_code/2 test */
    fun testCharCode()

    /** atom_codes/2 test */
    fun testAtomCodes()

    /** atom_concat/3 test */
    fun testAtomConcat()
}

