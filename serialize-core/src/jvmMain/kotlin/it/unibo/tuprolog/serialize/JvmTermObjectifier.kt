package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Set

internal class JvmTermObjectifier : TermObjectifier {
    override fun defaultValue(term: Term): Any {
        throw IllegalStateException()
    }

    override fun objectifyMany(values: Iterable<Term>): Any {
        return values.map { objectify(it) }
    }

    override fun visitVar(term: Var): Map<String, Any> =
        mapOf(
            "var" to term.name
        )

    override fun visitTruth(term: Truth): Any =
        when (term) {
            Truth.TRUE -> true
            Truth.FALSE -> false
            Truth.FAIL -> "fail"
            else -> NotImplementedError("Serialization of Truth value: $term")
        }

    override fun visitStruct(term: Struct): Map<String, Any> =
        mapOf(
            "fun" to term.functor,
            "args" to term.argsList.map { it.accept(this) }
        )

    override fun visitAtom(term: Atom): String =
        term.value

    override fun visitInteger(term: Integer): Any =
        try {
            term.value.toLongExact()
        } catch (e: ArithmeticException) {
            mapOf(
                "integer" to term.value.toString()
            )
        }

    override fun visitReal(term: Real): Any =
        mapOf(
            "real" to term.value.toString()
        )

    override fun visitSet(term: Set): Map<String, Any> =
        mapOf(
            "set" to term.toList().map { it.accept(this) }
        )

    override fun visitEmptySet(term: EmptySet): Map<String, Any> =
        visitSet(term)

    override fun visitList(term: List): Map<String, Any> {
        val listed = term.toList()
        return if (term.isWellFormed) {
            mapOf(
                "list" to listed.map { it.accept(this) }
            )
        } else {
            mapOf(
                "list" to listed.subList(0, listed.lastIndex).map { it.accept(this) },
                "tail" to listed[listed.lastIndex].accept(this)
            )
        }
    }

    override fun visitCons(term: Cons): Map<String, Any> =
        visitList(term)

    override fun visitEmptyList(term: EmptyList): Map<String, Any> =
        visitList(term)

    override fun visitTuple(term: Tuple): Map<String, Any> =
        mapOf(
            "tuple" to term.toList().map { it.accept(this) }
        )

    override fun visitIndicator(term: Indicator): Map<String, Any> =
        mapOf(
            "name" to term.nameTerm.accept(this),
            "arity" to term.arityTerm.accept(this)
        )

    override fun visitRule(term: Rule): Map<String, Any> =
        mapOf(
            "head" to term.head.accept(this),
            "body" to term.body.accept(this)
        )

    override fun visitFact(term: Fact): Map<String, Any> =
        mapOf(
            "head" to term.head.accept(this)
        )

    override fun visitDirective(term: Directive): Map<String, Any> =
        mapOf(
            "body" to term.body.accept(this)
        )
}