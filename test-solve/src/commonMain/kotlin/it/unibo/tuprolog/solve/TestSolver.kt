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
import it.unibo.tuprolog.solve.TimeRelatedTheories.slightlyMoreThan500MsGoalToSolution
import it.unibo.tuprolog.solve.TimeRelatedTheories.slightlyMoreThan600MsGoalToSolution
import it.unibo.tuprolog.solve.TimeRelatedTheories.slightlyMoreThan700MsGoalToSolution

/**
 * The main, comprehensive conformance suite for a [Solver] implementation: control-flow constructs (conjunction,
 * disjunction, if-then(-else), cut, `call/1`, `catch/3`, `not`/`\+`), timeouts, side effects (`assert`, `write`,
 * standard output, `findall/3`), and a battery of classic Prolog examples (search trees, backtracking, recursive
 * list processing, term ordering, etc.) drawn from [PrologStandardExampleTheories] and [TestingClauseTheories].
 *
 * Packaging it here lets every `Solver` implementation run the very same test cases without re-authoring or
 * duplicating them: a concrete `:solve-classic`/`:solve-streams`/`:solve-concurrent` module declares a `commonTest`
 * class implementing both this interface and its own `SolverFactory`, obtains a `prototype` via [TestSolver.prototype],
 * and delegates each overridden test method to it, e.g.:
 * ```kotlin
 * class TestClassicSolver : TestSolver, SolverFactory by ClassicSolverFactory {
 *     private val prototype = TestSolver.prototype(this)
 *
 *     @Test
 *     override fun testTrue() = prototype.testTrue()
 *     // ... one such override per test method declared here
 * }
 * ```
 * (see `TestClassicSolver` in `:solve-classic` for the full example). Because [callErrorSignature], [nafErrorSignature]
 * and [notErrorSignature] differ slightly across implementations (e.g. whether `not/1` or `\+/1` is used to report
 * an error), they are supplied as constructor parameters to [prototype] rather than hard-coded.
 */
interface TestSolver : SolverTest {
    companion object {
        /**
         * Creates a ready-to-use implementation of this test suite, to be delegated to by a concrete `Solver`
         * module's own test class.
         *
         * @param solverFactory produces the [Solver]/[MutableSolver] instances under test.
         * @param callErrorSignature the [Signature] reported in errors raised while resolving a goal through `call/1`.
         * @param nafErrorSignature the [Signature] reported in errors raised while resolving a goal through `\+/1`.
         * @param notErrorSignature the [Signature] reported in errors raised while resolving a goal through `not/1`.
         */
        fun prototype(
            solverFactory: SolverFactory,
            callErrorSignature: Signature = Signature("call", 1),
            nafErrorSignature: Signature = Signature("\\+", 1),
            notErrorSignature: Signature = Signature("not", 1),
        ): TestSolver = TestSolverImpl(solverFactory, callErrorSignature, nafErrorSignature, notErrorSignature)
    }

    /** The [Signature] expected in errors raised while resolving a goal through `call/1`. */
    val callErrorSignature: Signature

    /** The [Signature] expected in errors raised while resolving a goal through `\+/1` (negation as failure). */
    val nafErrorSignature: Signature

    /** The [Signature] expected in errors raised while resolving a goal through `not/1`. */
    val notErrorSignature: Signature

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
}
