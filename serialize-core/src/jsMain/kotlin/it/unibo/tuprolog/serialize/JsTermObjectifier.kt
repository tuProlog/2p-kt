package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Block
import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.EmptyBlock
import it.unibo.tuprolog.core.EmptyList
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var

@Suppress("TooManyFunctions")
internal class JsTermObjectifier : TermObjectifier {
    override fun defaultValue(term: Term): Any {
        error("Cannot convert to object: $term")
    }

    override fun objectifyMany(values: Iterable<Term>): Any = values.map { objectify(it) }.toTypedArray()

    override fun visitVar(term: Var): dynamic =
        jsObject(
            "var" to term.name,
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
            "args" to term.args.map { it.accept(this) },
        )

    override fun visitAtom(term: Atom): String = term.value

    @Suppress("SwallowedException")
    override fun visitInteger(term: Integer): dynamic =
        try {
            term.value.toIntExact()
        } catch (e: ArithmeticException) {
            jsObject(
                "integer" to term.value.toString(),
            )
        }

    override fun visitReal(term: Real): Any =
        jsObject(
            "real" to
                term.value.toString().let {
                    if ("." !in it) {
                        "$it.0"
                    } else {
                        it
                    }
                },
        )

    override fun visitBlock(term: Block): Any =
        jsObject(
            "block" to term.toList().map { it.accept(this) },
        )

    override fun visitEmptyBlock(term: EmptyBlock): Any = visitBlock(term)

    override fun visitList(term: List): Any {
        val listed = term.toList()
        return if (term.isWellFormed) {
            jsObject(
                "list" to listed.map { it.accept(this) }.toTypedArray(),
            )
        } else {
            jsObject(
                "list" to listed.subList(0, listed.lastIndex).map { it.accept(this) }.toTypedArray(),
                "tail" to listed[listed.lastIndex].accept(this),
            )
        }
    }

    override fun visitCons(term: Cons): Any = visitList(term)

    override fun visitEmptyList(term: EmptyList): Any = visitList(term)

    override fun visitTuple(term: Tuple): Any =
        jsObject(
            "tuple" to term.toList().map { it.accept(this) },
        )

    override fun visitIndicator(term: Indicator): Any =
        jsObject(
            "name" to term.nameTerm.accept(this),
            "arity" to term.arityTerm.accept(this),
        )

    override fun visitRule(term: Rule): Any =
        jsObject(
            "head" to term.head.accept(this),
            "body" to term.body.accept(this),
        )

    override fun visitFact(term: Fact): Any =
        jsObject(
            "head" to term.head.accept(this),
        )

    override fun visitDirective(term: Directive): Any =
        jsObject(
            "body" to term.body.accept(this),
        )
}
