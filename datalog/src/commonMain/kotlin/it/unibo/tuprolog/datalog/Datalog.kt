package it.unibo.tuprolog.datalog

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.datalog.exception.InvalidLiteralException
import it.unibo.tuprolog.datalog.visitors.CompoundFinder

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
    get() = this.accept(CompoundFinder)

val Clause.allHeadVariablesInNonNegatedLiterals: Boolean
    get() {
        val variablesInHead = head?.variables?.toSet() ?: emptySet()
        val variablesInNonNegatedLiterals = nonNegatedBodyLiterals.asSequence().flatMap { it.variables }.toSet()
        return variablesInHead.all { it in variablesInNonNegatedLiterals }
    }

val Clause.allNegatedLiteralsVariablesInNonNegatedLiteralsToo: Boolean
    get() {
        val variablesInNegatedLiterals = negatedBodyLiterals.asSequence().flatMap { it.variables }.toSet()
        val variablesInNonNegatedLiterals = nonNegatedBodyLiterals.asSequence().flatMap { it.variables }.toSet()
        return variablesInNegatedLiterals.all { it in variablesInNonNegatedLiterals }
    }
