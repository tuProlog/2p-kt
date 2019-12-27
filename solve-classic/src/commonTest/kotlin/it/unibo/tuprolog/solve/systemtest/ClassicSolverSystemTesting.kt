package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.libraries.stdlib.DefaultBuiltins
import it.unibo.tuprolog.solve.*
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlin.test.Test

class ClassicSolverSystemTesting : SolverFactory {

    private val prototype = SolverTestPrototype(this)

    override val defaultLibraries: Libraries = Libraries(DefaultBuiltins)

    override fun solverOf(
        libraries: Libraries,
        flags: PrologFlags,
        staticKB: ClauseDatabase,
        dynamicKB: ClauseDatabase
    ): Solver = ClassicSolver(libraries, flags, staticKB, dynamicKB)

    @Test
//    @Ignore
    fun entryPointForManualTests() {
        prolog {
            val solver = Solver.classic(
                libraries = defaultLibraries,
                staticKB = theoryOf(
                    fact { "a"(2) },
                    fact { "a"(1) },
                    fact { "b"(1) },
                    fact { "c"(2) }
//                    clause { "a"("X") `if` "b"("X") },
//                    clause { "a"(6) },
//                    clause { "b"("X") `if` ("c"("X") and "d"("X")) },
//                    clause { "b"(4) `if` "!" },
//                    clause { "b"(5) },
//                    clause { "c"(1) },
//                    clause { "c"(2) `if` "!" },
//                    clause { "c"(3) },
//                    clause { "d"(2) },
//                    clause { "d"(3) }
                )
            )

            solver.solve("a"("X") then "b"("X") or "c"("X"), 200).forEach { sol ->
                println(sol)
            }
            solver.solve("a"("X") and ("X" lowerThan 2) then "b"("X") or "c"("X"), 200).forEach { sol ->
                println(sol)
            }
            solver.solve("a"("X") and "!" and ("X" lowerThan 2) then "b"("X") or "c"("X"), 200).forEach { sol ->
                println(sol)
            }
//            solver.solve(tupleOf(true, "X"), 200).forEach { sol ->
//                println(sol)
//                if (sol is Solution.Halt) {
//                    sol.exception.prologStackTrace.forEach {
//                        println("\t$it")
//                    }
//                }
//            }
//            solver.solve(tupleOf(true, 1), 200).forEach {sol ->
//                println(sol)
//            }
//            solver.solve("call"("a"("X"))).forEach {
//                println(it)
//            }
//            solver.solve("call"("X")).forEach {
//                println(it)
//            }
//            solver.solve("!" and false or true).forEach {
//                println(it)
//            }
//            solver.solve("call"("!") and false or true).forEach {
//                println(it)
//            }
//            solver.solve("a"("X") and "!").forEach {
//                println(it)
//            }
//            solver.solve("a"("X") and "call"("!")).forEach {
//                println(it)
//            }
//            solver.solve("call"("Z" `=` "!" and "a"("X") and "Z")).forEach {
//                println(it)
//            }
//            solver.solve("Z" `=` "!" and "call"("Z" `=` "!" and ("a"("X") and "Z"))).forEach {
//                println(it)
//            }
        }
    }

    @Test
    fun testIfThen2() {
        prototype.testIfThen2()
    }

    @Test
    fun testIfThen1() {
        prototype.testIfThen1()
    }

    @Test
    fun testIfThenElse1() {
        prototype.testIfThenElse1()
    }

    @Test
    fun testIfThenElse2() {
        prototype.testIfThenElse1()
    }

    @Test
    fun testTimeout1() {
        prototype.testTimeout1()
    }

    @Test
    fun testTimeout2() {
        prototype.testTimeout2()
    }

    @Test
    fun testTimeout3() {
        prototype.testTimeout3()
    }

    @Test
    fun testTimeout4() {
        prototype.testTimeout4()
    }

    @Test
    fun testTrue() {
        prototype.testTrue()
    }

    @Test
    fun testUnification() {
        prototype.testUnification()
    }

    @Test
//    @Ignore // TODO: 07/11/2019 Substitution unused variable cleaning needed to pass this test (issue #52)
    fun testSimpleCutAlternatives() {
        prototype.testSimpleCutAlternatives()
    }

    @Test
//    @Ignore // TODO: (issue #52)
    fun testCutAndConjunction() {
        prototype.testCutAndConjunction()
    }

    @Test
//    @Ignore // TODO: (issue #52)
    fun testCutConjunctionAndBacktracking() {
        prototype.testCutConjunctionAndBacktracking()
    }

    @Test
    fun testMaxDurationParameterAndTimeOutException() {
        prototype.testMaxDurationParameterAndTimeOutException()
    }

    @Test
//    @Ignore // TODO (issue #52)
    fun testPrologStandardSearchTreeExample() {
        prototype.testPrologStandardSearchTreeExample()
    }

    @Test
//    @Ignore // TODO (issue #52)
    fun testPrologStandardSearchTreeWithCutExample() {
        prototype.testPrologStandardSearchTreeWithCutExample()
    }

    @Test
//    @Ignore // TODO (issue #52)
    fun testBacktrackingWithCustomReverseListImplementation() {
        prototype.testBacktrackingWithCustomReverseListImplementation()
    }

    @Test
//    @Ignore // TODO (issue #52)
    fun testWithPrologStandardConjunctionExamples() {
        prototype.testWithPrologStandardConjunctionExamples()
    }

    @Test
//    @Ignore // TODO (issue #52)
    fun testConjunctionProperties() {
        prototype.testConjunctionProperties()
    }

    @Test
//    @Ignore // TODO (issue #52)
    fun testCallPrimitive() {
        prototype.testCallPrimitive()
    }

    @Test
//    @Ignore // TODO (issue #52)
    fun testCallPrimitiveTransparency() {
        prototype.testCallPrimitiveTransparency()
    }

    @Test
//    @Ignore // TODO (issue #52)
    fun testCatchPrimitive() {
        prototype.testCatchPrimitive()
    }

    @Test
//    @Ignore // TODO (issue #52)
    fun testCatchPrimitiveTransparency() {
        prototype.testCatchPrimitiveTransparency()
    }

    @Test
    fun testHaltPrimitive() {
        prototype.testHaltPrimitive()
    }

    @Test
//    @Ignore // TODO (issue #52)
    fun testNotPrimitive() {
        prototype.testNotPrimitive()
    }

    @Test
//    @Ignore // TODO (issue #52)
    fun testNotModularity() {
        prototype.testNotModularity()
    }

    @Test
//    @Ignore // TODO (issue #52)
    fun testIfThenRule() {
        prototype.testIfThenRule()
    }

    @Test
//    @Ignore // TODO (issue #52)
    fun testIfTheElseRule() {
        prototype.testIfThenElseRule()
    }

    @Test
    fun testConjunction() {
        prototype.testConjunction()
    }

    @Test
    fun testConjunctionWithUnification() {
        prototype.testConjunctionWithUnification()
    }

    @Test
    fun testBuiltinApi() {
        prototype.testBuiltinApi()
    }

    @Test
    fun testDisjunction() {
        prototype.testDisjunction()
    }

    @Test
    fun testFailure() {
        prototype.testFailure()
    }

    @Test
    fun testDisjunctionWithUnification() {
        prototype.testDisjunctionWithUnification()
    }

    @Test
    fun testConjunctionOfConjunctions() {
        prototype.testConjunctionOfConjunctions()
    }

    @Test
    fun testMember() {
        prototype.testMember()
    }

    @Test
    fun testBasicBacktracking1() {
        prototype.testBasicBacktracking1()
    }

    @Test
    fun testBasicBacktracking2() {
        prototype.testBasicBacktracking2()
    }

    @Test
    fun testBasicBacktracking3() {
        prototype.testBasicBacktracking3()
    }

    @Test
    fun testBasicBacktracking4() {
        prototype.testBasicBacktracking4()
    }
}