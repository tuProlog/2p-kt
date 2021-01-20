package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Collection
import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.EmptyList
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Set
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.core.Tuple
import org.gciatto.kt.math.BigInteger

internal abstract class AbstractTermFormatter(
    protected val quoted: Boolean = true,
    protected val numberVars: Boolean = false,
    protected val ignoreOps: Boolean = false
) : TermFormatter {

    companion object {
        private val TWENTY_SIX = BigInteger.of(26)
        private const val A_INDEX = 'A'.toInt()
    }

    override fun defaultValue(term: Term): String = term.toString()

    override fun visitAtom(term: Atom): String = formatFunctor(term)

    override fun visitStruct(term: Struct): String {
        if (numberVars && isNumberedVar(term)) {
            return numberedVar(term[0] as Integer)
        }
        val functor = formatFunctor(term)
        val args = term.argsSequence.map { it.accept(childFormatter()) }.joinToString(", ", "(", ")")
        return "$functor$args"
    }

    private fun isNumberedVar(term: Struct): Boolean =
        term.functor == "\$VAR" && term.arity == 1 && term[0].let { it is Integer && it.value >= BigInteger.ZERO }

    private fun numberedVar(integer: Integer): String {
        val letterIndex = (integer.value % TWENTY_SIX).toInt() + A_INDEX
        val varNumber = integer.value / TWENTY_SIX
        return "${letterIndex.toChar()}$varNumber"
    }

    private fun formatFunctor(term: Struct): String {
        return if (quoted) {
            Struct.enquoteFunctorIfNecessary(
                Struct.escapeFunctorIfNecessary(term.functor)
            )
        } else {
            term.functor
        }
    }

    override fun visit(term: Collection): String =
        if (ignoreOps) {
            visitStruct(term)
        } else {
            super.visit(term)
        }

    override fun visit(term: Clause): String =
        if (ignoreOps) {
            visitStruct(term)
        } else {
            super.visit(term)
        }

    override fun visitSet(term: Set): String =
        term.unfoldedSequence.joinToString(", ", "{", "}") { it.accept(childFormatter()) }

    protected open fun itemFormatter(): TermFormatter = this

    protected open fun childFormatter(): TermFormatter = this

    override fun visitCons(term: Cons): String =
        with(term.unfoldedList) {
            val last = last()
            val base = subList(0, lastIndex).joinToString(", ", "[", "") {
                it.accept(itemFormatter())
            }
            val lastString = if (last is EmptyList) {
                "]"
            } else {
                " | ${last.accept(itemFormatter())}]"
            }
            return base + lastString
        }

    override fun visitTuple(term: Tuple): String =
        term.unfoldedSequence.joinToString(", ", "(", ")") { it.accept(childFormatter()) }

    override fun visitRule(term: Rule): String = visitStruct(term)

    override fun visitFact(term: Fact): String = visitRule(term)

    override fun visitDirective(term: Directive): String = visitStruct(term)

    override fun visitIndicator(term: Indicator): String = visitStruct(term)
}
