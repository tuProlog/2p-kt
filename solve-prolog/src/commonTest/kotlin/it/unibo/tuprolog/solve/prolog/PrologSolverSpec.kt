package it.unibo.tuprolog.solve.prolog

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
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
import it.unibo.tuprolog.solve.SolvePrologExpectations
import it.unibo.tuprolog.solve.SolverFactory
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
import it.unibo.tuprolog.solve.bindingRestorationContract
import it.unibo.tuprolog.solve.callContract
import it.unibo.tuprolog.solve.catchAndThrowContract
import it.unibo.tuprolog.solve.catchRestorationContract
import it.unibo.tuprolog.solve.charCodeContract
import it.unibo.tuprolog.solve.clauseContract
import it.unibo.tuprolog.solve.compoundContract
import it.unibo.tuprolog.solve.controlScopeContract
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
import it.unibo.tuprolog.solve.interceptionContract
import it.unibo.tuprolog.solve.isContract
import it.unibo.tuprolog.solve.lazinessContract
import it.unibo.tuprolog.solve.library.toRuntime
import it.unibo.tuprolog.solve.libraryOf
import it.unibo.tuprolog.solve.lifetimeContract
import it.unibo.tuprolog.solve.logicalUpdateViewContract
import it.unibo.tuprolog.solve.mutableSolverContract
import it.unibo.tuprolog.solve.nonVarContract
import it.unibo.tuprolog.solve.notProvableContract
import it.unibo.tuprolog.solve.notUnifyContract
import it.unibo.tuprolog.solve.numberCharsContract
import it.unibo.tuprolog.solve.numberCodesContract
import it.unibo.tuprolog.solve.numberContract
import it.unibo.tuprolog.solve.onceContract
import it.unibo.tuprolog.solve.opContract
import it.unibo.tuprolog.solve.orContract
import it.unibo.tuprolog.solve.primitive.Alternatives
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import it.unibo.tuprolog.solve.prolog.stdlib.DefaultBuiltins
import it.unibo.tuprolog.solve.recursionContract
import it.unibo.tuprolog.solve.repeatContract
import it.unibo.tuprolog.solve.retractContract
import it.unibo.tuprolog.solve.selectionPolicyContract
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
import it.unibo.tuprolog.solve.termSameContract
import it.unibo.tuprolog.solve.timeoutContract
import it.unibo.tuprolog.solve.trueContract
import it.unibo.tuprolog.solve.unifyContract
import it.unibo.tuprolog.solve.univContract
import it.unibo.tuprolog.solve.yes

/** The shared solver contracts of `:test-solve`, run against [PrologSolverFactory]. */
class PrologSolverSpec :
    FunSpec({
        include(staticFactoryContract(SolvePrologExpectations.expectations))
        include(prologSolverContracts(PrologSolverFactory))
    })

/** The shared solver contracts, and `:solve-prolog`'s own, against [factory]. */
internal fun prologSolverContracts(factory: SolverFactory) =
    funSpec {
        val ensureExecutable = Signature("ensure_executable", 1)
        include(unifyContract(factory))
        include(abolishContract(factory))
        include(andContract(factory))
        include(argContract(factory))
        include(arithContract(factory))
        include(assertAContract(factory))
        include(assertZContract(factory))
        include(atomContract(factory))
        include(atomCharsContract(factory))
        include(atomCodesContract(factory))
        include(atomConcatContract(factory))
        include(atomLengthContract(factory))
        include(atomicContract(factory))
        include(bagOfContract(factory))
        include(bigListContract(factory))
        include(callContract(factory, ensureExecutable))
        include(catchAndThrowContract(factory))
        include(charCodeContract(factory))
        include(clauseContract(factory))
        include(compoundContract(factory))
        include(copyTermContract(factory))
        include(customDataContract(factory))
        include(cutContract(factory))
        include(directivesContract(factory))
        include(failContract(factory))
        include(findAllContract(factory, ensureExecutable))
        include(flagsContract(factory))
        include(floatContract(factory))
        include(functorContract(factory))
        include(ifThenContract(factory))
        include(ifThenElseContract(factory))
        include(integerContract(factory))
        include(isContract(factory))
        include(nonVarContract(factory))
        include(notProvableContract(factory, ensureExecutable))
        include(notUnifyContract(factory))
        include(numberContract(factory))
        include(numberCharsContract(factory))
        include(numberCodesContract(factory))
        include(onceContract(factory, ensureExecutable))
        include(orContract(factory))
        include(recursionContract(factory))
        include(repeatContract(factory))
        include(retractContract(factory))
        include(setOfContract(factory))
        include(solutionPresentationContract(factory))
        include(solverContract(factory, ensureExecutable, ensureExecutable, ensureExecutable))
        include(solverConstructionContract<PrologSolver, MutablePrologSolver>(factory, DefaultBuiltins))
        include(stackTraceContract(factory))
        include(subAtomContract(factory))
        include(substitutionsContract(factory))
        include(tagsPreservationDuringResolutionContract(factory))
        include(termContract(factory))
        include(timeoutContract(factory))
        include(trueContract(factory))
        include(univContract(factory))
        include(opContract(factory))
        include(termSameContract(factory))

        withTests(
            nameFn = { "custom-data/non-atom-keys-are-type-errors/${it.replace('_', '-')}" },
            listOf("set_ephemeral", "set_durable", "set_persistent"),
        ) { functor ->
            val solution =
                factory
                    .solverWithDefaultBuiltins()
                    .solveOnce(Struct.of(functor, Integer.of(1), Integer.of(2)))
            withClue(
                "$functor: $solution",
            ) { (solution as? Solution.Halt)?.exception.shouldBeInstanceOf<TypeError>() }
        }

        test("op/priority-above-1200-is-domain-error") {
            val goal = Struct.of("op", Integer.of(1201), Atom.of("xfx"), Atom.of("foo"))
            val solution = factory.solverWithDefaultBuiltins().solveOnce(goal)
            val error =
                withClue("$solution") { (solution as? Solution.Halt)?.exception.shouldBeInstanceOf<DomainError>() }
            error.expectedDomain shouldBe DomainError.Expected.OPERATOR_PRIORITY
            error.culprit shouldBe Integer.of(1201)
        }

        test("variant-operators/comparisons-are-infix-operators") {
            val solver = factory.solverWithDefaultBuiltins()

            fun solveText(goal: String) = solver.solveOnce(Struct.parse(goal, solver.operators))
            solveText("f(a, X) =@= f(a, X)").isYes.shouldBeTrue()
            solveText("f(a) \\=@= f(b)").isYes.shouldBeTrue()
            solveText("X = (a =@= b), X =.. ['=@=', a, b]").isYes.shouldBeTrue()
        }

        // arithmetic results the host cannot represent must raise evaluation errors rather than crash the solver
        withTests(nameFn = { "arithmetic-errors/${it.id}" }, arithmeticErrorCases) { case ->
            val solution =
                factory
                    .solverWithDefaultBuiltins()
                    .solveOnce(Struct.of("is", Var.of("X"), case.expression))
            val halt = withClue("${case.expression}") { solution.shouldBeInstanceOf<Solution.Halt>() }
            withClue("${case.expression}") {
                (halt.exception.shouldBeInstanceOf<EvaluationError>().errorType) shouldBe
                    case.type
            }
        }

        test("arithmetic/power-to-negative-integer-is-fractional") {
            val goal = Struct.of("is", Var.of("X"), Struct.of("**", Integer.of(2), Integer.of(-1)))
            val solution =
                factory
                    .solverWithDefaultBuiltins()
                    .solveOnce(goal)
                    .shouldBeInstanceOf<Solution.Yes>()
            solution.substitution.values.single() shouldBe Real.of(0.5)
        }

        test("substitution-merging/substitutions-are-merged-after-primitives") {
            logicProgramming {
                val solver =
                    factory
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
        include(mutableSolverContract(factory))
        include(bindingRestorationContract(factory))
        include(catchRestorationContract(factory))
        include(interceptionContract(factory))
        include(selectionPolicyContract(factory))
        include(controlScopeContract(factory))
        include(lazinessContract(factory))
        include(lifetimeContract(factory))
        include(logicalUpdateViewContract(factory))
    }

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
        ArithmeticErrorCase("exp-overflows", Struct.of("exp", Real.of("1.0E10")), EvaluationError.Type.FLOAT_OVERFLOW),
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
            Struct.of("**", Real.of(10.0), Real.of("1.0E10")),
            EvaluationError.Type.FLOAT_OVERFLOW,
        ),
    )

/** Yields one solution per variable in the current substitution, binding it to an atom named after it. */
private object BindEveryVariableToItsName : UnaryPredicate<ExecutionContext>("primitive") {
    override val alternatives: Alternatives
        get() = Alternatives.LOOKAHEAD_SAFE

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
