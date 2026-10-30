package it.unibo.tuprolog.solve.libs.oop

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.dsl.logicProgramming
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.exception.error.RepresentationError
import it.unibo.tuprolog.solve.exception.error.SystemError
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.libs.oop.exceptions.OopRuntimeException
import it.unibo.tuprolog.solve.libs.oop.exceptions.TermToObjectConversionException
import it.unibo.tuprolog.solve.libs.oop.primitives.NewObject3
import it.unibo.tuprolog.solve.prolog.PrologSolverFactory
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class CreationSpec :
    FunSpec({
        val solverFactory = PrologSolverFactory

        fun testConstructorInvocation(
            case: TestDatum,
            case2Term: (TestDatum) -> Term,
            detectorType: KClass<*> = ConstructorOverloadDetector::class,
        ) = logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins(otherLibraries = Runtime.of(OOPLib))
            val query =
                NewObject3.functor(
                    TypeRef.of(detectorType),
                    logicListOf(case2Term(case)),
                    X,
                )
            val solutions = solver.solveList(query)
            assertTrue(solutions.size == 1)
            when (val solution = solutions.single()) {
                is Solution.Halt ->
                    when (val exception = solution.exception) {
                        is SystemError -> {
                            assertTrue(exception.cause is OopRuntimeException)
                            assertTrue(exception.cause?.cause is NullPointerException)
                        }
                        is RepresentationError -> {
                            assertTrue(exception.cause is TermToObjectConversionException)
                        }
                        else -> fail("Unexpected exception: $exception")
                    }
                is Solution.Yes ->
                    solver.solveOnce(query).substitution[X]?.let {
                        assertTrue(it is ObjectRef)
                        assertEquals(detectorType, it.`object`::class)
                        assertEquals<Pair<*, *>?>(
                            case.converted to case.type,
                            (it.`object` as? ConstructorOverloadDetector)?.args,
                        )
                    } ?: fail("Missing variable assignment in $solution: $X")
                else -> fail("Unexpected solution: $solution")
            }
        }

        fun castTerm(case: TestDatum): Term = Struct.of(OOP.CAST_OPERATOR, case.term, Atom.of(case.type.fullName))

        withTests(nameFn = { "most-proper-constructor-is-selected/${it.id}" }, Conversions.bestCases) { case ->
            testConstructorInvocation(case, TestDatum::term)
        }

        withTests(
            nameFn = { "constructor-can-be-selected-via-explicit-cast/${it.id}" },
            Conversions.explicitCases,
        ) { case ->
            testConstructorInvocation(case, ::castTerm)
        }

        withTests(nameFn = { "constructor-selection-may-fail/${it.id}" }, Conversions.cornerCases) { case ->
            testConstructorInvocation(case, ::castTerm)
        }
    })
