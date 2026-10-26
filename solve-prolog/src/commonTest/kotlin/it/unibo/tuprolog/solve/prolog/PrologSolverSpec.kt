package it.unibo.tuprolog.solve.prolog

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolvePrologTest
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
import it.unibo.tuprolog.solve.nonVarContract
import it.unibo.tuprolog.solve.notProvableContract
import it.unibo.tuprolog.solve.notUnifyContract
import it.unibo.tuprolog.solve.numberCharsContract
import it.unibo.tuprolog.solve.numberCodesContract
import it.unibo.tuprolog.solve.numberContract
import it.unibo.tuprolog.solve.onceContract
import it.unibo.tuprolog.solve.orContract
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
import kotlin.test.assertIs

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
        include(numberCharsContract(PrologSolverFactory, disabled = setOf("number-chars/number-chars-decimal-number")))
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
    })
