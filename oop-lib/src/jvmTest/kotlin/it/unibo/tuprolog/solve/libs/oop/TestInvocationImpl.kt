package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.dsl.logicProgramming
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.assertSolutionEquals
import it.unibo.tuprolog.solve.exception.error.RepresentationError
import it.unibo.tuprolog.solve.exception.error.SystemError
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.libs.oop.exceptions.OopRuntimeException
import it.unibo.tuprolog.solve.libs.oop.exceptions.TermToObjectConversionException
import it.unibo.tuprolog.solve.libs.oop.primitives.InvokeMethod
import it.unibo.tuprolog.solve.yes
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

abstract class TestInvocationImpl(protected val solverFactory: SolverFactory) : TestInvocation {

    internal abstract fun caseToResult(case: TestDatum): Term

    protected abstract val invoke: String

    private fun testMethodInvocation(
        cases: List<TestDatum>,
        detectorCreator: () -> OverloadDetector = OverloadDetector.Companion::create,
        refCreator: (OverloadDetector) -> Ref = ObjectRef.Companion::of,
        case2Term: (TestDatum) -> Term
    ) = logicProgramming {
        val solver = solverFactory.solverWithDefaultBuiltins(otherLibraries = Runtime.of(OOPLib))
        val obj = detectorCreator()
        val ref = refCreator(obj)
        for (case in cases) {
            val query = invoke(ref, "call"(case2Term(case)), R)
            val solutions = solver.solveList(query)
            assertTrue(solutions.size == 1)
            when (val solution = solutions.single()) {
                is Solution.Halt -> when (val exception = solution.exception) {
                    is SystemError -> {
                        assertTrue(exception.cause is OopRuntimeException)
                        assertTrue(exception.cause?.cause is NullPointerException)
                    }
                    is RepresentationError -> {
                        assertTrue(exception.cause is TermToObjectConversionException)
                    }
                    else -> fail("Unexpected exception: $exception")
                }
                is Solution.Yes -> {
                    assertSolutionEquals(
                        query.yes(R to caseToResult(case)),
                        solution
                    )
                }
                else -> fail("Unexpected solution: $solution")
            }
        }
        var query = InvokeMethod.functor(ref, "size", R)
        val expectedSize = cases.size - cases.filter { it.isFailed }.count()
        assertSolutionEquals(
            query.yes(R to Integer.of(expectedSize)),
            solver.solveOnce(query)
        )
        query = InvokeMethod.functor(ref, "toList", R)
        assertEquals(
            obj.toList(),
            solver.solveOnce(query).asYes()?.substitution?.get(R)?.let { it as ObjectRef }?.`object`
        )
        assertEquals(cases.filterNot { it.isFailed }.map { it.converted!! }, obj.toList())
        assertEquals(cases.filterNot { it.isFailed }.map { it.converted!! to it.type }, obj.recordings)
    }

    override fun mostProperOverloadIsSelectedWhenInvokingObjectRefMethod() {
        testMethodInvocation(Conversions.bestCases) { it.term }
    }

    override fun overloadCanBeSelectedViaExplicitCastWhenInvokingObjectRefMethod() {
        testMethodInvocation(Conversions.explicitCases) {
            Struct.of(OOP.CAST_OPERATOR, it.term, Atom.of(it.type.fullName))
        }
    }

    override fun overloadSelectionMayFailWhenInvokingObjectRefMethod() {
        testMethodInvocation(Conversions.cornerCases) {
            Struct.of(OOP.CAST_OPERATOR, it.term, Atom.of(it.type.fullName))
        }
    }

    override fun mostProperOverloadIsSelectedWhenInvokingTypeRefMethod() {
        testMethodInvocation(
            Conversions.bestCases,
            detectorCreator = OverloadDetectorObject::refresh,
            refCreator = { TypeRef.of(OverloadDetectorObject::class) }
        ) { it.term }
    }

    override fun overloadCanBeSelectedViaExplicitCastWhenInvokingTypeRefMethod() {
        testMethodInvocation(
            Conversions.explicitCases,
            detectorCreator = OverloadDetectorObject::refresh,
            refCreator = { TypeRef.of(OverloadDetectorObject::class) }
        ) {
            Struct.of(OOP.CAST_OPERATOR, it.term, Atom.of(it.type.fullName))
        }
    }

    override fun overloadSelectionMayFailWhenInvokingTypeRefMethod() {
        testMethodInvocation(
            Conversions.cornerCases,
            detectorCreator = OverloadDetectorObject::refresh,
            refCreator = { TypeRef.of(OverloadDetectorObject::class) }
        ) {
            Struct.of(OOP.CAST_OPERATOR, it.term, Atom.of(it.type.fullName))
        }
    }
}
