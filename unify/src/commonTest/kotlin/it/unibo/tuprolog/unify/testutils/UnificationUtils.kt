package it.unibo.tuprolog.unify.testutils

import Equation
import `=`
import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.unify.Unification
import kotlin.test.assertEquals

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
     * Returns same equations as [unifications] but maps only to their Most General Unifier
     */
    private fun <T1 : Term, T2 : Term> equationToMgu(unifications: Map<Equation<T1, T2>, Triple<Substitution, Boolean, Term?>>) =
            unifications.mapValues { it.value.first }

    /**
     * Returns same equations as [unifications] but maps only to their "match boolean value"
     */
    private fun <T1 : Term, T2 : Term> equationToMatch(unifications: Map<Equation<T1, T2>, Triple<Substitution, Boolean, Term?>>) =
            unifications.mapValues { it.value.second }

    /**
     * Returns same equations as [unifications] but maps only to their resulting unified Term
     */
    private fun <T1 : Term, T2 : Term> equationToUnifiedTerm(unifications: Map<Equation<T1, T2>, Triple<Substitution, Boolean, Term?>>) =
            unifications.mapValues { it.value.third }

    /**
     * Contains the same equations as per [successfulUnifications] but maps only to their Most General Unifier
     */
    internal val successfulUnificationMgus by lazy { equationToMgu(successfulUnifications) }
    /**
     * Contains the same equations as per [successfulUnifications] but maps only to their match response
     */
    internal val successfulUnificationMatches by lazy { equationToMatch(successfulUnifications) }
    /**
     * Contains the same equations as per [successfulUnifications] but maps only to their unified Term
     */
    internal val successfulUnificationTerms by lazy { equationToUnifiedTerm(successfulUnifications) }

    /**
     * Contains the same equations as per [failedUnifications] but maps only to their Most General Unifier
     */
    internal val failedUnificationMgus by lazy { equationToMgu(failedUnifications) }
    /**
     * Contains the same equations as per [failedUnifications] but maps only to their match response
     */
    internal val failedUnificationMatches by lazy { equationToMatch(failedUnifications) }
    /**
     * Contains the same equations as per [failedUnifications] but maps only to their unified Term
     */
    internal val failedUnificationTerms by lazy { equationToUnifiedTerm(failedUnifications) }

    /**
     * Contains the same equations as per [occurCheckFailedUnifications] but maps only to their Most General Unifier
     */
    internal val occurCheckFailedUnificationMgus by lazy { equationToMgu(occurCheckFailedUnifications) }
    /**
     * Contains the same equations as per [occurCheckFailedUnifications] but maps only to their match response
     */
    internal val occurCheckFailedUnificationMatches by lazy { equationToMatch(occurCheckFailedUnifications) }
    /**
     * Contains the same equations as per [occurCheckFailedUnifications] but maps only to their unified Term
     */
    internal val occurCheckFailedUnificationTerms by lazy { equationToUnifiedTerm(occurCheckFailedUnifications) }


    /**
     * Asserts that mgu computed with [unificationStrategy] is equals to that present in [equationsToExpectedMgu]
     */
    internal fun <T1 : Term, T2 : Term> assertMguCorrect(
            unificationStrategy: Unification,
            equationsToExpectedMgu: Map<Equation<T1, T2>, Substitution>,
            occurCheck: Boolean
    ) {
        equationsToExpectedMgu.forEach { (equation, correctMgu) ->
            val (equationLhs, equationRhs) = equation

            assertEquals(correctMgu, unificationStrategy.mgu(equationLhs, equationRhs, occurCheck), "$equationLhs=$equationRhs mgu?")
        }
    }

    /**
     * Asserts that matching computed with [unificationStrategy] is equals to that present in [equationsToExpectedMatch]
     */
    internal fun <T1 : Term, T2 : Term> assertMatchCorrect(
            unificationStrategy: Unification,
            equationsToExpectedMatch: Map<Equation<T1, T2>, Boolean>,
            occurCheck: Boolean
    ) {
        equationsToExpectedMatch.forEach { (equation, correctMatch) ->
            val (equationLhs, equationRhs) = equation

            assertEquals(correctMatch, unificationStrategy.match(equationLhs, equationRhs, occurCheck), "$equationLhs=$equationRhs match?")
        }
    }

    /**
     * Asserts that unified term computed with [unificationStrategy] is equals to that present in [equationsToExpectedTerm]
     */
    internal fun <T1 : Term, T2 : Term> assertUnifiedTermCorrect(
            unificationStrategy: Unification,
            equationsToExpectedTerm: Map<Equation<T1, T2>, Term?>,
            occurCheck: Boolean
    ) {
        equationsToExpectedTerm.forEach { (equation, correctTerm) ->
            val (equationLhs, equationRhs) = equation

            assertEquals(correctTerm, unificationStrategy.unify(equationLhs, equationRhs, occurCheck), "$equationLhs=$equationRhs unify?")
        }
    }

}
