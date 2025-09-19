package it.unibo.tuprolog.solve.problog.lib.knowledge.impl

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.exception.TuPrologException
import it.unibo.tuprolog.solve.problog.lib.ProblogLib
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbExplanation
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbExplanationTerm
import it.unibo.tuprolog.solve.problog.lib.primitive.ProbExplAnd
import it.unibo.tuprolog.solve.problog.lib.rules.Prob
import it.unibo.tuprolog.utils.setTag

/**
 * Collection of general-purpose functions and values useful for internal clause mapping.
 *
 * @author Jason Dellaluce
 */
internal object ClauseMappingUtils {
    private var clauseIndex: Long = 1

    private val cascadeMappers =
        listOf(
            DirectiveClauseMapper,
            AnnotatedDisjunctionClauseMapper,
            ProbabilisticClauseMapper,
            EvidenceClauseMapper,
            PrologClauseMapper,
            NothingClauseMapper,
        )

    /** Generates a new clause identifier.
     * Identifying clauses is necessary to distinguish them
     * during probabilistic logic query resolutions. */
    fun newClauseId(): Long = clauseIndex++

    /** Entrypoint function for internal theory mapping of single [Clause]s */
    fun map(clause: Clause): List<Clause> =
        if (clause.isMappedAsProblog) {
            listOf(clause)
        } else {
            cascadeMappers.first { it.isCompatible(clause) }.apply(clause).map { it.setMappedAsProblog(true) }
        }

    /** Entrypoint function for internal mapping of an [Indicator] */
    fun map(indicator: Indicator): Indicator {
        val arityTerm = indicator.arityTerm
        return Indicator.of(
            indicator.nameTerm,
            when (arityTerm) {
                is Numeric -> Numeric.of(arityTerm.intValue.toInt() + 1)
                else -> arityTerm
            },
        )
    }
}

private const val PROBLOG_MAPPED_TAG = "it.unibo.tuprolog.problog.mapped"

internal val Clause.isMappedAsProblog: Boolean
    get() = getTag(PROBLOG_MAPPED_TAG) ?: false

internal fun Clause.setMappedAsProblog(value: Boolean): Clause = setTag(PROBLOG_MAPPED_TAG, value)

private val nonWrappableFunctors = setOf("!", "true", "false")

internal val Term.isWrappableWithExplanation: Boolean get() = this.safeToStruct().functor !in nonWrappableFunctors

internal fun Term.withExplanationNonWrappable(explanation: Term): Struct =
    Tuple.of(
        this,
        Struct.of("=", explanation, ProbExplanation.TRUE.toTerm()),
    )

/** This has to be used on head terms of [Clause]s and with goal [Term]s.
 * Adds [explanation] to [this] term by making it the last argument of the predicate.
 * Implicitly, the arity is increased by 1.
 * */
internal fun Term.withExplanation(explanation: Term): Struct =
    if (!this.isWrappableWithExplanation) {
        this.withExplanationNonWrappable(explanation)
    } else {
        when (this) {
            is Struct -> Struct.of(this.functor, this.args + explanation)
            else -> Struct.of(this.toString(), explanation)
        }
    }

/** This has to be used on body terms of [Clause]s. Adds [explanation] to [this]
 * term by wrapping it in an higher-level meta-predicate.
 * */
internal fun Term.withBodyExplanation(explanation: Term): Struct = wrapInPredicateRecursive(Prob.functor, explanation)

/** Wraps the [this] term with the provided in a new predicate that has [functor] as its
 * functor, and [this] and [explanation] as argument terms. In this use case, [explanation]
 * refers to a term representing the probabilistic logic explanation of [this] term that
 * will be used during queries resolution to compute the overall [ProbExplanation] of
 * solutions. [explanation] can either be a known and definite [Term], or a [Var].
 *
 * The wrapping is not applied recursively.
 * */
internal fun Term.wrapInPredicate(
    functor: String,
    explanation: Term,
): Struct =
    if (!this.isWrappableWithExplanation) {
        this.withExplanationNonWrappable(explanation)
    } else {
        Struct.of(functor, explanation, this)
    }

/** Wraps the [this] term with the provided in a new predicate that has [functor] as its
 * functor, and [this] and [explanation] as argument terms. In this use case, [explanation]
 * refers to a term representing the probabilistic logic explanation of [this] term that
 * will be used during queries resolution to compute the overall [ProbExplanation] of
 * solutions. [explanation] can either be a known and definite [Term], or a [Var].
 *
 * The wrapping is applied recursively if [this] is an instance of [Struct] and its functor
 * belongs to the following set of predicates:
 *  (",", ";", "->")
 * In all those cases, the wrapping will not be applied in order to leverage the
 * semantics provided by Prolog for those predicates. Most of them represent, in fact,
 * an operator.
 * */
internal fun Term.wrapInPredicateRecursive(
    functor: String,
    explanation: Term,
    depth: Int = 1,
): Struct =
    when (this) {
        is Struct -> {
            when (this.functor) {
                "," -> {
                    val leftVar = Var.of("${ProblogLib.EXPLANATION_VAR_NAME}_A_$depth")
                    val rightVar = Var.of("${ProblogLib.EXPLANATION_VAR_NAME}_B_$depth")
                    Tuple.of(
                        this[0].wrapInPredicateRecursive(functor, leftVar, depth + 1),
                        this[1].wrapInPredicateRecursive(functor, rightVar, depth + 1),
                        Struct.of(ProbExplAnd.functor, explanation, leftVar, rightVar),
                    )
                }
                ";" -> {
                    Struct.of(
                        ";",
                        this[0].wrapInPredicateRecursive(functor, explanation, depth + 1),
                        this[1].wrapInPredicateRecursive(functor, explanation, depth + 1),
                    )
                }
                "->" -> {
                    Struct.of(
                        "->",
                        this[0].wrapInPredicateRecursive(functor, Var.anonymous(), depth + 1),
                        this[1].wrapInPredicateRecursive(functor, explanation, depth + 1),
                    )
                }
                else -> {
                    this.wrapInPredicate(functor, explanation)
                }
            }
        }
        else -> {
            this.wrapInPredicate(functor, explanation)
        }
    }

/** In Prolog, it's legit to have rules with a body containing more variables than the head.
 * This function computes a difference between the set of variables from the body and from the head,
 * and returns a [Set] containing that difference. */
internal val Rule.extraVariables: Set<Var>
    get() {
        return if (!this.isGround) {
            val headVars = this.head.variables.toSet()
            val bodyVars = this.body.variables.toSet()
            bodyVars.subtract(headVars)
        } else {
            emptySet()
        }
    }

/** Applies a simple casting from a [Term] instance to a [Struct] one.
 * In case [this] is already an instance of [Struct], this function does
 * nothing if not regular casting. */
internal fun Term.safeToStruct(): Struct =
    if (this is Struct) {
        this
    } else {
        Struct.of(this.toString())
    }

/** Given a [this], removes all anonymous [Var] variables contained
 * in head and body, substituting them with non-anonymous variables. */
internal fun Rule.withoutAnonymousVars(): Rule {
    val anonVarSubstitution =
        Substitution.of(
            this.head.variables
                .toSet()
                .plus(this.body.variables.toSet())
                .filter {
                    it.isAnonymous
                }.mapIndexed { index, it ->
                    Pair(it, Var.of("ANON_$index"))
                },
        )
    return when (this) {
        is Fact -> {
            Fact.of(this.head.apply(anonVarSubstitution).safeToStruct())
        }
        else ->
            Rule.of(
                this.head.apply(anonVarSubstitution).safeToStruct(),
                this.body.apply(anonVarSubstitution).safeToStruct(),
            )
    }
}

/** Encapsulates a [ProbExplanation] object inside a Prolog [Term] */
internal fun ProbExplanation.toTerm(): Term = ProbExplanationTerm(this)

/** If the given term represents an arithmetic expression, this function returns
 * a [Double] containing the numeric solution of the expression. Otherwise,
 * an exception is thrown. */
internal fun Term.solveArithmeticExpression(): Double {
    val termStr = this.toString().replace("\\s".toRegex(), "")
    val regexMatch =
        Regex("([0-9.]+)(/)([0-9.]+).*").find(termStr)
            ?: throw TuPrologException(
                "Unsupported probability notation in annotated disjunction: $termStr",
            )
    val (dividendStr, _, divisorStr) = regexMatch.destructured
    val dividend = dividendStr.toDoubleOrNull()
    val divisor = divisorStr.toDoubleOrNull()
    if (dividend == null || divisor == null) {
        throw TuPrologException(
            "Unable to parse arithmetic division expression: $termStr",
        )
    }
    return dividend / divisor
}

/** If the given term represents an arithmetic expression, this function returns
 * a [Double] containing the numeric solution of the expression. Otherwise,
 * this returns null. */
internal fun Term.solveArithmeticExpressionOrNull(): Double? =
    try {
        this.solveArithmeticExpression()
    } catch (e: Throwable) {
        null
    }
