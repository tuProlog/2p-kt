package it.unibo.tuprolog.solve.concurrent

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.solve.goalQueryContract
import it.unibo.tuprolog.solve.metaResolutionContract
import it.unibo.tuprolog.solve.metaVariableContract
import it.unibo.tuprolog.solve.patternDataContract

/**
 * The concurrent solver's own conformance cases (multiset semantics), apart from `:test-solve`'s contracts, and the
 * contracts of `:test-solve` that solve queries, which the concurrent solver only does on the JVM.
 */
class ConcurrentSolverConformanceSpec :
    FunSpec({
        include(concurrentAndContract())
        include(concurrentArgContract())
        include(concurrentArithContract())
        include(concurrentAtomContract())
        include(concurrentAtomCharsContract())
        include(concurrentAtomCodesContract())
        include(concurrentAtomConcatContract())
        include(concurrentAtomLengthContract())
        include(concurrentAtomicContract())
        include(concurrentBagOfContract())
        include(concurrentBigListContract())
        include(concurrentCallContract())
        include(concurrentCatchAndThrowContract())
        include(concurrentCharCodeContract())
        include(concurrentClauseContract())
        include(concurrentCompoundContract())
        include(concurrentCopyTermContract())
        include(concurrentCustomDataContract())
        include(concurrentFailContract())
        include(concurrentFindAllContract())
        include(concurrentFlagsContract())
        include(concurrentFloatContract())
        include(concurrentFunctorContract())
        include(concurrentIfThenContract())
        include(concurrentIfThenElseContract())
        include(concurrentIntegerContract())
        include(concurrentIsContract())
        include(concurrentNonVarContract())
        include(concurrentNotProvableContract())
        include(concurrentNotUnifyContract())
        include(concurrentNumberContract())
        include(concurrentNumberCharsContract())
        include(concurrentNumberCodesContract())
        include(concurrentOrContract())
        include(concurrentRepeatContract())
        include(concurrentRetractContract())
        include(concurrentSetOfContract())
        include(concurrentSolutionPresentationContract())
        include(concurrentSolverContract())
        include(concurrentStackTraceContract())
        include(concurrentSubAtomContract())
        include(concurrentTermContract())
        include(concurrentTrueContract())
        include(concurrentUnifyContract())
        include(goalQueryContract(ConcurrentSolverFactory))
        include(
            metaResolutionContract(
                ConcurrentSolverFactory,
                // solutions come in any order, and meta-resolution policies are not supported
                disabled =
                    setOf(
                        "meta-resolution/concrete-call-selects-metarules-in-order",
                        "meta-resolution/enumerate-user-predicates-of-the-arity",
                        "meta-resolution/enumerate-respects-cut-and-negation",
                        "meta-resolution/enumerate-sees-dynamic-predicates",
                        "meta-resolution/enumerate-chain-metarule",
                        "meta-resolution/custom-policy",
                    ),
            ),
        )
        include(patternDataContract(ConcurrentSolverFactory))
        include(
            metaVariableContract(
                ConcurrentSolverFactory,
                // branches run concurrently: a later one may answer, or fail, first; and there are no counters
                disabled =
                    setOf(
                        "meta-variables/mr-87/solutions-computed-lazily",
                        "meta-variables/mr-88/early-stop-tries-few-candidates",
                        // in the order solutions come in
                        "meta-variables/built-ins/findall-collects-every-answer",
                    ),
                ordered = false,
            ),
        )
    })
