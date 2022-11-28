package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.dsl.logicProgramming
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.exception.error.RepresentationError
import it.unibo.tuprolog.solve.exception.error.SystemError
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.libs.oop.exceptions.OopRuntimeException
import it.unibo.tuprolog.solve.libs.oop.exceptions.TermToObjectConversionException
import it.unibo.tuprolog.solve.libs.oop.primitives.NewObject3
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class TestCreationImpl(protected val solverFactory: SolverFactory) : TestCreation {
    private fun testConstructorInvocation(
        cases: List<TestDatum>,
        detectorType: KClass<*> = ConstructorOverloadDetector::class,
        case2Term: (TestDatum) -> Term
    ) = logicProgramming {
        val solver = solverFactory.solverWithDefaultBuiltins(otherLibraries = Runtime.of(OOPLib))
        for (case in cases) {
            val query = NewObject3.functor(TypeRef.of(detectorType), listOf(case2Term(case)), X)
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
                is Solution.Yes -> solver.solveOnce(query).substitution[X]?.let {
                    assertTrue(it is ObjectRef)
                    assertEquals(detectorType, it.`object`::class)
                    assertEquals<Pair<*, *>?>(
                        case.converted to case.type,
                        (it.`object` as? ConstructorOverloadDetector)?.args
                    )
                } ?: fail("Missing variable assignment in $solution: $X")
                else -> fail("Unexpected solution: $solution")
            }
        }
    }

    override fun mostProperConstructorIsSelectedWhenInstantiatingTypeRef() {
        testConstructorInvocation(Conversions.bestCases, ConstructorOverloadDetector::class) { it.term }
    }

    override fun constructorCanBeSelectedViaExplicitCastWhenInstantiatingTypeRef() {
        testConstructorInvocation(Conversions.explicitCases, ConstructorOverloadDetector::class) {
            Struct.of(OOP.CAST_OPERATOR, it.term, Atom.of(it.type.fullName))
        }
    }

    override fun constructorSelectionMayFailWhenInstantiatingTypeRef() {
        testConstructorInvocation(Conversions.cornerCases, ConstructorOverloadDetector::class) {
            Struct.of(OOP.CAST_OPERATOR, it.term, Atom.of(it.type.fullName))
        }
    }
}
