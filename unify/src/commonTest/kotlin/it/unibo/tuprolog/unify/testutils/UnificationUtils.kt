package it.unibo.tuprolog.unify.testutils

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.unify.Equation
import it.unibo.tuprolog.unify.Unification
import it.unibo.tuprolog.unify.`=`
import kotlin.test.assertEquals
import it.unibo.tuprolog.core.List.Companion as LogicList
import kotlin.collections.List as KtList

/** Represents the Unification Mgu function */
private typealias MguStrategy<T1, T2> = (T1, T2) -> Substitution

/** Represents the Unification Match function */
private typealias MatchStrategy<T1, T2> = (T1, T2) -> Boolean

/** Represents teh Unification Unify function */
private typealias UnifyStrategy<T1, T2> = (T1, T2) -> Term?


/** A typealias to simplify method signature writing */
private typealias CorrectnessMap<T1, T2> = Map<Equation<T1, T2>, Triple<Substitution, Boolean, Term?>>


/**
 * Utils singleton for testing [Unification]
 *
 * @author Enrico
 */
internal object UnificationUtils {

    private val aAtom = Atom.of("a")
    private val bAtom = Atom.of("b")
    private val xVar = Var.of("X")
    private val yVar = Var.of("Y")

    private val failedResultsTriple: Triple<Substitution, Boolean, Term?> =
            Triple(Substitution.failed(), false, null)

    /**
     * Contains a mapping between equations that should have success unifying and a `Triple(mgu, isMatching, unifiedTerm)`
     */
    internal val successfulUnifications by lazy {
        mapOf(
                *EquationUtils.allIdentityEquations.map { (lhs, rhs) ->
                    (lhs `=` rhs) to Triple(Substitution.empty(), true, lhs)
                }.toTypedArray(),

                (aAtom `=` aAtom) to Triple(Substitution.empty(), true, aAtom),
                (xVar `=` xVar) to Triple(Substitution.empty(), true, xVar),
                (aAtom `=` xVar) to Triple(Substitution.of(xVar, aAtom), true, aAtom),
                (xVar `=` yVar) to Triple(Substitution.of(xVar, yVar), true, yVar),
                with(Scope.empty()) { (xVar `=` varOf("X")) to Triple(Substitution.of(xVar, varOf("X")), true, varOf("X")) },
                Var.anonymous().let { (xVar `=` it) to Triple(Substitution.of(xVar, it), true, it) },
                (Struct.of("f", aAtom, xVar) `=` Struct.of("f", aAtom, bAtom)) to
                        Triple(Substitution.of(xVar, bAtom), true, Struct.of("f", aAtom, bAtom)),
                (Struct.of("f", xVar) `=` Struct.of("f", yVar)) to
                        Triple(Substitution.of(xVar, yVar), true, Struct.of("f", yVar)),
                (Struct.of("f", Struct.of("g", xVar)) `=` Struct.of("f", yVar)) to
                        Triple(
                                Substitution.of(yVar, Struct.of("g", xVar)),
                                true,
                                Struct.of("f", Struct.of("g", xVar))
                        ),
                (Struct.of("f", Struct.of("g", xVar), xVar) `=` Struct.of("f", yVar, aAtom)) to
                        Triple(
                                Substitution.of(yVar to Struct.of("g", aAtom), xVar to aAtom),
                                true,
                                Struct.of("f", Struct.of("g", aAtom), aAtom)
                        ),
                with(Scope.empty()) {
                    Rule.of(Struct.of("f", aAtom, Struct.of("b", varOf("X"))), bAtom) `=`
                            Rule.of(Struct.of("f", varOf("A"), varOf("B")), varOf("C")) to
                            Triple(
                                    Substitution.of(varOf("A") to aAtom,
                                            varOf("B") to Struct.of("b", varOf("X")),
                                            varOf("C") to bAtom),
                                    true,
                                    Rule.of(Struct.of("f", aAtom, Struct.of("b", varOf("X"))), bAtom)
                            )
                },
                with(Scope.empty()) {
                    Indicator.of("ciao", 2) `=` Indicator.of(varOf("A"), varOf("B")) to
                            Triple(
                                    Substitution.of(varOf("A") to Atom.of("ciao"),
                                            varOf("B") to Integer.of(2)),
                                    true,
                                    Indicator.of("ciao", 2)
                            )
                }

        )
    }

    /**
     * Contains a mapping between faulty equations and their failed result Triple(mgu, isMatching, unifiedTerm)
     */
    internal val failedUnifications by lazy {
        mapOf(
                *EquationUtils.allContradictionEquations.map { (lhs, rhs) ->
                    (lhs `=` rhs) to failedResultsTriple
                }.toTypedArray(),

                (aAtom `=` bAtom) to failedResultsTriple,
                (Struct.of("f", aAtom) `=` Struct.of("g", aAtom)) to failedResultsTriple,
                (Struct.of("f", xVar) `=` Struct.of("g", yVar)) to failedResultsTriple,
                (Struct.of("f", xVar) `=` Struct.of("f", yVar, Var.of("Z"))) to failedResultsTriple
        )
    }

    /**
     * Contains faulty equations that will be rejected by the occur-check
     */
    internal val occurCheckFailedUnifications by lazy {
        mapOf(
                (xVar `=` Struct.of("f", xVar)) to failedResultsTriple,
                (xVar `=` LogicList.from(listOf(aAtom, bAtom), xVar)) to failedResultsTriple
        )
    }

    /**
     * Contains successful sequence of equations that should result in specified Triple(mgu, isMatching, unifiedTerm)
     */
    internal val successSequenceOfUnification by lazy {
        mapOf(
                listOf(xVar `=` yVar, yVar `=` aAtom) to
                        Triple(Substitution.of(xVar to aAtom, yVar to aAtom), true, aAtom),
                listOf(aAtom `=` yVar, xVar `=` yVar) to
                        Triple(Substitution.of(xVar to aAtom, yVar to aAtom), true, aAtom),
                listOf(aAtom `=` yVar, xVar `=` yVar, xVar `=` aAtom) to
                        Triple(Substitution.of(xVar to aAtom, yVar to aAtom), true, aAtom)
        )
    }

    /**
     * Contains faulty sequence of equations that should result in a failure
     */
    internal val failSequenceOfUnification by lazy {
        mapOf(
                listOf(xVar `=` aAtom, bAtom `=` xVar) to failedResultsTriple,
                listOf(xVar `=` aAtom, bAtom `=` xVar, yVar `=` aAtom) to failedResultsTriple,
                listOf(aAtom `=` yVar, xVar `=` yVar, xVar `=` bAtom) to failedResultsTriple,
                listOf(xVar `=` Struct.of("f", yVar, xVar), yVar `=` Struct.of("f", xVar, yVar)) to failedResultsTriple,
                listOf(xVar `=` Struct.of("f", yVar), yVar `=` Struct.of("f", xVar), aAtom `=` bAtom) to failedResultsTriple
        )
    }


    /**
     * Asserts that mgu computed with [mguStrategy] over [equation] is equals to [expectedMgu]
     */
    internal fun <T1 : Term, T2 : Term> assertMguCorrect(
            equation: Equation<T1, T2>, expectedMgu: Substitution, mguStrategy: MguStrategy<T1, T2>) {

        val (equationLhs, equationRhs) = equation.toPair()

        assertEquals(
                expectedMgu,
                mguStrategy(equationLhs, equationRhs),
                "$equationLhs=$equationRhs mgu?"
        )
    }

    /**
     * Asserts that mgu computed with [mguStrategy] over [correctnessMap] keys are equals to those present in [correctnessMap] values
     */
    internal fun <T1 : Term, T2 : Term> assertMguCorrect(correctnessMap: CorrectnessMap<T1, T2>, mguStrategy: MguStrategy<T1, T2>) =
            correctnessMap.forEach { (equation, correctTriple) -> assertMguCorrect(equation, correctTriple.first, mguStrategy) }

    /**
     * Asserts that match computed with [matchStrategy] over [equation] is equals to [expectedMatch]
     */
    internal fun <T1 : Term, T2 : Term> assertMatchCorrect(
            equation: Equation<T1, T2>, expectedMatch: Boolean, matchStrategy: MatchStrategy<T1, T2>) {

        val (equationLhs, equationRhs) = equation.toPair()

        assertEquals(
                expectedMatch,
                matchStrategy(equationLhs, equationRhs),
                "$equationLhs=$equationRhs match?"
        )
    }

    /**
     * Asserts that matching computed with [matchStrategy] over [correctnessMap] keys are equals to those present in [correctnessMap] values
     */
    internal fun <T1 : Term, T2 : Term> assertMatchCorrect(correctnessMap: CorrectnessMap<T1, T2>, matchStrategy: MatchStrategy<T1, T2>) =
            correctnessMap.forEach { (equation, correctTriple) -> assertMatchCorrect(equation, correctTriple.second, matchStrategy) }

    /**
     * Asserts that unified term computed with [unifyStrategy] over [equation] is equals to [expectedUnifiedTerm]
     */
    internal fun <T1 : Term, T2 : Term> assertUnifiedTermCorrect(
            equation: Equation<T1, T2>, expectedUnifiedTerm: Term?, unifyStrategy: UnifyStrategy<T1, T2>) {

        val (equationLhs, equationRhs) = equation.toPair()

        assertEquals(
                expectedUnifiedTerm,
                unifyStrategy(equationLhs, equationRhs),
                "$equationLhs=$equationRhs unify?"
        )
    }

    /**
     * Asserts that unified term computed with [unifyStrategy] over [correctnessMap] keys are equals to those present in [correctnessMap] values
     */
    internal fun <T1 : Term, T2 : Term> assertUnifiedTermCorrect(correctnessMap: CorrectnessMap<T1, T2>, unifyStrategy: UnifyStrategy<T1, T2>) =
            correctnessMap.forEach { (equation, correctTriple) -> assertUnifiedTermCorrect(equation, correctTriple.third, unifyStrategy) }


    /**
     * Utility function to calculate the unifier for more than one equation, passing created context through different unification
     */
    private fun <T1 : Term, T2 : Term> multipleEquationMgu(
            equations: KtList<Equation<T1, T2>>, unificationStrategyConstructor: (Substitution) -> Unification): Substitution {

        var context: Substitution = Substitution.empty()

        // enrich the context with subsequent equations
        equations.forEach { equation ->
            val (equationLhs, equationRhs) = equation.toPair()
            context = unificationStrategyConstructor(context).mgu(equationLhs, equationRhs)
        }
        return context
    }

    /**
     * Utility function to work with Sequence of Equations to be solved within same context;
     *
     * It calculates mgu for the equation list except for the last equation, piggy-backing the context created;
     * the last equation is used to launch the [lastEquationAssertion] and verify at last if solving equations consecutively,
     * results in expected [correctnessMap] keys
     */
    internal fun <T1 : Term, T2 : Term, O> forEquationSequence(
            lastEquationAssertion: (CorrectnessMap<T1, T2>, (T1, T2) -> O) -> Unit,
            correctnessMap: Map<KtList<Equation<T1, T2>>, Triple<Substitution, Boolean, Term?>>,
            unificationStrategyConstructor: (Substitution) -> Unification,
            unificationStrategyUse: (Substitution, T1, T2) -> O
    ) {
        correctnessMap.forEach { (equations, correctTriple) ->
            val context = multipleEquationMgu(equations.dropLast(1), unificationStrategyConstructor)

            lastEquationAssertion(mapOf(equations.last() to correctTriple)) { term1, term2 -> unificationStrategyUse(context, term1, term2) }
        }
    }
}
