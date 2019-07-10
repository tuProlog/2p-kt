package it.unibo.tuprolog.unify.testutils

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.unify.Equation
import it.unibo.tuprolog.unify.Unification
import it.unibo.tuprolog.unify.`=`
import kotlin.test.assertEquals
import kotlin.collections.List as KtList

/**
 * Utils singleton for testing [Unification]
 *
 * @author Enrico
 */
internal object UnificationUtils {

    private val aAtom = Atom.of("a")
    private val bAtom = Atom.of("b")
    private val xVar get() = Var.of("X") // this syntax will create a new var every time
    private val yVar get() = Var.of("Y")

    private val failedResultsTriple: Triple<Substitution, Boolean, Term?> =
            Triple(Substitution.failed(), false, null)

    /**
     * Contains a mapping between equations that should have success unifying and a `Triple(mgu, isMatching, unifiedTerm)`
     */
    internal val successfulUnifications by lazy {
        mapOf(
                (aAtom `=` aAtom) to Triple(Substitution.empty(), true, aAtom),
                (xVar `=` xVar) to Triple(Substitution.empty(), true, xVar),
                (aAtom `=` xVar) to Triple(Substitution.of(xVar, aAtom), true, aAtom),
                (xVar `=` yVar) to Triple(Substitution.of(xVar, yVar), true, yVar),
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
                        )
        )
    }

    /**
     * Contains a mapping between faulty equations and their failed result Triple(mgu, isMatching, unifiedTerm)
     */
    internal val failedUnifications by lazy {
        mapOf(
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
                (xVar `=` Struct.of("f", xVar)) to failedResultsTriple
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
                        Triple(Substitution.of(xVar to aAtom, yVar to aAtom), true, aAtom)
        )
    }

    /**
     * Contains faulty sequence of equations that should result in a failure
     */
    internal val failSequenceOfUnification by lazy {
        mapOf(
                listOf(xVar `=` aAtom, bAtom `=` xVar) to failedResultsTriple
        )
    }


    /**
     * Asserts that mgu computed with [unificationStrategy] over [equation] is equals to [expectedMgu], optionally enabling [occurCheck]
     */
    internal fun <T1 : Term, T2 : Term> assertMguCorrect(
            equation: Equation<T1, T2>,
            expectedMgu: Substitution,
            unificationStrategy: Unification,
            occurCheck: Boolean
    ) {
        val (equationLhs, equationRhs) = equation

        assertEquals(
                expectedMgu,
                unificationStrategy.mgu(equationLhs, equationRhs, occurCheck),
                "$equationLhs=$equationRhs mgu?"
        )
    }

    /**
     * Asserts that match computed with [unificationStrategy] over [equation] is equals to [expectedMatch], optionally enabling [occurCheck]
     */
    internal fun <T1 : Term, T2 : Term> assertMatchCorrect(
            equation: Equation<T1, T2>,
            expectedMatch: Boolean,
            unificationStrategy: Unification,
            occurCheck: Boolean
    ) {
        val (equationLhs, equationRhs) = equation

        assertEquals(
                expectedMatch,
                unificationStrategy.match(equationLhs, equationRhs, occurCheck),
                "$equationLhs=$equationRhs match?"
        )
    }

    /**
     * Asserts that unified term computed with [unificationStrategy] over [equation] is equals to [expectedUnifiedTerm], optionally enabling [occurCheck]
     */
    internal fun <T1 : Term, T2 : Term> assertUnifiedTermCorrect(
            equation: Equation<T1, T2>,
            expectedUnifiedTerm: Term?,
            unificationStrategy: Unification,
            occurCheck: Boolean
    ) {
        val (equationLhs, equationRhs) = equation

        assertEquals(
                expectedUnifiedTerm,
                unificationStrategy.unify(equationLhs, equationRhs, occurCheck),
                "$equationLhs=$equationRhs unify?"
        )
    }

    /**
     * Asserts that mgu computed with [unificationStrategy] over [correctnessMap] keys are equals to those present in [correctnessMap] values
     */
    internal fun <T1 : Term, T2 : Term> assertMguCorrect(
            correctnessMap: Map<Equation<T1, T2>, Triple<Substitution, Boolean, Term?>>,
            unificationStrategy: Unification,
            occurCheck: Boolean
    ) {
        correctnessMap.forEach { (equation, correctTriple) ->
            assertMguCorrect(equation, correctTriple.first, unificationStrategy, occurCheck)
        }
    }

    /**
     * Asserts that matching computed with [unificationStrategy] over [correctnessMap] keys are equals to those present in [correctnessMap] values
     */
    internal fun <T1 : Term, T2 : Term> assertMatchCorrect(
            correctnessMap: Map<Equation<T1, T2>, Triple<Substitution, Boolean, Term?>>,
            unificationStrategy: Unification,
            occurCheck: Boolean
    ) {
        correctnessMap.forEach { (equation, correctTriple) ->
            assertMatchCorrect(equation, correctTriple.second, unificationStrategy, occurCheck)
        }
    }

    /**
     * Asserts that unified term computed with [unificationStrategy] over [correctnessMap] keys are equals to those present in [correctnessMap] values
     */
    internal fun <T1 : Term, T2 : Term> assertUnifiedTermCorrect(
            correctnessMap: Map<Equation<T1, T2>, Triple<Substitution, Boolean, Term?>>,
            unificationStrategy: Unification,
            occurCheck: Boolean
    ) {
        correctnessMap.forEach { (equation, correctTriple) ->
            assertUnifiedTermCorrect(equation, correctTriple.third, unificationStrategy, occurCheck)
        }
    }


    /**
     * Utility function to calculate the unifier for more than one equation, passing created context among different unification
     */
    private fun <T1 : Term, T2 : Term> multipleEquationMgu(
            equations: KtList<Equation<T1, T2>>,
            unificationStrategyConstructor: (Substitution) -> Unification
    ): Substitution {
        var context = Substitution.empty()

        // enrich the context with subsequent equations
        equations.forEach { equation ->
            val (equationLhs, equationRhs) = equation
            context = unificationStrategyConstructor(context).mgu(equationLhs, equationRhs)
        }
        return context
    }


    /**
     * Asserts that creating unification strategy with [unificationStrategyConstructor] and computing the mgu over [correctnessMap] keys,
     * that contain multiple equations, results in [correctnessMap] correct values
     */
    internal fun <T1 : Term, T2 : Term> assertMguCorrectForMultipleEq(
            correctnessMap: Map<KtList<Equation<T1, T2>>, Triple<Substitution, Boolean, Term?>>,
            unificationStrategyConstructor: (Substitution) -> Unification,
            occurCheck: Boolean
    ) {
        correctnessMap.forEach { (equations, correctTriple) ->
            val context = multipleEquationMgu(equations.dropLast(1), unificationStrategyConstructor)

            assertMguCorrect(mapOf(equations.last() to correctTriple), unificationStrategyConstructor(context), occurCheck)
        }
    }

    /**
     * Asserts that creating unification strategy with [unificationStrategyConstructor] and computing the matching over [correctnessMap] keys,
     * that contain multiple equations, results in [correctnessMap] correct values
     */
    internal fun <T1 : Term, T2 : Term> assertMatchCorrectForMultipleEq(
            correctnessMap: Map<KtList<Equation<T1, T2>>, Triple<Substitution, Boolean, Term?>>,
            unificationStrategyConstructor: (Substitution) -> Unification,
            occurCheck: Boolean
    ) {
        correctnessMap.forEach { (equations, correctTriple) ->
            val context = multipleEquationMgu(equations.dropLast(1), unificationStrategyConstructor)

            assertMatchCorrect(mapOf(equations.last() to correctTriple), unificationStrategyConstructor(context), occurCheck)
        }
    }

    /**
     * Asserts that creating unification strategy with [unificationStrategyConstructor] and computing the unified term over [correctnessMap] keys,
     * that contain multiple equations, results in [correctnessMap] correct values
     */
    internal fun <T1 : Term, T2 : Term> assertUnifiedTermCorrectForMultipleEq(
            correctnessMap: Map<KtList<Equation<T1, T2>>, Triple<Substitution, Boolean, Term?>>,
            unificationStrategyConstructor: (Substitution) -> Unification,
            occurCheck: Boolean
    ) {
        correctnessMap.forEach { (equations, correctTriple) ->
            val context = multipleEquationMgu(equations.dropLast(1), unificationStrategyConstructor)

            assertUnifiedTermCorrect(mapOf(equations.last() to correctTriple), unificationStrategyConstructor(context), occurCheck)
        }
    }
}
