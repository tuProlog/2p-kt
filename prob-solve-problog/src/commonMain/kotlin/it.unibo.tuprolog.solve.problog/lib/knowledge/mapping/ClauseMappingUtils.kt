package it.unibo.tuprolog.solve.problog.lib.knowledge.mapping

import it.unibo.tuprolog.bdd.BinaryDecisionDiagram
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.exception.TuPrologException
import it.unibo.tuprolog.solve.problog.lib.ProblogLib
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbTerm
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProblogObjectRef
import it.unibo.tuprolog.solve.problog.lib.primitive.ProbBuildAnd
import it.unibo.tuprolog.solve.problog.lib.rule.Prob

/**
 * Collection of general-purpose functions and values useful for internal clause mapping.
 */
internal object ClauseMappingUtils {

    private var clauseIndex: Long = 1
    private val cascadeMappers = listOf(
        DisjointAnnotationClauseMapper,
        ProbabilisticClauseMapper,
        EvidenceClauseMapper,
        PrologClauseMapper,
        NothingClauseMapper,
    )

    /** Generates a new clause identifier.
     * Identifying clauses is necessary to distinguish them
     * during probabilistic logic query resolutions. */
    fun newClauseId(): Long {
        return clauseIndex++
    }

    /** Entrypoint function for internal theory mapping of single [Clause]s */
    fun map(clause: Clause): List<Clause> {
        return cascadeMappers.first { it.isCompatible(clause) }.apply(clause)
    }
}

/** Wraps the given term, making it being part of a higher-level binary predicate.
 * In the result predicate, the first argument is [leftTerm], and the second one is
 * the provided [this]. The wrapping is not applied recursively
 * */
internal fun Term.wrapInBinaryPredicate(leftTerm: Term): Struct {
    return Struct.of(Prob.FUNCTOR, leftTerm, this)
}

/** Wraps the given term, making it being part of a higher-level binary predicate.
 * In the result predicate, the first argument is [leftTerm], and the second one is
 * the provided [this]. The mapping is applied recursively if [this] is an instance
 * of [Struct] and its functor belongs to the following set of predicates:
 *  (",", ";", "->")
 * In all those cases, the wrapping will not be applied in order to leverage the
 * semantics provided by Prolog for those predicates. Most of them represent, in fact,
 * an operator.
 * */
internal fun Term.wrapInBinaryPredicateRecursive(leftTerm: Term, depth: Int = 1): Struct {
    return when (this) {
        is Struct -> {
            when (this.functor) {
                "," -> {
                    val leftVar = Var.of("${ProblogLib.DD_VAR_NAME}_A_$depth")
                    val rightVar = Var.of("${ProblogLib.DD_VAR_NAME}_B_$depth")
                    Tuple.of(
                        this[0].wrapInBinaryPredicateRecursive(leftVar, depth + 1),
                        this[1].wrapInBinaryPredicateRecursive(rightVar, depth + 1),
                        Struct.of(ProbBuildAnd.functor, leftTerm, leftVar, rightVar),
                    )
                }
                ";" -> {
                    Struct.of(
                        ";",
                        this[0].wrapInBinaryPredicateRecursive(leftTerm, depth + 1),
                        this[1].wrapInBinaryPredicateRecursive(leftTerm, depth + 1),
                    )
                }
                "->" -> {
                    Struct.of(
                        "->",
                        this[0].wrapInBinaryPredicateRecursive(Var.anonymous(), depth + 1),
                        this[1].wrapInBinaryPredicateRecursive(leftTerm, depth + 1),
                    )
                }
                else -> {
                    this.wrapInBinaryPredicate(leftTerm)
                }
            }
        }
        else -> {
            this.wrapInBinaryPredicate(leftTerm)
        }
    }
}

/** Given a generic [Rule], computes a [Struct] representing a ground version of the rule's head.
 * In Prolog, it's legit to have rules with a body containing more variables than the head.
 * This function computes a difference between the set of variables from the body and from the head,
 * and appends it to the original head as predicate arguments. */
internal val Rule.groundHead: Struct
    get() {
        return if (!this.isGround) {
            val headVars = this.head.variables.toSet()
            val bodyVars = this.body.variables.toSet()
            val diffVars = bodyVars.subtract(headVars)
            Struct.of(this.head.functor, *(this.head.args + diffVars.toTypedArray()))
        } else {
            this.head
        }
    }

/** Applies a simple casting from a [Term] instance to a [Struct] one.
 * In case [this] is already an instance of [Struct], this function does
 * nothing if not regular casting. */
internal fun Term.safeToStruct(): Struct {
    return if (this is Struct) {
        this
    } else {
        Struct.of(this.toString())
    }
}

/** Given a [this], removes all anonymous [Var] variables contained
 * in head and body, substituting them with non-anonymous variables. */
internal fun Rule.withoutAnonymousVars(): Rule {
    val anonVarSubstitution = Substitution.of(
        this.head.variables.toSet()
            .plus(this.body.variables.toSet())
            .filter {
                it.isAnonymous
            }
            .mapIndexed { index, it ->
                Pair(it, Var.of("ANON_$index"))
            }
    )
    return when (this) {
        is Fact -> {
            Fact.of(this.head.apply(anonVarSubstitution).safeToStruct())
        }
        else -> Rule.of(
            this.head.apply(anonVarSubstitution).safeToStruct(),
            this.body.apply(anonVarSubstitution).safeToStruct()
        )
    }
}

/** Encapsulates a [BinaryDecisionDiagram] object inside a Prolog [Term] */
internal fun BinaryDecisionDiagram<ProbTerm>.toTerm(): Term {
    return ProblogObjectRef(this)
}

/** If the given term represents an arithmetic expression, this function returns
 * a [Double] containing the numeric solution of the expression. Otherwise,
 * an exception is thrown. */
internal fun Term.solveArithmeticExpression(): Double {
    val termStr = this.toString().replace("\\s".toRegex(), "")
    val regexMatch = Regex("([0-9.]+)(/)([0-9.]+).*").find(termStr)
        ?: throw TuPrologException(
            "Unsupported probability notation in annotated disjunction: $termStr"
        )
    val (dividendStr, _, divisorStr) = regexMatch.destructured
    val dividend = dividendStr.toDoubleOrNull()
    val divisor = divisorStr.toDoubleOrNull()
    if (dividend == null || divisor == null) {
        throw TuPrologException(
            "Unable to parse arithmetic division expression: $termStr"
        )
    }
    return dividend / divisor
}

/** If the given term represents an arithmetic expression, this function returns
 * a [Double] containing the numeric solution of the expression. Otherwise,
 * this returns null. */
internal fun Term.solveArithmeticExpressionOrNull(): Double? {
    return try {
        this.solveArithmeticExpression()
    } catch (e: Throwable) {
        null
    }
}
