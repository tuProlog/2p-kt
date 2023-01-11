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

val Struct.isNegated
    get() = arity == 1 && functor in negationPredicates

internal fun Term.asLiteral(ofClause: Clause? = null) =
    when {
        isStruct -> castToStruct()
        isVar -> Struct.of("call", this)
        else -> throw InvalidLiteralException(this, ofClause)
    }

private val Clause.bodyLiterals: List<Struct>
    get() = bodyItems.map { it.asLiteral(ofClause = this) }

val Clause.literals: List<Struct>
    get() = (head?.let { listOf(it) } ?: emptyList()) + bodyLiterals

val Clause.negatedBodyLiterals: List<Struct>
    get() = bodyLiterals.filter { it.isNegated }

val Clause.nonNegatedBodyLiterals: List<Struct>
    get() = bodyLiterals.filterNot { it.isNegated }

val Clause.hasNoCompound: Boolean
    get() = !this.accept(CompoundFinder)

fun Clause.ensureHasNoCompound() {
    if (!hasNoCompound) {
        throw DatalogViolationException("the clause ", this, " contains compound terms")
    }
}

val Clause.allHeadVariablesInNonNegatedLiterals: Boolean
    get() {
        val variablesInHead = head?.variables?.toSet() ?: emptySet()
        val variablesInNonNegatedLiterals = nonNegatedBodyLiterals.asSequence().flatMap { it.variables }.toSet()
        return variablesInHead.all { it in variablesInNonNegatedLiterals }
    }

fun Clause.ensureAllHeadVariablesInNonNegatedLiterals() {
    if (!allHeadVariablesInNonNegatedLiterals) {
        throw DatalogViolationException(
            "some variable occurring in head of",
            this,
            " does not occur within any non-negated literal of the same clause"
        )
    }
}

val Clause.allNegatedLiteralsVariablesInNonNegatedLiteralsToo: Boolean
    get() {
        val variablesInNegatedLiterals = negatedBodyLiterals.asSequence().flatMap { it.variables }.toSet()
        val variablesInNonNegatedLiterals = nonNegatedBodyLiterals.asSequence().flatMap { it.variables }.toSet()
        return variablesInNegatedLiterals.all { it in variablesInNonNegatedLiterals }
    }

fun Clause.ensureAllNegatedLiteralsVariablesInNonNegatedLiteralsToo() {
    if (!allNegatedLiteralsVariablesInNonNegatedLiteralsToo) {
        throw DatalogViolationException(
            "some variable occurring in some negated literal of ",
            this,
            " does not occur in any non-negated literal of the same clause"
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

val Theory.callGraph: Graph<Indicator, Boolean>
    get() = Graph.build {
        rules.forEach { register(it) }
    }

val Theory.isNonRecursive: Boolean
    get() = callGraph.isAcyclic

fun Theory.ensureIsNonRecursive() {
    if (!isNonRecursive) {
        throw DatalogViolationException(
            "the theory ",
            this.joinToString("", "{", "}") { "$it." },
            " contains either direct or indirect recursion"
        )
    }
}

val Theory.isDatalog: Boolean
    get() = rules.all {
        it.hasNoCompound && it.allHeadVariablesInNonNegatedLiterals && it.allNegatedLiteralsVariablesInNonNegatedLiteralsToo
    } && isNonRecursive

fun Theory.ensureIsDatalog() {
    for (rule in rules) {
        rule.ensureHasNoCompound()
        rule.ensureAllHeadVariablesInNonNegatedLiterals()
        rule.ensureAllNegatedLiteralsVariablesInNonNegatedLiteralsToo()
    }
    ensureIsNonRecursive()
}
