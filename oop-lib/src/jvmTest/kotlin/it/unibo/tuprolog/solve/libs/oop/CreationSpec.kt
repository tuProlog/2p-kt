package it.unibo.tuprolog.solve.libs.oop

import io.kotest.assertions.fail
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
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
            solutions.size shouldBe 1
            when (val solution = solutions.single()) {
                is Solution.Halt ->
                    when (val exception = solution.exception) {
                        is SystemError -> {
                            exception.cause.shouldBeInstanceOf<OopRuntimeException>()
                            exception.cause?.cause.shouldBeInstanceOf<NullPointerException>()
                        }
                        is RepresentationError -> {
                            exception.cause.shouldBeInstanceOf<TermToObjectConversionException>()
                        }
                        else -> fail("Unexpected exception: $exception")
                    }
                is Solution.Yes ->
                    solver.solveOnce(query).substitution[X]?.let {
                        it.shouldBeInstanceOf<ObjectRef>()
                        (it.`object`::class) shouldBe detectorType
                        (it.`object` as? ConstructorOverloadDetector)?.args.shouldBe<Pair<*, *>?>(
                            case.converted to case.type,
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
