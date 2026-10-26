package it.unibo.tuprolog.solve.prolog

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.parsing.parse
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolvePrologTest
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.abolishContract
import it.unibo.tuprolog.solve.andContract
import it.unibo.tuprolog.solve.argContract
import it.unibo.tuprolog.solve.arithContract
import it.unibo.tuprolog.solve.assertAContract
import it.unibo.tuprolog.solve.assertSolutionEquals
import it.unibo.tuprolog.solve.assertZContract
import it.unibo.tuprolog.solve.atomCharsContract
import it.unibo.tuprolog.solve.atomCodesContract
import it.unibo.tuprolog.solve.atomConcatContract
import it.unibo.tuprolog.solve.atomContract
import it.unibo.tuprolog.solve.atomLengthContract
import it.unibo.tuprolog.solve.atomicContract
import it.unibo.tuprolog.solve.bagOfContract
import it.unibo.tuprolog.solve.bigListContract
import it.unibo.tuprolog.solve.callContract
import it.unibo.tuprolog.solve.catchAndThrowContract
import it.unibo.tuprolog.solve.charCodeContract
import it.unibo.tuprolog.solve.clauseContract
import it.unibo.tuprolog.solve.compoundContract
import it.unibo.tuprolog.solve.copyTermContract
import it.unibo.tuprolog.solve.customDataContract
import it.unibo.tuprolog.solve.cutContract
import it.unibo.tuprolog.solve.directivesContract
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.EvaluationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.failContract
import it.unibo.tuprolog.solve.findAllContract
import it.unibo.tuprolog.solve.flagsContract
import it.unibo.tuprolog.solve.floatContract
import it.unibo.tuprolog.solve.functorContract
import it.unibo.tuprolog.solve.ifThenContract
import it.unibo.tuprolog.solve.ifThenElseContract
import it.unibo.tuprolog.solve.integerContract
import it.unibo.tuprolog.solve.isContract
import it.unibo.tuprolog.solve.library.toRuntime
import it.unibo.tuprolog.solve.libraryOf
import it.unibo.tuprolog.solve.nonVarContract
import it.unibo.tuprolog.solve.notProvableContract
import it.unibo.tuprolog.solve.notUnifyContract
import it.unibo.tuprolog.solve.numberCharsContract
import it.unibo.tuprolog.solve.numberCodesContract
import it.unibo.tuprolog.solve.numberContract
import it.unibo.tuprolog.solve.onceContract
import it.unibo.tuprolog.solve.orContract
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import it.unibo.tuprolog.solve.prolog.stdlib.DefaultBuiltins
import it.unibo.tuprolog.solve.recursionContract
import it.unibo.tuprolog.solve.repeatContract
import it.unibo.tuprolog.solve.retractContract
import it.unibo.tuprolog.solve.setOfContract
import it.unibo.tuprolog.solve.solutionPresentationContract
import it.unibo.tuprolog.solve.solverConstructionContract
import it.unibo.tuprolog.solve.solverContract
import it.unibo.tuprolog.solve.stackTraceContract
import it.unibo.tuprolog.solve.staticFactoryContract
import it.unibo.tuprolog.solve.subAtomContract
import it.unibo.tuprolog.solve.substitutionsContract
import it.unibo.tuprolog.solve.tagsPreservationDuringResolutionContract
import it.unibo.tuprolog.solve.termContract
import it.unibo.tuprolog.solve.timeoutContract
import it.unibo.tuprolog.solve.trueContract
import it.unibo.tuprolog.solve.unifyContract
import it.unibo.tuprolog.solve.yes
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

/** The shared solver contracts of `:test-solve`, run against [PrologSolverFactory]. */
class PrologSolverSpec :
    FunSpec({
        val ensureExecutable = Signature("ensure_executable", 1)
        include(unifyContract(PrologSolverFactory))
        include(abolishContract(PrologSolverFactory))
        include(andContract(PrologSolverFactory))
        include(argContract(PrologSolverFactory))
        include(arithContract(PrologSolverFactory))
        include(assertAContract(PrologSolverFactory))
        include(assertZContract(PrologSolverFactory))
        include(atomContract(PrologSolverFactory))
        include(atomCharsContract(PrologSolverFactory))
        include(atomCodesContract(PrologSolverFactory))
        include(atomConcatContract(PrologSolverFactory))
        include(atomLengthContract(PrologSolverFactory))
        include(atomicContract(PrologSolverFactory))
        include(bagOfContract(PrologSolverFactory))
        include(bigListContract(PrologSolverFactory))
        include(callContract(PrologSolverFactory, ensureExecutable))
        include(catchAndThrowContract(PrologSolverFactory))
        include(charCodeContract(PrologSolverFactory))
        include(clauseContract(PrologSolverFactory))
        include(compoundContract(PrologSolverFactory))
        include(copyTermContract(PrologSolverFactory))
        include(customDataContract(PrologSolverFactory))
        include(cutContract(PrologSolverFactory))
        include(directivesContract(PrologSolverFactory))
        include(failContract(PrologSolverFactory))
        include(findAllContract(PrologSolverFactory, ensureExecutable))
        include(flagsContract(PrologSolverFactory))
        include(floatContract(PrologSolverFactory))
        include(functorContract(PrologSolverFactory))
        include(ifThenContract(PrologSolverFactory))
        include(ifThenElseContract(PrologSolverFactory))
        include(integerContract(PrologSolverFactory))
        include(isContract(PrologSolverFactory))
        include(nonVarContract(PrologSolverFactory))
        include(notProvableContract(PrologSolverFactory, ensureExecutable))
        include(notUnifyContract(PrologSolverFactory))
        include(numberContract(PrologSolverFactory))
        include(numberCharsContract(PrologSolverFactory))
        include(numberCodesContract(PrologSolverFactory))
        include(onceContract(PrologSolverFactory, ensureExecutable))
        include(orContract(PrologSolverFactory))
        include(recursionContract(PrologSolverFactory))
        include(repeatContract(PrologSolverFactory))
        include(retractContract(PrologSolverFactory))
        include(setOfContract(PrologSolverFactory))
        include(solutionPresentationContract(PrologSolverFactory))
        include(solverContract(PrologSolverFactory, ensureExecutable, ensureExecutable, ensureExecutable))
        include(solverConstructionContract<PrologSolver, MutablePrologSolver>(PrologSolverFactory, DefaultBuiltins))
        include(stackTraceContract(PrologSolverFactory))
        include(subAtomContract(PrologSolverFactory))
        include(substitutionsContract(PrologSolverFactory))
        include(tagsPreservationDuringResolutionContract(PrologSolverFactory))
        include(termContract(PrologSolverFactory))
        include(timeoutContract(PrologSolverFactory))
        include(trueContract(PrologSolverFactory))
        include(staticFactoryContract(SolvePrologTest.expectations))

        test("custom-data/non-atom-keys-are-type-errors") {
            for (functor in listOf("set_ephemeral", "set_durable", "set_persistent")) {
                val solution =
                    PrologSolverFactory
                        .solverWithDefaultBuiltins()
                        .solveOnce(Struct.of(functor, Integer.of(1), Integer.of(2)))
                assertIs<TypeError>((solution as? Solution.Halt)?.exception, "$functor: $solution")
            }
        }

        test("op/priority-above-1200-is-domain-error") {
            val goal = Struct.of("op", Integer.of(1201), Atom.of("xfx"), Atom.of("foo"))
            val solution = Solver.prolog.solverWithDefaultBuiltins().solveOnce(goal)
            val error = assertIs<DomainError>((solution as? Solution.Halt)?.exception, "$solution")
            assertEquals(DomainError.Expected.OPERATOR_PRIORITY, error.expectedDomain)
            assertEquals(Integer.of(1201), error.culprit)
        }

        test("variant-operators/comparisons-are-infix-operators") {
            val solver = Solver.prolog.solverWithDefaultBuiltins()

            fun solveText(goal: String) = solver.solveOnce(Struct.parse(goal, solver.operators))
            assertTrue(solveText("f(a, X) =@= f(a, X)").isYes)
            assertTrue(solveText("f(a) \\=@= f(b)").isYes)
            assertTrue(solveText("X = (a =@= b), X =.. ['=@=', a, b]").isYes)
        }

        // arithmetic results the host cannot represent must raise evaluation errors rather than crash the solver
        withTests(nameFn = { "arithmetic-errors/${it.id}" }, arithmeticErrorCases) { case ->
            val solution =
                Solver.prolog
                    .solverWithDefaultBuiltins()
                    .solveOnce(Struct.of("is", Var.of("X"), case.expression))
            val halt = assertIs<Solution.Halt>(solution, "${case.expression}")
            assertEquals(case.type, assertIs<EvaluationError>(halt.exception).errorType, "${case.expression}")
        }

        test("arithmetic/power-to-negative-integer-is-fractional") {
            val goal = Struct.of("is", Var.of("X"), Struct.of("**", Integer.of(2), Integer.of(-1)))
            val solution = assertIs<Solution.Yes>(Solver.prolog.solverWithDefaultBuiltins().solveOnce(goal))
            assertEquals(Real.of(0.5), solution.substitution.values.single())
        }

        test("substitution-merging/substitutions-are-merged-after-primitives") {
            logicProgramming {
                val solver =
                    PrologSolverFactory
                        .newBuilder()
                        .staticKb(
                            theoryOf(
                                rule { "a"(X) impliedBy "b"(X) },
                                rule { "b"(Y) impliedBy "c"(Y) },
                                rule { "c"(Z) impliedBy "primitive"(Z) },
                            ),
                        ).noBuiltins()
                        .runtime(libraryOf("test", BindEveryVariableToItsName).toRuntime())
                        .build()
                val goal = "a"(A)
                assertSolutionEquals(
                    listOf(
                        goal.yes(A to atomOf("A")),
                        goal.yes(A to atomOf("X")),
                        goal.yes(A to atomOf("Y")),
                        goal.yes(A to atomOf("Z")),
                    ),
                    solver.solveList(goal),
                )
            }
        }
    })

private data class ArithmeticErrorCase(
    val id: String,
    val expression: Term,
    val type: EvaluationError.Type,
)

private val arithmeticErrorCases =
    listOf(
        ArithmeticErrorCase("log-of-zero", Struct.of("log", Integer.of(0)), EvaluationError.Type.UNDEFINED),
        ArithmeticErrorCase("log-of-zero-real", Struct.of("log", Real.of(0.0)), EvaluationError.Type.UNDEFINED),
        ArithmeticErrorCase("log-of-negative", Struct.of("log", Integer.of(-1)), EvaluationError.Type.UNDEFINED),
        ArithmeticErrorCase("sqrt-of-negative", Struct.of("sqrt", Integer.of(-1)), EvaluationError.Type.UNDEFINED),
        ArithmeticErrorCase("sqrt-of-negative-real", Struct.of("sqrt", Real.of(-0.5)), EvaluationError.Type.UNDEFINED),
        ArithmeticErrorCase("exp-overflows", Struct.of("exp", Integer.of(1000)), EvaluationError.Type.FLOAT_OVERFLOW),
        ArithmeticErrorCase(
            "power-of-zero-to-negative",
            Struct.of("**", Integer.of(0), Integer.of(-1)),
            EvaluationError.Type.UNDEFINED,
        ),
        ArithmeticErrorCase(
            "power-of-zero-to-negative-real",
            Struct.of("**", Real.of(0.0), Real.of(-1.5)),
            EvaluationError.Type.UNDEFINED,
        ),
        ArithmeticErrorCase(
            "power-of-negative-to-fractional",
            Struct.of("**", Integer.of(-8), Real.of(0.5)),
            EvaluationError.Type.UNDEFINED,
        ),
        ArithmeticErrorCase(
            "power-overflows",
            Struct.of("**", Real.of(10.0), Real.of(400.0)),
            EvaluationError.Type.FLOAT_OVERFLOW,
        ),
    )

/** Yields one solution per variable in the current substitution, binding it to an atom named after it. */
private object BindEveryVariableToItsName : UnaryPredicate<ExecutionContext>("primitive") {
    override fun Solve.Request<ExecutionContext>.computeAll(first: Term): Sequence<Solve.Response> =
        sequence {
            val allVariables =
                context.substitution.let {
                    it.keys.toSet() + it.values.filterIsInstance<Var>().toSet()
                }
            for (x in allVariables.sortedBy { it.name }) {
                yield(replySuccess(Substitution.unifier(x to Atom.of(x.name))))
            }
        }
}
