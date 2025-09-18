package it.unibo.tuprolog.unify.testutils

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.Equation
import it.unibo.tuprolog.unify.Unificator
import it.unibo.tuprolog.unify.eq
import kotlin.test.assertEquals
import it.unibo.tuprolog.core.List.Companion as LogicList
import kotlin.collections.List as KtList

/** Represents the Unification Mgu function */
private typealias MguStrategy = (Term, Term) -> Substitution

/** Represents the Unification Match function */
private typealias MatchStrategy = (Term, Term) -> Boolean

/** Represents teh Unification Unify function */
private typealias UnifyStrategy = (Term, Term) -> Term?

/** A typealias to simplify method signature writing */
private typealias CorrectnessMap = Map<Equation, Triple<Substitution, Boolean, Term?>>

/**
 * Utils singleton for testing [Unificator]T2
 *
 * @author Enrico
 */
internal object UnificatorUtils {
    private val aAtom = Atom.of("a")
    private val bAtom = Atom.of("b")
    private val xVar = Var.of("X")
    private val yVar = Var.of("Y")

    private val failedResultsTriple: Triple<Substitution, Boolean, Term?> =
        Triple(Substitution.failed(), false, null)

    internal val memberClause =
        Scope.empty {
            factOf(structOf("member", varOf("H"), consOf(varOf("H"), varOf("T"))))
        }

    private fun member(
        first: Term,
        second: Term,
    ): kotlin.collections.List<Rule> =
        listOf(
            Fact.of(Struct.of("member", first, second)),
            Rule.of(Struct.of("member", first, second), Var.of("B")),
        )

    internal val positiveMemberPatterns =
        member(Atom.of("a"), List.of(Atom.of("a"))) +
            member(
                List.of(Struct.of("a", Var.of("X"))),
                List.of(List.of(Struct.of("a", Integer.of(1)))),
            )

    internal val negativeMemberPatterns =
        member(Atom.of("a"), List.of(Atom.of("b"))) +
            member(
                List.of(Struct.of("a", Var.of("X"))),
                List.of(List.of(Struct.of("b", Integer.of(1)))),
            )

    /** Contains a mapping between equations that should have success unifying and a `Triple(mgu, isMatching, unifiedTerm)` */
    internal val successfulUnifications by lazy {
        mapOf(
            *EquationUtils.allIdentityEquations
                .map { (lhs, rhs) ->
                    (lhs eq rhs) to Triple(Substitution.empty(), true, lhs)
                }.toTypedArray(),
            (aAtom eq aAtom) to Triple(Substitution.empty(), true, aAtom),
            (xVar eq xVar) to Triple(Substitution.empty(), true, xVar),
            (aAtom eq xVar) to Triple(Substitution.of(xVar, aAtom), true, aAtom),
            (xVar eq yVar) to Triple(Substitution.of(xVar, yVar), true, yVar),
            Scope.empty { (xVar eq varOf("X")) to Triple(Substitution.of(xVar, varOf("X")), true, varOf("X")) },
            Var.anonymous().let { (xVar eq it) to Triple(Substitution.of(xVar, it), true, it) },
            (Struct.of("f", aAtom, xVar) eq Struct.of("f", aAtom, bAtom)) to
                Triple(Substitution.of(xVar, bAtom), true, Struct.of("f", aAtom, bAtom)),
            (Struct.of("f", xVar) eq Struct.of("f", yVar)) to
                Triple(Substitution.of(xVar, yVar), true, Struct.of("f", yVar)),
            (Struct.of("f", Struct.of("g", xVar)) eq Struct.of("f", yVar)) to
                Triple(
                    Substitution.of(yVar, Struct.of("g", xVar)),
                    true,
                    Struct.of("f", Struct.of("g", xVar)),
                ),
            (Struct.of("f", Struct.of("g", xVar), xVar) eq Struct.of("f", yVar, aAtom)) to
                Triple(
                    Substitution.of(yVar to Struct.of("g", aAtom), xVar to aAtom),
                    true,
                    Struct.of("f", Struct.of("g", aAtom), aAtom),
                ),
            Scope.empty {
                Rule.of(Struct.of("f", aAtom, Struct.of("b", varOf("X"))), bAtom) eq
                    Rule.of(Struct.of("f", varOf("A"), varOf("B")), varOf("C")) to
                    Triple(
                        Substitution.of(
                            varOf("A") to aAtom,
                            varOf("B") to Struct.of("b", varOf("X")),
                            varOf("C") to bAtom,
                        ),
                        true,
                        Rule.of(Struct.of("f", aAtom, Struct.of("b", varOf("X"))), bAtom),
                    )
            },
            Scope.empty {
                Indicator.of("ciao", 2) eq Indicator.of(varOf("A"), varOf("B")) to
                    Triple(
                        Substitution.of(varOf("A") to Atom.of("ciao"), varOf("B") to Integer.of(2)),
                        true,
                        Indicator.of("ciao", 2),
                    )
            },
        )
    }

    /** Contains a mapping between faulty equations and their failed result Triple(mgu, isMatching, unifiedTerm) */
    internal val failedUnifications by lazy {
        mapOf(
            *EquationUtils.allContradictionEquations
                .map { (lhs, rhs) ->
                    (lhs eq rhs) to failedResultsTriple
                }.toTypedArray(),
            (aAtom eq bAtom) to failedResultsTriple,
            (Struct.of("f", aAtom) eq Struct.of("g", aAtom)) to failedResultsTriple,
            (Struct.of("f", xVar) eq Struct.of("g", yVar)) to failedResultsTriple,
            (Struct.of("f", xVar) eq Struct.of("f", yVar, Var.of("Z"))) to failedResultsTriple,
        )
    }

    /** Contains faulty equations that will be rejected by the occur-check */
    internal val occurCheckFailedUnifications by lazy {
        mapOf(
            (xVar eq Struct.of("f", xVar)) to failedResultsTriple,
            (xVar eq LogicList.from(listOf(aAtom, bAtom), xVar)) to failedResultsTriple,
        )
    }

    /** Contains successful sequence of equations that should result in specified Triple(mgu, isMatching, unifiedTerm) */
    internal val successSequenceOfUnification by lazy {
        mapOf(
            listOf(xVar eq yVar, yVar eq aAtom) to
                Triple(Substitution.of(xVar to aAtom, yVar to aAtom), true, aAtom),
            listOf(aAtom eq yVar, xVar eq yVar) to
                Triple(Substitution.of(xVar to aAtom, yVar to aAtom), true, aAtom),
            listOf(aAtom eq yVar, xVar eq yVar, xVar eq aAtom) to
                Triple(Substitution.of(xVar to aAtom, yVar to aAtom), true, aAtom),
        )
    }

    /** Contains faulty sequence of equations that should result in a failure */
    internal val failSequenceOfUnification by lazy {
        mapOf(
            listOf(xVar eq aAtom, bAtom eq xVar) to failedResultsTriple,
            listOf(xVar eq aAtom, bAtom eq xVar, yVar eq aAtom) to failedResultsTriple,
            listOf(aAtom eq yVar, xVar eq yVar, xVar eq bAtom) to failedResultsTriple,
            listOf(xVar eq Struct.of("f", yVar, xVar), yVar eq Struct.of("f", xVar, yVar)) to failedResultsTriple,
            listOf(xVar eq Struct.of("f", yVar), yVar eq Struct.of("f", xVar), aAtom eq bAtom) to failedResultsTriple,
        )
    }

    /** Asserts that mgu computed with [mguStrategy] over [equation] is equals to [expectedMgu] */
    internal inline fun assertMguCorrect(
        equation: Equation,
        expectedMgu: Substitution,
        mguStrategy: MguStrategy,
    ) {
        val (equationLhs, equationRhs) = equation.toPair()

        assertEquals(
            expectedMgu,
            mguStrategy(equationLhs, equationRhs),
            "$equationLhs=$equationRhs mgu?",
        )
    }

    /** Asserts that mgu computed with [mguStrategy] over [correctnessMap] keys are equals to those present in [correctnessMap] values */
    internal inline fun assertMguCorrect(
        correctnessMap: CorrectnessMap,
        mguStrategy: MguStrategy,
    ) = correctnessMap.forEach { (equation, correctTriple) ->
        assertMguCorrect(
            equation,
            correctTriple.first,
            mguStrategy,
        )
    }

    /** Asserts that match computed with [matchStrategy] over [equation] is equals to [expectedMatch] */
    internal inline fun assertMatchCorrect(
        equation: Equation,
        expectedMatch: Boolean,
        matchStrategy: MatchStrategy,
    ) {
        val (equationLhs, equationRhs) = equation.toPair()

        assertEquals(
            expectedMatch,
            matchStrategy(equationLhs, equationRhs),
            "$equationLhs=$equationRhs match?",
        )
    }

    /** Asserts that matching computed with [matchStrategy] over [correctnessMap] keys are equals to those present in [correctnessMap] values */
    internal inline fun assertMatchCorrect(
        correctnessMap: CorrectnessMap,
        matchStrategy: MatchStrategy,
    ) = correctnessMap.forEach { (equation, correctTriple) ->
        assertMatchCorrect(
            equation,
            correctTriple.second,
            matchStrategy,
        )
    }

    /** Asserts that unified term computed with [unifyStrategy] over [equation] is equals to [expectedUnifiedTerm] */
    internal inline fun assertUnifiedTermCorrect(
        equation: Equation,
        expectedUnifiedTerm: Term?,
        unifyStrategy: UnifyStrategy,
    ) {
        val (equationLhs, equationRhs) = equation.toPair()

        assertEquals(
            expectedUnifiedTerm,
            unifyStrategy(equationLhs, equationRhs),
            "$equationLhs=$equationRhs unify?",
        )
    }

    /** Asserts that unified term computed with [unifyStrategy] over [correctnessMap] keys are equals to those present in [correctnessMap] values */
    internal inline fun assertUnifiedTermCorrect(
        correctnessMap: CorrectnessMap,
        unifyStrategy: UnifyStrategy,
    ) = correctnessMap.forEach { (equation, correctTriple) ->
        assertUnifiedTermCorrect(
            equation,
            correctTriple.third,
            unifyStrategy,
        )
    }

    /** Utility function to calculate the unifier for more than one equation, passing created context through different unification */
    private inline fun multipleEquationMgu(
        equations: KtList<Equation>,
        unificationStrategyConstructor: (Substitution) -> Unificator,
    ): Substitution {
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
    internal inline fun <T1 : Term, T2 : Term, O> forEquationSequence(
        lastEquationAssertion: (CorrectnessMap, (T1, T2) -> O) -> Unit,
        correctnessMap: Map<KtList<Equation>, Triple<Substitution, Boolean, Term?>>,
        unificationStrategyConstructor: (Substitution) -> Unificator,
        crossinline unificationStrategyUse: (Substitution, T1, T2) -> O,
    ) {
        correctnessMap.forEach { (equations, correctTriple) ->
            val context = multipleEquationMgu(equations.dropLast(1), unificationStrategyConstructor)

            lastEquationAssertion(mapOf(equations.last() to correctTriple)) { term1, term2 ->
                unificationStrategyUse(context, term1, term2)
            }
        }
    }
}
