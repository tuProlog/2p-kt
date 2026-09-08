@file:JvmName("Datalog")

package it.unibo.tuprolog.datalog

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.datalog.exception.DatalogViolationException
import it.unibo.tuprolog.datalog.exception.InvalidLiteralException
import it.unibo.tuprolog.datalog.visitors.CompoundFinder
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.utils.graphs.Graph
import it.unibo.tuprolog.utils.graphs.MutableGraph
import it.unibo.tuprolog.utils.graphs.edgeOf
import it.unibo.tuprolog.utils.graphs.isAcyclic
import it.unibo.tuprolog.utils.graphs.nodeOf
import kotlin.collections.List
import kotlin.jvm.JvmName

private val negationPredicates = setOf("not", "\\+")

/**
 * Whether this [Struct] is a negation literal, i.e. a unary `not(...)` or `\+(...)` term — the two negation
 * predicates recognised throughout this module (see [Clause.negatedBodyLiterals]).
 */
val Struct.isNegated
    get() = arity == 1 && functor in negationPredicates

/**
 * Reinterprets this [Term] as a Datalog/Prolog *literal* (a callable goal): a [Struct] is returned as-is,
 * while a bare [it.unibo.tuprolog.core.Var] goal (e.g. a body item coming from a higher-order call variable)
 * is wrapped into a `call/1` struct, mirroring the usual Prolog meta-call convention.
 *
 * @throws InvalidLiteralException if this [Term] is neither a [Struct] nor a [it.unibo.tuprolog.core.Var]
 * (e.g. a number or a list), and so cannot occur as a literal; [ofClause], when given, is attached to the
 * exception to identify which clause the offending term came from.
 */
internal fun Term.asLiteral(ofClause: Clause? = null) =
    when {
        isStruct -> castToStruct()
        isVar -> Struct.of("call", this)
        else -> throw InvalidLiteralException(this, ofClause)
    }

private val Clause.bodyLiterals: List<Struct>
    get() = bodyItems.map { it.asLiteral(ofClause = this) }

/**
 * All literals of this [Clause]: its [Clause.head] (if any, i.e. if this is a [Rule]), followed by every
 * goal in [Clause.bodyItems], each normalised to a [Struct] via [asLiteral].
 */
val Clause.literals: List<Struct>
    get() = (head?.let { listOf(it) } ?: emptyList()) + bodyLiterals

/** The body literals of this [Clause] that are negated, i.e. for which [isNegated] holds. */
val Clause.negatedBodyLiterals: List<Struct>
    get() = bodyLiterals.filter { it.isNegated }

/** The body literals of this [Clause] that are *not* negated, i.e. for which [isNegated] does not hold. */
val Clause.nonNegatedBodyLiterals: List<Struct>
    get() = bodyLiterals.filterNot { it.isNegated }

/**
 * Whether this [Clause] is function-free in the Datalog sense: no argument, anywhere in the head or in any
 * body literal, is a compound term (a [Struct] with [Struct.arity] `> 0`) — only constants and variables may
 * appear as arguments. For instance `parent(X, Y)` qualifies, while `parent(f(X), Y)` does not, because
 * `f(X)` is itself a compound. Backed by the [CompoundFinder] visitor.
 */
val Clause.hasNoCompound: Boolean
    get() = !this.accept(CompoundFinder)

/**
 * Asserts [hasNoCompound], i.e. that no argument of this [Clause] is a compound term.
 * @throws DatalogViolationException if [hasNoCompound] is `false`.
 */
fun Clause.ensureHasNoCompound() {
    if (!hasNoCompound) {
        throw DatalogViolationException("the clause ", this, " contains compound terms")
    }
}

/**
 * Whether every variable occurring in this [Clause]'s head also occurs in at least one of its
 * [nonNegatedBodyLiterals] — the Datalog requirement that a rule may not "invent" head variables that are
 * never bound by a positive body goal. A [it.unibo.tuprolog.core.Fact] (empty body) vacuously satisfies
 * this only if its head has no variables at all, since there are no non-negated body literals to bind them.
 */
val Clause.allHeadVariablesInNonNegatedLiterals: Boolean
    get() {
        val variablesInHead = head?.variables?.toSet() ?: emptySet()
        val variablesInNonNegatedLiterals = nonNegatedBodyLiterals.asSequence().flatMap { it.variables }.toSet()
        return variablesInHead.all { it in variablesInNonNegatedLiterals }
    }

/**
 * Asserts [allHeadVariablesInNonNegatedLiterals].
 * @throws DatalogViolationException if [allHeadVariablesInNonNegatedLiterals] is `false`.
 */
fun Clause.ensureAllHeadVariablesInNonNegatedLiterals() {
    if (!allHeadVariablesInNonNegatedLiterals) {
        throw DatalogViolationException(
            "some variable occurring in head of",
            this,
            " does not occur within any non-negated literal of the same clause",
        )
    }
}

/**
 * Whether every variable occurring in one of this [Clause]'s [negatedBodyLiterals] also occurs in at least
 * one of its [nonNegatedBodyLiterals] — the usual Datalog "safe negation" condition. It guarantees a negated
 * literal such as `not(q(Y))` is only ever checked once `Y` has already been bound to one of finitely many
 * values by a positive goal earlier in the body, rather than requiring `q` to be enumerated over its
 * (potentially infinite) universe of terms.
 */
val Clause.allNegatedLiteralsVariablesInNonNegatedLiteralsToo: Boolean
    get() {
        val variablesInNegatedLiterals = negatedBodyLiterals.asSequence().flatMap { it.variables }.toSet()
        val variablesInNonNegatedLiterals = nonNegatedBodyLiterals.asSequence().flatMap { it.variables }.toSet()
        return variablesInNegatedLiterals.all { it in variablesInNonNegatedLiterals }
    }

/**
 * Asserts [allNegatedLiteralsVariablesInNonNegatedLiteralsToo].
 * @throws DatalogViolationException if [allNegatedLiteralsVariablesInNonNegatedLiteralsToo] is `false`.
 */
fun Clause.ensureAllNegatedLiteralsVariablesInNonNegatedLiteralsToo() {
    if (!allNegatedLiteralsVariablesInNonNegatedLiteralsToo) {
        throw DatalogViolationException(
            "some variable occurring in some negated literal of ",
            this,
            " does not occur in any non-negated literal of the same clause",
        )
    }
}

private fun MutableGraph<Indicator, Boolean>.register(rule: Rule) {
    val caller = nodeOf(rule.head.indicator)
    for (literal in rule.bodyLiterals) {
        if (literal.isNegated) {
            val callee = nodeOf(literal[0].asLiteral(rule).indicator)
            add(edgeOf(caller, callee, false))
        } else {
            val callee = nodeOf(literal.indicator)
            add(edgeOf(caller, callee, true))
        }
    }
}

/**
 * The predicate call graph of this [Theory]: one node per [Indicator] (functor/arity) that occurs as a rule
 * head or as a body literal, and one edge per rule from its head's [Indicator] to each body literal's
 * [Indicator] it calls — the edge weight is `true` for a positive call and `false` for a call through
 * negation ([isNegated]). Facts contribute a node but no outgoing edge. Used by [isNonRecursive] to detect
 * (possibly indirect, possibly negated) recursion.
 */
val Theory.callGraph: Graph<Indicator, Boolean>
    get() =
        Graph.build {
            rules.forEach { register(it) }
        }

/**
 * Whether this [Theory]'s [callGraph] is acyclic, i.e. no predicate directly or transitively calls itself,
 * whether through positive or negated literals. Datalog evaluation typically tolerates recursion (that is
 * indeed one of its selling points over plain non-recursive query languages), but this module enforces a
 * stricter, recursion-free reading — see [isDatalog].
 */
val Theory.isNonRecursive: Boolean
    get() = callGraph.isAcyclic

/**
 * Asserts [isNonRecursive].
 * @throws DatalogViolationException if [isNonRecursive] is `false`.
 */
fun Theory.ensureIsNonRecursive() {
    if (!isNonRecursive) {
        throw DatalogViolationException(
            "the theory ",
            this.joinToString("", "{", "}") { "$it." },
            " contains either direct or indirect recursion",
        )
    }
}

/**
 * Whether this [Theory] belongs to the (function-free, safely-negated, non-recursive) Datalog subset of
 * Prolog recognised by this module, i.e. whether every one of its [Theory.rules] satisfies [hasNoCompound],
 * [allHeadVariablesInNonNegatedLiterals] and [allNegatedLiteralsVariablesInNonNegatedLiteralsToo], *and* the
 * theory as a whole is [isNonRecursive].
 *
 * Restricting a theory to this subset matters because, unlike a general Prolog theory, a Datalog theory is
 * guaranteed to terminate and to admit a unique minimal model when evaluated bottom-up: it cannot construct
 * arbitrarily large new terms (no compound arguments) and it cannot loop through recursive predicate calls,
 * so every derivable fact is built from the — finitely many — constants already occurring in the theory.
 * Note this module's notion of Datalog is slightly stricter than some textbook definitions on two points:
 * compound *arguments* are forbidden outright (not just newly-constructed ones), and recursion is forbidden
 * outright (not just recursion through negation).
 *
 * Example — using the `:dsl-core` builders, a clause accepted as Datalog, and one rejected for each reason:
 * ```kotlin
 * // accepted: only constants/variables as arguments, head vars covered by a positive goal, no recursion
 * rule { "ancestor"(X, Y) impliedBy "parent"(X, Y) }
 *
 * // rejected by hasNoCompound: `f(X)` is a compound argument
 * rule { "p"(X) impliedBy "q"("f"(X)) }
 *
 * // rejected by allHeadVariablesInNonNegatedLiterals: `Y` occurs in the head only
 * rule { "p"(X, Y) impliedBy "q"(X) }
 *
 * // rejected by allNegatedLiteralsVariablesInNonNegatedLiteralsToo: `Y` occurs only inside `not(...)`
 * rule { "p"(X).impliedBy("q"(X), "not"("r"(Y))) }
 *
 * // rejected by isNonRecursive: `p` calls itself (directly)
 * rule { "p"(X) impliedBy "p"(X) }
 * ```
 *
 * @see ensureIsDatalog for the throwing counterpart, which also reports *which* rule/condition failed.
 */
val Theory.isDatalog: Boolean
    get() =
        rules.all {
            it.hasNoCompound &&
                it.allHeadVariablesInNonNegatedLiterals &&
                it.allNegatedLiteralsVariablesInNonNegatedLiteralsToo
        } &&
            isNonRecursive

/**
 * Asserts [isDatalog] rule by rule, so that the resulting exception identifies precisely which rule and
 * which condition ([hasNoCompound], [allHeadVariablesInNonNegatedLiterals] or
 * [allNegatedLiteralsVariablesInNonNegatedLiteralsToo]) was violated first, before finally checking
 * [ensureIsNonRecursive] on the theory as a whole.
 *
 * @throws DatalogViolationException on the first rule/condition (in that order) that fails.
 */
fun Theory.ensureIsDatalog() {
    for (rule in rules) {
        rule.ensureHasNoCompound()
        rule.ensureAllHeadVariablesInNonNegatedLiterals()
        rule.ensureAllNegatedLiteralsVariablesInNonNegatedLiteralsToo()
    }
    ensureIsNonRecursive()
}
