package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Block
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.EmptyList
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Recursive
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.core.Tuple
import org.gciatto.kt.math.BigInteger

internal abstract class AbstractTermFormatter(
    protected val quoted: Boolean = true,
    protected val numberVars: Boolean = false,
    protected val ignoreOps: Boolean = false,
) : TermFormatter {
    companion object {
        private val TWENTY_SIX = BigInteger.of(26)
        private const val A_INDEX = 'A'.code
    }

    override fun defaultValue(term: Term): String = term.toString()

    override fun visitAtom(term: Atom): String = formatFunctor(term)

    override fun visitStruct(term: Struct): String {
        if (numberVars && isNumberedVar(term)) {
            return numberedVar(term[0].castToInteger())
        }
        val functor = formatFunctor(term)
        val args = term.argsSequence.map { it.accept(childFormatter()) }.joinToString(", ", "(", ")")
        return "$functor$args"
    }

    private fun isNumberedVar(term: Struct): Boolean =
        term.functor == "\$VAR" &&
            term.arity == 1 &&
            term[0].let {
                it.isInteger && it.castToInteger().value >= BigInteger.ZERO
            }

    private fun numberedVar(integer: Integer): String {
        val letterIndex = (integer.value % TWENTY_SIX).toInt() + A_INDEX
        val varNumber = integer.value / TWENTY_SIX
        return "${letterIndex.toChar()}$varNumber"
    }

    private fun formatFunctor(term: Struct): String =
        if (quoted) {
            Struct.enquoteFunctorIfNecessary(
                Struct.escapeFunctorIfNecessary(term.functor),
            )
        } else {
            term.functor
        }

    private fun <T : Struct> visitStructImpl(
        term: T,
        actualVisit: (T) -> String,
    ): String =
        if (ignoreOps) {
            visitStruct(term)
        } else {
            actualVisit(term)
        }

    override fun visitCollection(term: Recursive): String = visitStructImpl(term) { defaultValue(term) }

    override fun visitList(term: List): String = visitCollection(term)

    override fun visitEmptyList(term: EmptyList): String = defaultValue(term)

    override fun visitCons(term: Cons): String =
        visitStructImpl(term) {
            with(term.unfoldedList) {
                val last = last()
                val base =
                    subList(0, lastIndex).joinToString(", ", "[", "") {
                        it.accept(itemFormatter())
                    }
                val lastString =
                    if (last.isEmptyList) {
                        "]"
                    } else {
                        " | ${last.accept(itemFormatter())}]"
                    }
                base + lastString
            }
        }

    override fun visitBlock(term: Block): String =
        visitStructImpl(term) {
            term.unfoldedSequence.joinToString(", ", "{", "}") { it.accept(childFormatter()) }
        }

    override fun visitTuple(term: Tuple): String =
        visitStructImpl(term) {
            term.unfoldedSequence.joinToString(", ", "(", ")") { it.accept(childFormatter()) }
        }

    override fun visitClause(term: Clause): String =
        visitStructImpl(term) {
            term.accept(this)
        }

    override fun visitRule(term: Rule): String = visitStruct(term)

    override fun visitFact(term: Fact): String = visitRule(term)

    override fun visitDirective(term: Directive): String = visitStruct(term)

    protected open fun itemFormatter(): TermFormatter = this

    protected open fun childFormatter(): TermFormatter = this

    override fun visitIndicator(term: Indicator): String = visitStruct(term)
}
