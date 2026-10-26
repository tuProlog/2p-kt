package it.unibo.tuprolog.solve.problog

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.SolveProblogTest
import it.unibo.tuprolog.solve.abolishContract
import it.unibo.tuprolog.solve.andContract
import it.unibo.tuprolog.solve.argContract
import it.unibo.tuprolog.solve.arithContract
import it.unibo.tuprolog.solve.assertAContract
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
import it.unibo.tuprolog.solve.failContract
import it.unibo.tuprolog.solve.findAllContract
import it.unibo.tuprolog.solve.floatContract
import it.unibo.tuprolog.solve.functorContract
import it.unibo.tuprolog.solve.ifThenContract
import it.unibo.tuprolog.solve.ifThenElseContract
import it.unibo.tuprolog.solve.integerContract
import it.unibo.tuprolog.solve.isContract
import it.unibo.tuprolog.solve.nonVarContract
import it.unibo.tuprolog.solve.notProvableContract
import it.unibo.tuprolog.solve.notUnifyContract
import it.unibo.tuprolog.solve.numberCharsContract
import it.unibo.tuprolog.solve.numberCodesContract
import it.unibo.tuprolog.solve.numberContract
import it.unibo.tuprolog.solve.onceContract
import it.unibo.tuprolog.solve.orContract
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
import it.unibo.tuprolog.solve.termContract
import it.unibo.tuprolog.solve.trueContract
import it.unibo.tuprolog.solve.unifyContract

/** The shared solver contracts of `:test-solve`, run against [ProblogSolverFactory]. */
class ProblogSolverSpec :
    FunSpec({
        val ensureExecutable = Signature("ensure_executable", 1)
        include(staticFactoryContract(SolveProblogTest.expectations))
        include(
            solverConstructionContract<ProblogSolver, MutableProblogSolver>(
                ProblogSolverFactory,
                ProblogSolverFactory.defaultBuiltins,
                disabled =
                    setOf(
                        "solver-construction/creating-empty-solver",
                        "solver-construction/creating-custom-solver",
                        "solver-construction/creating-solver-with-default-builtins",
                        "solver-construction/creating-custom-solver-with-default-builtins",
                        "solver-construction/creating-empty-mutable-solver",
                        "solver-construction/creating-custom-mutable-solver",
                        "solver-construction/creating-mutable-solver-with-default-builtins",
                        "solver-construction/creating-custom-mutable-solver-with-default-builtins",
                        "solver-construction/building-empty-solver",
                        "solver-construction/building-custom-solver",
                        "solver-construction/building-solver-with-default-builtins",
                        "solver-construction/building-custom-solver-with-default-builtins",
                        "solver-construction/building-empty-mutable-solver",
                        "solver-construction/building-custom-mutable-solver",
                        "solver-construction/building-mutable-solver-with-default-builtins",
                        "solver-construction/building-custom-mutable-solver-with-default-builtins",
                    ),
            ),
        )
        include(abolishContract(ProblogSolverFactory))
        include(andContract(ProblogSolverFactory, disabled = setOf("and/no-foo-is-callable")))
        include(argContract(ProblogSolverFactory))
        include(arithContract(ProblogSolverFactory))
        include(assertAContract(ProblogSolverFactory, disabled = setOf("assert-a/assert-a-clause")))
        include(assertZContract(ProblogSolverFactory))
        include(atomContract(ProblogSolverFactory))
        include(atomCharsContract(ProblogSolverFactory))
        include(atomCodesContract(ProblogSolverFactory))
        include(atomConcatContract(ProblogSolverFactory))
        include(atomLengthContract(ProblogSolverFactory))
        include(atomicContract(ProblogSolverFactory))
        include(bagOfContract(ProblogSolverFactory))
        include(bigListContract(ProblogSolverFactory, disabled = setOf("big-list/big-list-generation")))
        include(callContract(ProblogSolverFactory, ensureExecutable))
        include(catchAndThrowContract(ProblogSolverFactory))
        include(charCodeContract(ProblogSolverFactory))
        include(clauseContract(ProblogSolverFactory, disabled = setOf("clause/clause-variables")))
        include(compoundContract(ProblogSolverFactory))
        include(copyTermContract(ProblogSolverFactory))
        include(customDataContract(ProblogSolverFactory))
        include(
            directivesContract(
                ProblogSolverFactory,
                disabled =
                    setOf(
                        "directives/dynamic1",
                        "directives/static1",
                        "directives/wrong-directives",
                        "directives/failing-initialization1",
                        "directives/failing-solve1",
                        "directives/exceptional-initialization1",
                        "directives/exceptional-solve1",
                        "directives/directive-loading-quickly",
                    ),
            ),
        )
        include(floatContract(ProblogSolverFactory))
        include(functorContract(ProblogSolverFactory))
        include(ifThenContract(ProblogSolverFactory))
        include(ifThenElseContract(ProblogSolverFactory))
        include(integerContract(ProblogSolverFactory))
        include(isContract(ProblogSolverFactory))
        include(nonVarContract(ProblogSolverFactory))
        include(notProvableContract(ProblogSolverFactory, ensureExecutable))
        include(notUnifyContract(ProblogSolverFactory))
        include(numberContract(ProblogSolverFactory))
        include(numberCharsContract(ProblogSolverFactory))
        include(numberCodesContract(ProblogSolverFactory))
        include(onceContract(ProblogSolverFactory, ensureExecutable))
        include(orContract(ProblogSolverFactory))
        include(cutContract(ProblogSolverFactory))
        include(
            failContract(
                ProblogSolverFactory,
                disabled = setOf("fail/undef-pred", "fail/set-flag-fail", "fail/set-flag-warning"),
            ),
        )
        include(
            findAllContract(
                ProblogSolverFactory,
                ensureExecutable,
                disabled = setOf("find-all/find-xin-goal", "find-all/find-xin-number"),
            ),
        )
        include(
            recursionContract(
                ProblogSolverFactory,
                disabled =
                    setOf(
                        "recursion/recursion1",
                        "recursion/recursion2",
                        "recursion/tail-recursion",
                        "recursion/non-tail-recursion",
                    ),
            ),
        )
        include(repeatContract(ProblogSolverFactory))
        include(retractContract(ProblogSolverFactory))
        include(setOfContract(ProblogSolverFactory))
        include(solutionPresentationContract(ProblogSolverFactory))
        include(
            solverContract(
                ProblogSolverFactory,
                ensureExecutable,
                ensureExecutable,
                ensureExecutable,
                disabled =
                    setOf(
                        "solver/unknown-flag1",
                        "solver/find-all",
                        "solver/assert",
                        "solver/assert-z",
                        "solver/assert-a",
                        "solver/conjunction-properties",
                        "solver/call-primitive-transparency",
                        "solver/catch-primitive",
                        "solver/catch-primitive-transparency",
                        "solver/not-modularity",
                        "solver/assert-rules",
                    ),
            ),
        )
        include(
            stackTraceContract(
                ProblogSolverFactory,
                disabled =
                    setOf(
                        "stack-trace/simple-stack-trace",
                        "stack-trace/double-stack-trace",
                        "stack-trace/triple-stack-trace",
                        "stack-trace/throw-is-not-in-stacktrace",
                    ),
            ),
        )
        include(subAtomContract(ProblogSolverFactory))
        include(termContract(ProblogSolverFactory))
        include(trueContract(ProblogSolverFactory))
        include(unifyContract(ProblogSolverFactory))
    })
