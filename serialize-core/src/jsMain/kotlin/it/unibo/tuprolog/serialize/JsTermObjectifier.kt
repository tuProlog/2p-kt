package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.*

internal class JsTermObjectifier : TermObjectifier {
    override fun defaultValue(term: Term): Any {
        throw IllegalStateException()
    }

    override fun objectifyMany(values: Iterable<Term>): Any {
        return values.map { objectify(it) }.toTypedArray()
    }

    override fun visitVar(term: Var): dynamic =
        jsObject(
            "var" to term.name
        )

    override fun visitTruth(term: Truth): Any =
        when (term) {
            Truth.TRUE -> true
            Truth.FALSE -> false
            Truth.FAIL -> "fail"
            else -> NotImplementedError("Serialization of Truth value: $term")
        }

    override fun visitStruct(term: Struct): Any =
        jsObject(
            "fun" to term.functor,
            "args" to term.argsList.map { it.accept(this) }
        )

    override fun visitAtom(term: Atom): String =
        term.value

    override fun visitInteger(term: Integer): dynamic =
        try {
            term.value.toIntExact()
        } catch (e: ArithmeticException) {
            jsObject(
                "integer" to term.value.toString()
            )
        }

    override fun visitReal(term: Real): Any =
        jsObject(
            "real" to term.value.toString().let {
                if ("." !in it) {
                    "$it.0"
                } else {
                    it
                }
            }
        )

    override fun visitSet(term: Set): Any =
        jsObject(
            "set" to term.toList().map { it.accept(this) }
        )

    override fun visitEmptySet(term: EmptySet): Any =
        visitSet(term)

    override fun visitList(term: List): Any =
        jsObject("list" to term.toList().map { it.accept(this) }) {
            if (!term.isWellFormed) {
                this["tail"] = term.unfoldedSequence.last().accept(this@JsTermObjectifier)
            }
        }

    override fun visitCons(term: Cons): Any =
        visitList(term)

    override fun visitEmptyList(term: EmptyList): Any =
        visitList(term)

    override fun visitTuple(term: Tuple): Any =
        jsObject(
            "tuple" to term.toList().map { it.accept(this) }
        )

    override fun visitIndicator(term: Indicator): Any =
        jsObject(
            "name" to term.nameTerm.accept(this),
            "arity" to term.arityTerm.accept(this)
        )

    override fun visitRule(term: Rule): Any =
        jsObject(
            "head" to term.head.accept(this),
            "body" to term.body.accept(this)
        )

    override fun visitFact(term: Fact): Any =
        jsObject(
            "head" to term.head.accept(this)
        )

    override fun visitDirective(term: Directive): Any =
        jsObject(
            "body" to term.body.accept(this)
        )
}