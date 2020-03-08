package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.operators.*

internal class TermFormatterWithPrettyExpressions(
    private val priority: Int,
    private val delegate: TermFormatter,
    private val operators: OperatorsIndex
) : AbstractTermFormatter() {

    public constructor(delegate: TermFormatter, operators: OperatorSet)
        : this(Int.MAX_VALUE, delegate, operators.toOperatorsIndex())

    override fun visitVar(term: Var): String =
        term.accept(delegate)

    override fun visitAtom(term: Atom): String =
        term.accept(delegate)

    override fun visitInteger(term: Integer): String =
        term.accept(delegate)

    override fun visitReal(term: Real): String =
        term.accept(delegate)

    override fun visitEmptySet(term: EmptySet): String =
        term.accept(delegate)

    override fun visitEmptyList(term: EmptyList): String =
        term.accept(delegate)

    override fun visitStruct(term: Struct): String {
        return if (term.functor.isOperator()) {
            visitExpression(term)
        } else {
            super.visitStruct(term)
        }
    }

    private fun visitExpression(struct: Struct): String {
        val prefix = struct.isPrefix()
        if (prefix != null) {
            return "${struct.functor} ${struct[0].accept(subFormatter(prefix.second))}"
        }
        val postfix = struct.isPostfix()
        if (postfix != null) {
            return "${struct[0].accept(subFormatter(postfix.second))} ${struct.functor} "
        }
        val infix = struct.isInfix()
        if (infix != null) {
            return "${struct[0].accept(subFormatter(infix.second))} ${struct.functor} ${struct[1].accept(subFormatter(infix.second))}"
        }
        val lowerPriority = struct.isLowerPriority()
        if (lowerPriority != null) {
            return "(${struct.accept(subFormatter(lowerPriority.second))})"
        }
        return super.visitStruct(struct)
    }

    private fun subFormatter(priority: Int): TermFormatterWithPrettyExpressions =
        TermFormatterWithPrettyExpressions(priority, delegate, operators)


    private fun String.isOperator() = operators.containsKey(this)

    private fun Struct.isPrefix(): Pair<Specifier, Int>? =
        if (arity == 1) operators[functor]?.asSequence()
            ?.filter { it.key.isPrefix && it.value <= priority }
            ?.map { it.toPair() }
            ?.firstOrNull()
        else null


    private fun Struct.isPostfix(): Pair<Specifier, Int>? =
        if (arity == 1) operators[functor]?.asSequence()
            ?.filter { it.key.isPostfix && it.value <= priority }
            ?.map { it.toPair() }
            ?.firstOrNull()
        else null

    private fun Struct.isInfix(): Pair<Specifier, Int>? =
        if (arity == 2) operators[functor]?.asSequence()
            ?.filter { it.key.isInfix && it.value <= priority }
            ?.map { it.toPair() }
            ?.firstOrNull()
        else null

    private fun Struct.isLowerPriority(): Pair<Specifier, Int>? =
        when (arity) {
            1 -> operators[functor]?.asSequence()
                ?.filter { (it.key.isPrefix || it.key.isPostfix) && it.value > priority }
                ?.map { it.toPair() }
                ?.firstOrNull()
            2 -> operators[functor]?.asSequence()
                ?.filter { it.key.isInfix && it.value > priority }
                ?.map { it.toPair() }
                ?.firstOrNull()
            else -> null
        }

    override fun visitRule(term: Rule): String =
        visitExpression(term)

    override fun visitFact(term: Fact): String =
        term.head.accept(this)

    override fun visitDirective(term: Directive): String =
        visitExpression(term)
}