package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.EmptyBlock
import it.unibo.tuprolog.core.EmptyList
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.core.Terms.TUPLE_FUNCTOR
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.operators.OperatorsIndex
import it.unibo.tuprolog.core.operators.Specifier
import it.unibo.tuprolog.core.operators.toOperatorsIndex
import kotlin.collections.Set

internal class TermFormatterWithPrettyExpressions private constructor(
    private val priority: Int,
    private val delegate: TermFormatter,
    private val operators: OperatorsIndex,
    private val forceParentheses: Set<String>,
    quoted: Boolean = true,
    numberVars: Boolean = false,
    ignoreOps: Boolean = false,
    tagsOptions: TermFormatter.TagsFormattingOptions = TermFormatter.TagsFormattingOptions(),
) : AbstractTermFormatter(quoted, numberVars, ignoreOps, tagsOptions) {
    companion object {
        private data class OperatorDecorations(
            val prefix: String,
            val suffix: String,
        )

        private val defaultDecorations = OperatorDecorations(" ", " ")

        private val SIGNS = setOf("-", "+")

        private const val TUPLE_ITEM_PRIORITY = 999

        /**
         * The highest priority the [index]-th operand of an operator with this specifier and priority may have
         * unparenthesised: the priority itself on a `y` side, one less on an `x` side.
         */
        private fun Pair<Specifier, Int>.argumentPriority(index: Int): Int {
            val (specifier, priority) = this
            val side = specifier.name.filter { it != 'F' }[index]
            return if (side == 'Y') priority else priority - 1
        }

        private val adHocDecorations: Map<String, OperatorDecorations> =
            mapOf(
                "," to OperatorDecorations("", " "),
                ";" to OperatorDecorations("", " "),
                "\\+" to OperatorDecorations("", " "),
            )

        private val String.decorations: OperatorDecorations
            get() = adHocDecorations[this] ?: defaultDecorations

        private val String.prefix: String
            get() = decorations.prefix

        private val String.suffix: String
            get() = decorations.suffix
    }

    constructor(
        delegate: TermFormatter,
        operators: OperatorSet,
        quoted: Boolean = true,
        numberVars: Boolean = false,
        ignoreOps: Boolean = false,
        tagsOptions: TermFormatter.TagsFormattingOptions = TermFormatter.TagsFormattingOptions(),
    ) : this(
        Int.MAX_VALUE,
        delegate,
        operators.toOperatorsIndex(),
        emptySet(),
        quoted,
        numberVars,
        ignoreOps,
        tagsOptions,
    )

    override fun visitVar(term: Var): String = term.accept(delegate)

    override fun visitAtom(term: Atom): String = term.accept(delegate)

    override fun visitInteger(term: Integer): String = term.accept(delegate)

    override fun visitReal(term: Real): String = term.accept(delegate)

    override fun visitEmptyBlock(term: EmptyBlock): String = term.accept(delegate)

    override fun visitEmptyList(term: EmptyList): String = term.accept(delegate)

    override fun visitStruct(term: Struct): String =
        if (term.functor.isOperator()) {
            visitExpression(term)
        } else {
            super.visitStruct(term)
        }

    private fun String.wrapWithinParentheses(): String = "($this)"

    private fun addingParenthesesIfForced(
        struct: Struct,
        stringGenerator: Struct.() -> String,
    ): String {
        var string = struct.stringGenerator()
        val tags = tagsOptions.formatTags(struct.tags)
        if (struct.functor in forceParentheses || tags.isNotEmpty()) {
            string = string.wrapWithinParentheses()
        }
        return string + tags
    }

    override fun visitTuple(term: Tuple): String =
        addingParenthesesIfForced(term) {
            val op = TUPLE_FUNCTOR
            // `,` is xfy 1000: an item must bind tighter, or `(a :- b), c` would read back as `a :- (b, c)`
            term.unfoldedSequence.joinToString("${op.prefix}$op${op.suffix}") {
                it.accept(childFormatter(TUPLE_ITEM_PRIORITY, setOf(",", "|")))
            }
        }

    private fun visitExpression(struct: Struct): String {
        val prefix = struct.isPrefix()
        if (prefix != null) {
            return addingParenthesesIfForced(struct) {
                val arg = getArgAt(0).accept(childFormatter(prefix.argumentPriority(0)))
                // `- 1` would read back as the number -1, so a sign applied to a number keeps the canonical syntax
                if (getArgAt(0) is Numeric && functor in SIGNS) "$functor($arg)" else "$functor${functor.suffix}$arg"
            }
        }
        val postfix = struct.isPostfix()
        if (postfix != null) {
            return addingParenthesesIfForced(struct) {
                "${getArgAt(0).accept(childFormatter(postfix.argumentPriority(0)))}${functor.prefix}$functor"
            }
        }
        val infix = struct.isInfix()
        if (infix != null) {
            return addingParenthesesIfForced(struct) {
                getArgAt(0).accept(childFormatter(infix.argumentPriority(0))) +
                    "${functor.prefix}$functor${functor.suffix}" +
                    getArgAt(1).accept(childFormatter(infix.argumentPriority(1)))
            }
        }
        val lowerPriority = struct.isLowerPriority()
        if (lowerPriority != null) {
            return struct
                .accept(childFormatter(lowerPriority.second))
                .wrapWithinParentheses()
                .plusTagsOf(struct)
        }
        return super.visitStruct(struct)
    }

    override fun itemFormatter(): TermFormatter = childFormatter(Int.MAX_VALUE, setOf(",", "|"))

    override fun childFormatter(): TermFormatter = childFormatter(Int.MAX_VALUE, setOf(","))

    private fun childFormatter(
        priority: Int,
        forceParentheses: Set<String> = emptySet(),
    ): TermFormatter =
        TermFormatterWithPrettyExpressions(
            priority,
            delegate,
            operators,
            forceParentheses,
            quoted,
            numberVars,
            ignoreOps,
            tagsOptions,
        )

    private fun String.isOperator() = operators.containsKey(this)

    private fun OperatorsIndex.getSpecifierAndIndex(
        functor: String,
        priorityPredicate: (Int) -> Boolean,
        specifierPredicate: Specifier.() -> Boolean,
    ): Pair<Specifier, Int>? =
        this[functor]
            ?.asSequence()
            ?.filter { it.key.specifierPredicate() && priorityPredicate(it.value) }
            ?.filter { it.key.name !in forceParentheses }
            ?.map { it.toPair() }
            ?.firstOrNull()

    private fun OperatorsIndex.getSpecifierAndIndexWithNonGreaterPriority(
        functor: String,
        priority: Int,
        specifierPredicate: Specifier.() -> Boolean,
    ): Pair<Specifier, Int>? = getSpecifierAndIndex(functor, { it <= priority }, specifierPredicate)

    private fun OperatorsIndex.getSpecifierAndIndexWithGreaterPriority(
        functor: String,
        priority: Int,
        specifierPredicate: Specifier.() -> Boolean,
    ): Pair<Specifier, Int>? = getSpecifierAndIndex(functor, { it > priority }, specifierPredicate)

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
}
