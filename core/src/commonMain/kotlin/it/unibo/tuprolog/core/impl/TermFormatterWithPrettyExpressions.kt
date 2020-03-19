package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.operators.*
import kotlin.collections.Set

internal class TermFormatterWithPrettyExpressions(
    private val priority: Int,
    private val delegate: TermFormatter,
    private val operators: OperatorsIndex,
    private val forceParentheses: Set<String>
) : AbstractTermFormatter() {

    companion object {
        private data class OperatorDecorations(val prefix: String, val suffix: String)

        private val defaultDecorations = OperatorDecorations(" ", " ")

        private val adHocDecorations: Map<String, OperatorDecorations> = mapOf(
            "," to OperatorDecorations("", " "),
            ";" to OperatorDecorations("", " "),
            "\\+" to OperatorDecorations("", " ")
        )

        private val String.decorations: OperatorDecorations
            get() = adHocDecorations[this] ?: defaultDecorations

        private val String.prefix: String
            get() = decorations.prefix

        private val String.suffix: String
            get() = decorations.suffix
    }

    constructor(delegate: TermFormatter, operators: OperatorSet)
        : this(Int.MAX_VALUE, delegate, operators.toOperatorsIndex(), emptySet())

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

    private fun addingParenthesesIfForced(struct: Struct, stringGenerator: Struct.() -> String): String {
        val string = struct.stringGenerator()
        if (struct.functor in forceParentheses) {
            return "($string)"
        }
        return string
    }

    private fun visitExpression(struct: Struct): String {
        val prefix = struct.isPrefix()
        if (prefix != null) {
            return addingParenthesesIfForced(struct) {
                "$functor${functor.suffix}${args[0].accept(childFormatter(prefix.second))}"
            }
        }
        val postfix = struct.isPostfix()
        if (postfix != null) {
            return addingParenthesesIfForced(struct) {
                "${args[0].accept(childFormatter(postfix.second))}${functor.prefix}$functor"
            }
        }
        val infix = struct.isInfix()
        if (infix != null) {
            val childFormatter = childFormatter(infix.second, setOf(","))
            return addingParenthesesIfForced(struct) {
                "${args[0].accept(childFormatter(infix.second))}${functor.prefix}$functor${functor.suffix}${args[1].accept(childFormatter)}"
            }
        }
        val lowerPriority = struct.isLowerPriority()
        if (lowerPriority != null) {
            return "(${struct.accept(childFormatter(lowerPriority.second))})"
        }
        return super.visitStruct(struct)
    }

    override fun itemFormatter(): TermFormatter {
        return childFormatter(forceParentheses = setOf(",", "|"))
    }

    private fun childFormatter(priority: Int = Int.MAX_VALUE, forceParentheses: Set<String> = emptySet()): TermFormatter =
        TermFormatterWithPrettyExpressions(priority, delegate, operators, forceParentheses)

    private fun String.isOperator() = operators.containsKey(this)

    private fun OperatorsIndex.getSpecifierAndIndex(
        functor: String,
        priorityPredicate: (Int) -> Boolean,
        specifierPredicate: Specifier.() -> Boolean
    ): Pair<Specifier, Int>? {
        return operators[functor]?.asSequence()
            ?.filter { it.key.specifierPredicate() && priorityPredicate(it.value) }
            ?.filter { it.key.name !in forceParentheses }
            ?.map { it.toPair() }
            ?.firstOrNull()
    }

    private fun OperatorsIndex.getSpecifierAndIndexWithNonGreaterPriority(
        functor: String,
        priority: Int,
        specifierPredicate: Specifier.() -> Boolean
    ): Pair<Specifier, Int>? {
        return getSpecifierAndIndex(functor, { it <= priority }, specifierPredicate)
    }

    private fun OperatorsIndex.getSpecifierAndIndexWithGreaterPriority(
        functor: String,
        priority: Int,
        specifierPredicate: Specifier.() -> Boolean
    ): Pair<Specifier, Int>? {
        return getSpecifierAndIndex(functor, { it > priority }, specifierPredicate)
    }

    private fun Struct.isPrefix(): Pair<Specifier, Int>? =
        if (arity == 1) {
            operators.getSpecifierAndIndexWithNonGreaterPriority(functor, priority) { isPrefix }
        } else {
            null
        }

    private fun Struct.isPostfix(): Pair<Specifier, Int>? =
        if (arity == 1) {
            operators.getSpecifierAndIndexWithNonGreaterPriority(functor, priority) { isPostfix }
        } else {
            null
        }

    private fun Struct.isInfix(): Pair<Specifier, Int>? =
        if (arity == 2) {
            operators.getSpecifierAndIndexWithNonGreaterPriority(functor, priority) { isInfix }
        } else {
            null
        }

    private fun Struct.isLowerPriority(): Pair<Specifier, Int>? =
        when (arity) {
            1 -> operators.getSpecifierAndIndexWithGreaterPriority(functor, priority) { isPrefix || isPostfix }
            2 -> operators.getSpecifierAndIndexWithGreaterPriority(functor, priority) { isInfix }
            else -> null
        }

    override fun visitRule(term: Rule): String =
        visitExpression(term)

    override fun visitFact(term: Fact): String =
        term.head.accept(this)

    override fun visitDirective(term: Directive): String =
        visitExpression(term)
}