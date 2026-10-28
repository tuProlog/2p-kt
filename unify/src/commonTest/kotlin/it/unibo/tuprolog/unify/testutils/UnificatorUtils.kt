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

/**
 * Utils singleton for testing [Unificator]T2
 *
 * @author Enrico
 */
internal object UnificatorUtils {
    /** A named [equation], with its expected mgu, matching outcome and unified term */
    internal data class UnificationCase(
        val id: String,
        val equation: Equation,
        val mgu: Substitution,
        val isMatching: Boolean,
        val unifiedTerm: Term?,
    )

    /** A named sequence of [equations] solved in the same context, with the expected outcome of the [last] one */
    internal class EquationSequenceCase(
        val id: String,
        val equations: KtList<Equation>,
        expected: Triple<Substitution, Boolean, Term?>,
    ) {
        val last = UnificationCase(id, equations.last(), expected.first, expected.second, expected.third)

        /** The context obtained by solving all [equations] but the last one */
        fun contextBeforeLast(unificationStrategyConstructor: (Substitution) -> Unificator): Substitution =
            equations.dropLast(1).fold<Equation, Substitution>(Substitution.empty()) { context, equation ->
                val (lhs, rhs) = equation.toPair()
                unificationStrategyConstructor(context).mgu(lhs, rhs)
            }
    }

    /** A named pattern to be matched against [memberClause] */
    internal class MemberPatternCase(
        val id: String,
        val pattern: Rule,
    )

    private fun named(
        id: String,
        expectation: Pair<Equation, Triple<Substitution, Boolean, Term?>>,
    ) = UnificationCase(
        id,
        expectation.first,
        expectation.second.first,
        expectation.second.second,
        expectation.second.third,
    )

    private fun namedSequence(
        id: String,
        expectation: Pair<KtList<Equation>, Triple<Substitution, Boolean, Term?>>,
    ) = EquationSequenceCase(id, expectation.first, expectation.second)

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
        id: String,
        first: Term,
        second: Term,
    ): KtList<MemberPatternCase> =
        listOf(
            MemberPatternCase("fact/$id", Fact.of(Struct.of("member", first, second))),
            MemberPatternCase("rule/$id", Rule.of(Struct.of("member", first, second), Var.of("B"))),
        )

    internal val positiveMemberPatterns =
        member("atom-in-list", Atom.of("a"), List.of(Atom.of("a"))) +
            member(
                "struct-in-nested-list",
                List.of(Struct.of("a", Var.of("X"))),
                List.of(List.of(Struct.of("a", Integer.of(1)))),
            )

    internal val negativeMemberPatterns =
        member("atom-not-in-list", Atom.of("a"), List.of(Atom.of("b"))) +
            member(
                "struct-not-in-nested-list",
                List.of(Struct.of("a", Var.of("X"))),
                List.of(List.of(Struct.of("b", Integer.of(1)))),
            )

    /** Contains a mapping between equations that should have success unifying and a `Triple(mgu, isMatching, unifiedTerm)` */
    internal val successfulUnifications by lazy {
        EquationUtils.allIdentityEquations.map {
            UnificationCase(it.id, it.lhs eq it.rhs, Substitution.empty(), true, it.lhs)
        } +
            listOf(
                named("unification/same-atom", (aAtom eq aAtom) to Triple(Substitution.empty(), true, aAtom)),
                named("unification/same-var", (xVar eq xVar) to Triple(Substitution.empty(), true, xVar)),
                named(
                    "unification/atom-with-var",
                    (aAtom eq xVar) to Triple(Substitution.of(xVar, aAtom), true, aAtom),
                ),
                named("unification/var-with-var", (xVar eq yVar) to Triple(Substitution.of(xVar, yVar), true, yVar)),
                named(
                    "unification/var-with-same-named-var",
                    Scope.empty {
                        (xVar eq varOf("X")) to
                            Triple(Substitution.of(xVar, varOf("X")), true, varOf("X"))
                    },
                ),
                named(
                    "unification/var-with-anonymous-var",
                    Var.anonymous().let {
                        (xVar eq it) to
                            Triple(Substitution.of(xVar, it), true, it)
                    },
                ),
                named(
                    "unification/struct-binding-var",
                    (Struct.of("f", aAtom, xVar) eq Struct.of("f", aAtom, bAtom)) to
                        Triple(Substitution.of(xVar, bAtom), true, Struct.of("f", aAtom, bAtom)),
                ),
                named(
                    "unification/structs-with-var-args",
                    (Struct.of("f", xVar) eq Struct.of("f", yVar)) to
                        Triple(Substitution.of(xVar, yVar), true, Struct.of("f", yVar)),
                ),
                named(
                    "unification/nested-struct-with-var",
                    (Struct.of("f", Struct.of("g", xVar)) eq Struct.of("f", yVar)) to
                        Triple(
                            Substitution.of(yVar, Struct.of("g", xVar)),
                            true,
                            Struct.of("f", Struct.of("g", xVar)),
                        ),
                ),
                named(
                    "unification/nested-struct-binding-two-vars",
                    (Struct.of("f", Struct.of("g", xVar), xVar) eq Struct.of("f", yVar, aAtom)) to
                        Triple(
                            Substitution.of(yVar to Struct.of("g", aAtom), xVar to aAtom),
                            true,
                            Struct.of("f", Struct.of("g", aAtom), aAtom),
                        ),
                ),
                named(
                    "unification/rule",
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
                ),
                named(
                    "unification/indicator",
                    Scope.empty {
                        Indicator.of("ciao", 2) eq Indicator.of(varOf("A"), varOf("B")) to
                            Triple(
                                Substitution.of(varOf("A") to Atom.of("ciao"), varOf("B") to Integer.of(2)),
                                true,
                                Indicator.of("ciao", 2),
                            )
                    },
                ),
            )
    }

    /** Contains a mapping between faulty equations and their failed result Triple(mgu, isMatching, unifiedTerm) */
    internal val failedUnifications by lazy {
        EquationUtils.allContradictionEquations.map {
            UnificationCase(it.id, it.lhs eq it.rhs, Substitution.failed(), false, null)
        } +
            listOf(
                named("failure/different-atoms", (aAtom eq bAtom) to failedResultsTriple),
                named(
                    "failure/different-functors",
                    (Struct.of("f", aAtom) eq Struct.of("g", aAtom)) to failedResultsTriple,
                ),
                named(
                    "failure/different-functors-with-vars",
                    (Struct.of("f", xVar) eq Struct.of("g", yVar)) to failedResultsTriple,
                ),
                named(
                    "failure/different-arities",
                    (Struct.of("f", xVar) eq Struct.of("f", yVar, Var.of("Z"))) to failedResultsTriple,
                ),
            )
    }

    /** Contains faulty equations that will be rejected by the occur-check */
    internal val occurCheckFailedUnifications by lazy {
        listOf(
            named("occur-check/var-in-struct", (xVar eq Struct.of("f", xVar)) to failedResultsTriple),
            named(
                "occur-check/var-in-partial-list",
                (xVar eq LogicList.from(listOf(aAtom, bAtom), xVar)) to failedResultsTriple,
            ),
        )
    }

    /** Contains successful sequence of equations that should result in specified Triple(mgu, isMatching, unifiedTerm) */
    internal val successSequenceOfUnification by lazy {
        listOf(
            namedSequence(
                "sequence/var-var-then-var-atom",
                listOf(xVar eq yVar, yVar eq aAtom) to
                    Triple(Substitution.of(xVar to aAtom, yVar to aAtom), true, aAtom),
            ),
            namedSequence(
                "sequence/atom-var-then-var-var",
                listOf(aAtom eq yVar, xVar eq yVar) to
                    Triple(Substitution.of(xVar to aAtom, yVar to aAtom), true, aAtom),
            ),
            namedSequence(
                "sequence/atom-var-then-var-var-then-var-atom",
                listOf(aAtom eq yVar, xVar eq yVar, xVar eq aAtom) to
                    Triple(Substitution.of(xVar to aAtom, yVar to aAtom), true, aAtom),
            ),
        )
    }

    /** Contains faulty sequence of equations that should result in a failure */
    internal val failSequenceOfUnification by lazy {
        listOf(
            namedSequence("sequence/rebinding-var", listOf(xVar eq aAtom, bAtom eq xVar) to failedResultsTriple),
            namedSequence(
                "sequence/rebinding-var-then-binding-another",
                listOf(xVar eq aAtom, bAtom eq xVar, yVar eq aAtom) to failedResultsTriple,
            ),
            namedSequence(
                "sequence/conflicting-bindings-through-var",
                listOf(aAtom eq yVar, xVar eq yVar, xVar eq bAtom) to failedResultsTriple,
            ),
            namedSequence(
                "sequence/cyclic-structs",
                listOf(xVar eq Struct.of("f", yVar, xVar), yVar eq Struct.of("f", xVar, yVar)) to failedResultsTriple,
            ),
            namedSequence(
                "sequence/cyclic-structs-then-contradiction",
                listOf(xVar eq Struct.of("f", yVar), yVar eq Struct.of("f", xVar), aAtom eq bAtom) to
                    failedResultsTriple,
            ),
        )
    }

    /** Asserts that mgu computed with [mguStrategy] over the equation of [case] is the expected one */
    internal inline fun assertMguCorrect(
        case: UnificationCase,
        mguStrategy: MguStrategy,
    ) {
        val (equationLhs, equationRhs) = case.equation.toPair()

        assertEquals(case.mgu, mguStrategy(equationLhs, equationRhs), "$equationLhs=$equationRhs mgu?")
    }

    /** Asserts that match computed with [matchStrategy] over the equation of [case] is the expected one */
    internal inline fun assertMatchCorrect(
        case: UnificationCase,
        matchStrategy: MatchStrategy,
    ) {
        val (equationLhs, equationRhs) = case.equation.toPair()

        assertEquals(case.isMatching, matchStrategy(equationLhs, equationRhs), "$equationLhs=$equationRhs match?")
    }

    /** Asserts that unified term computed with [unifyStrategy] over the equation of [case] is the expected one */
    internal inline fun assertUnifiedTermCorrect(
        case: UnificationCase,
        unifyStrategy: UnifyStrategy,
    ) {
        val (equationLhs, equationRhs) = case.equation.toPair()

        assertEquals(case.unifiedTerm, unifyStrategy(equationLhs, equationRhs), "$equationLhs=$equationRhs unify?")
    }
}
