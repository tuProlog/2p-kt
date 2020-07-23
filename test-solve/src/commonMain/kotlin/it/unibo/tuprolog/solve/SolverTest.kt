package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.CustomDatabases.ifThen1ToSolution
import it.unibo.tuprolog.solve.CustomDatabases.ifThen2ToSolution
import it.unibo.tuprolog.solve.CustomDatabases.ifThenElse1ToSolution
import it.unibo.tuprolog.solve.CustomDatabases.ifThenElse2ToSolution
import it.unibo.tuprolog.solve.PrologStandardExampleDatabases.callStandardExampleDatabaseGoalsToSolution
import it.unibo.tuprolog.solve.PrologStandardExampleDatabases.catchAndThrowStandardExampleDatabaseNotableGoalToSolution
import it.unibo.tuprolog.solve.PrologStandardExampleDatabases.conjunctionStandardExampleDatabaseNotableGoalToSolution
import it.unibo.tuprolog.solve.PrologStandardExampleDatabases.ifThenElseStandardExampleNotableGoalToSolution
import it.unibo.tuprolog.solve.PrologStandardExampleDatabases.ifThenStandardExampleDatabaseNotableGoalToSolution
import it.unibo.tuprolog.solve.PrologStandardExampleDatabases.notStandardExampleDatabaseNotableGoalToSolution
import it.unibo.tuprolog.solve.PrologStandardExampleDatabases.prologStandardExampleDatabaseNotableGoalToSolution
import it.unibo.tuprolog.solve.PrologStandardExampleDatabases.prologStandardExampleWithCutDatabaseNotableGoalToSolution
import it.unibo.tuprolog.solve.TestingClauseDatabases.callTestingGoalsToSolutions
import it.unibo.tuprolog.solve.TestingClauseDatabases.catchTestingGoalsToSolutions
import it.unibo.tuprolog.solve.TestingClauseDatabases.customRangeListGeneratorDatabaseNotableGoalToSolution
import it.unibo.tuprolog.solve.TestingClauseDatabases.customReverseListDatabaseNotableGoalToSolution
import it.unibo.tuprolog.solve.TestingClauseDatabases.cutConjunctionAndBacktrackingDatabaseNotableGoalToSolutions
import it.unibo.tuprolog.solve.TestingClauseDatabases.haltTestingGoalsToSolutions
import it.unibo.tuprolog.solve.TestingClauseDatabases.infiniteComputationDatabaseNotableGoalToSolution
import it.unibo.tuprolog.solve.TestingClauseDatabases.simpleCutAndConjunctionDatabaseNotableGoalToSolutions
import it.unibo.tuprolog.solve.TestingClauseDatabases.simpleCutDatabaseNotableGoalToSolutions
import it.unibo.tuprolog.solve.TestingClauseDatabases.simpleFactDatabaseNotableGoalToSolutions
import it.unibo.tuprolog.solve.TimeRelatedDatabases.lessThan500MsGoalToSolution
import it.unibo.tuprolog.solve.TimeRelatedDatabases.slightlyMoreThan1100MsGoalToSolution
import it.unibo.tuprolog.solve.TimeRelatedDatabases.slightlyMoreThan1800MsGoalToSolution
import it.unibo.tuprolog.solve.TimeRelatedDatabases.slightlyMoreThan500MsGoalToSolution

/** A prototype class for testing solver implementations */
interface SolverTest {

    companion object {
        fun prototype(solverFactory: SolverFactory): SolverTest =
            SolverTestImpl(solverFactory)
    }

    /** A short test max duration */
    val shortDuration: TimeDuration
        get() = 500L

    /** A medium test max duration */
    val mediumDuration: TimeDuration
        get() = 2 * shortDuration

    /** A long test max duration */
    val longDuration: TimeDuration
        get() = 2 * mediumDuration

    val maxDuration: TimeDuration
        get() = shortDuration

    /** Test presence of correct built-ins */
    fun testBuiltinApi()

    fun testAssert()

    fun testAssertZ()

    fun testAssertA()

    fun testWrite()

    fun testStandardOutput()

    fun testFindAll()

    /** Test `true` goal */
    fun testTrue()

    /** Test with [lessThan500MsGoalToSolution] */
    fun testTimeout1()

    /** Test with [slightlyMoreThan500MsGoalToSolution] */
    fun testTimeout2()

    /** Test with [slightlyMoreThan1100MsGoalToSolution] */
    fun testTimeout3()

    /** Test with [slightlyMoreThan1800MsGoalToSolution] */
    fun testTimeout4()

    /** Test with [ifThen1ToSolution] */
    fun testIfThen1()

    /** Test with [ifThenElse1ToSolution] */
    fun testIfThenElse1()

    /** Test with [ifThenElse2ToSolution] */
    fun testIfThenElse2()

    /** Test with [ifThen2ToSolution] */
    fun testIfThen2()

    /** Test with [simpleFactDatabaseNotableGoalToSolutions] */
    fun testUnification()

    /** Test with [simpleCutDatabaseNotableGoalToSolutions] */
    fun testSimpleCutAlternatives()

    /** Test with [simpleCutAndConjunctionDatabaseNotableGoalToSolutions] */
    fun testCutAndConjunction()

    /** Test with [cutConjunctionAndBacktrackingDatabaseNotableGoalToSolutions] */
    fun testCutConjunctionAndBacktracking()

    /** Test with [infiniteComputationDatabaseNotableGoalToSolution] */
    fun testMaxDurationParameterAndTimeOutException()

    /** Test with [prologStandardExampleDatabaseNotableGoalToSolution] */
    fun testPrologStandardSearchTreeExample()

    /** Test with [prologStandardExampleWithCutDatabaseNotableGoalToSolution] */
    fun testPrologStandardSearchTreeWithCutExample()

    /** Test with [customReverseListDatabaseNotableGoalToSolution] */
    fun testBacktrackingWithCustomReverseListImplementation()

    /** Test with [conjunctionStandardExampleDatabaseNotableGoalToSolution] */
    fun testWithPrologStandardConjunctionExamples()

    /** A test with all goals used in conjunction with `true` or `fail` to test Conjunction properties */
    fun testConjunctionProperties()

    /** Call primitive testing with [callTestingGoalsToSolutions] and [callStandardExampleDatabaseGoalsToSolution] */
    fun testCallPrimitive()

    /** A test in which all testing goals are called through the Call primitive */
    fun testCallPrimitiveTransparency()

    /** Call primitive testing with [catchTestingGoalsToSolutions] and [catchAndThrowStandardExampleDatabaseNotableGoalToSolution] */
    fun testCatchPrimitive()

    /** A test in which all testing goals are called through the Catch primitive */
    fun testCatchPrimitiveTransparency()

    /** Halt primitive testing with [haltTestingGoalsToSolutions] */
    fun testHaltPrimitive()

    /** Not rule testing with [notStandardExampleDatabaseNotableGoalToSolution] */
    fun testNotPrimitive()

    /** A test in which all testing goals are called through the Not rule */
    fun testNotModularity()

    /** If-Then rule testing with [ifThenStandardExampleDatabaseNotableGoalToSolution] */
    fun testIfThenRule()

    /** If-Then-Else rule testing with [ifThenElseStandardExampleNotableGoalToSolution] */
    fun testIfThenElseRule()

    /** Test with [customRangeListGeneratorDatabaseNotableGoalToSolution] */
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

    /** Test StandardOperatorPrimitive */
    fun testStandardGreaterThan()
    fun testStandardGreaterThanOrEqual()
    fun testStandardLowerThan()
    fun testStandardLowerThanOrEqual()
    fun testStandardEqual()
    fun testStandardNotEqual()

}

