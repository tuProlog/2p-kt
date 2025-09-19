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
internal class JvmTermObjectifier : TermObjectifier {
    override fun defaultValue(term: Term): Any {
        error("Cannot objectify term: $term")
    }

    override fun objectifyMany(values: Iterable<Term>): Any = values.map { objectify(it) }

    override fun visitVar(term: Var): Map<String, Any> =
        mapOf(
            "var" to term.name,
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
            "args" to term.args.map { it.accept(this) },
        )

    override fun visitAtom(term: Atom): String = term.value

    @Suppress("SwallowedException")
    override fun visitInteger(term: Integer): Any =
        try {
            term.value.toLongExact()
        } catch (e: ArithmeticException) {
            mapOf(
                "integer" to term.value.toString(),
            )
        }

    override fun visitReal(term: Real): Any =
        mapOf(
            "real" to term.value.toString(),
        )

    override fun visitBlock(term: Block): Map<String, Any> =
        mapOf(
            "block" to term.toList().map { it.accept(this) },
        )

    override fun visitEmptyBlock(term: EmptyBlock): Map<String, Any> = visitBlock(term)

    override fun visitList(term: List): Map<String, Any> {
        val listed = term.toList()
        return if (term.isWellFormed) {
            mapOf(
                "list" to listed.map { it.accept(this) },
            )
        } else {
            mapOf(
                "list" to listed.subList(0, listed.lastIndex).map { it.accept(this) },
                "tail" to listed[listed.lastIndex].accept(this),
            )
        }
    }

    override fun visitCons(term: Cons): Map<String, Any> = visitList(term)

    override fun visitEmptyList(term: EmptyList): Map<String, Any> = visitList(term)

    override fun visitTuple(term: Tuple): Map<String, Any> =
        mapOf(
            "tuple" to term.toList().map { it.accept(this) },
        )

    override fun visitIndicator(term: Indicator): Map<String, Any> =
        mapOf(
            "name" to term.nameTerm.accept(this),
            "arity" to term.arityTerm.accept(this),
        )

    override fun visitRule(term: Rule): Map<String, Any> =
        mapOf(
            "head" to term.head.accept(this),
            "body" to term.body.accept(this),
        )

    override fun visitFact(term: Fact): Map<String, Any> =
        mapOf(
            "head" to term.head.accept(this),
        )

    override fun visitDirective(term: Directive): Map<String, Any> =
        mapOf(
            "body" to term.body.accept(this),
        )
}
