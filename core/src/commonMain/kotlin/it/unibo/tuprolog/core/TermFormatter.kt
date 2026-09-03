package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.TermFormatter.FuncFormat.LITERAL
import it.unibo.tuprolog.core.TermFormatter.FuncFormat.QUOTED_IF_NECESSARY
import it.unibo.tuprolog.core.TermFormatter.OpFormat.COLLECTIONS
import it.unibo.tuprolog.core.TermFormatter.OpFormat.EXPRESSIONS
import it.unibo.tuprolog.core.TermFormatter.OpFormat.IGNORE_OPERATORS
import it.unibo.tuprolog.core.TermFormatter.VarFormat.COMPLETE_NAME
import it.unibo.tuprolog.core.TermFormatter.VarFormat.PRETTY
import it.unibo.tuprolog.core.TermFormatter.VarFormat.UNDERSCORE
import it.unibo.tuprolog.core.impl.SimpleTermFormatter
import it.unibo.tuprolog.core.impl.TermFormatterWithAnonymousVariables
import it.unibo.tuprolog.core.impl.TermFormatterWithPrettyExpressions
import it.unibo.tuprolog.core.impl.TermFormatterWithPrettyVariables
import it.unibo.tuprolog.core.operators.OperatorSet
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * A particular sort of [Formatter]s aimed at representing terms
 */
interface TermFormatter :
    Formatter<Term>,
    TermVisitor<String> {
    enum class VarFormat {
        COMPLETE_NAME,
        UNDERSCORE,
        PRETTY,
    }

    enum class OpFormat {
        IGNORE_OPERATORS,
        COLLECTIONS,
        EXPRESSIONS,
    }

    enum class FuncFormat {
        QUOTED_IF_NECESSARY,
        LITERAL,
    }

    data class TagsFormattingOptions(
        val showTags: Boolean = false,
        val delimiters: Pair<String, String> = "<" to ">",
        val separator: String = ", ",
        val showKeys: Boolean = true,
        val showDelimitersIfEmpty: Boolean = false,
        val keyFilter: (String) -> Boolean = { true },
        val tagFormatter: Formatter<Any>? = null,
    ) {
        fun formatTags(tags: Map<String, Any>): String {
            if (!showTags) return ""
            val filteredTags = tags.filterKeys(keyFilter)
            if (filteredTags.isEmpty()) {
                return if (showDelimitersIfEmpty) "${delimiters.first}${delimiters.second}" else ""
            }
            val formattedTags =
                filteredTags.entries.joinToString(separator) { (key, value) ->
                    val formattedValue = tagFormatter?.format(value) ?: value.toString()
                    if (showKeys) "$key=$formattedValue" else formattedValue
                }
            return "${delimiters.first}$formattedTags${delimiters.second}"
        }
    }

    /**
     * Converts a [Term] into a [String]
     * @param value is the [Term] to be converted in [String]
     * @return a [String] representing the [Term] provided as argument
     */
    override fun format(value: Term): String = value.accept(this)

    companion object {
        @JvmStatic
        @JsName("of")
        fun of(
            varFormat: VarFormat,
            opFormat: OpFormat,
            funcFormat: FuncFormat = QUOTED_IF_NECESSARY,
            numberVars: Boolean = false,
            operators: OperatorSet = OperatorSet.DEFAULT,
            tagsOptions: TagsFormattingOptions = TagsFormattingOptions(),
        ): TermFormatter {
            val quoted = funcFormat == QUOTED_IF_NECESSARY
            val ignoreOps = opFormat == IGNORE_OPERATORS
            val inner =
                when (varFormat) {
                    COMPLETE_NAME -> SimpleTermFormatter(quoted, numberVars, ignoreOps, tagsOptions)
                    UNDERSCORE -> TermFormatterWithAnonymousVariables(quoted, numberVars, ignoreOps, tagsOptions)
                    PRETTY -> TermFormatterWithPrettyVariables(quoted, numberVars, ignoreOps, tagsOptions)
                }
            return when (opFormat) {
                EXPRESSIONS ->
                    TermFormatterWithPrettyExpressions(
                        inner,
                        operators,
                        quoted,
                        numberVars,
                        ignoreOps,
                        tagsOptions,
                    )
                else -> inner
            }
        }

        @JvmStatic
        @JsName("default")
        fun default(
            operators: OperatorSet = OperatorSet.DEFAULT,
            tagsOptions: TagsFormattingOptions = TagsFormattingOptions(showTags = true),
        ): TermFormatter = of(UNDERSCORE, EXPRESSIONS, LITERAL, true, operators, tagsOptions)

        @JvmStatic
        @JsName("canonical")
        fun canonical(): TermFormatter = of(UNDERSCORE, IGNORE_OPERATORS, QUOTED_IF_NECESSARY, numberVars = false)

        @JvmStatic
        @JsName("readable")
        fun readable(
            operators: OperatorSet = OperatorSet.DEFAULT,
            tagsOptions: TagsFormattingOptions = TagsFormattingOptions(),
        ): TermFormatter = of(PRETTY, EXPRESSIONS, QUOTED_IF_NECESSARY, numberVars = true, operators, tagsOptions)

        /**
         * A [TermFormatter] representing terms in _canonical_ form, except for [Var]iables which are represented
         * using their simple name only, if possible.
         * So for instance the term `A_1 + B_2` is represented as `'+'(A, B)`.
         * Conversely, if two or more variables share the same simple name, they are represented through relative and
         * progressive indexes.
         * So for instance, the term `A_3 + A_4` is represented as `'+'(A1, A2)`.
         */
        @JvmStatic
        @JsName("prettyVariables")
        fun prettyVariables(tagsOptions: TagsFormattingOptions = TagsFormattingOptions()): TermFormatter =
            of(PRETTY, COLLECTIONS, tagsOptions = tagsOptions)

        /**
         * A [TermFormatter] representing terms in a pretty way, i.e. by representing prefix, postfix, or infix expressions
         * according to the provided [OperatorSet]. Variables may be represented either in a pretty way or through their
         * raw representation, depending on the value of the [prettyVariables].
         *
         * So for instance, assuming an infix operator `+` is contained in [operatorSet], the term `A_1 + B_2` is
         * would be represented as `A + B`, if [prettyVariables] is `true`, or `A_1 + B_2` otherwise.
         */
        @JvmStatic
        @JsName("prettyExpressions")
        fun prettyExpressions(
            prettyVariables: Boolean,
            operatorSet: OperatorSet,
            tagsOptions: TagsFormattingOptions = TagsFormattingOptions(),
        ): TermFormatter =
            of(
                if (prettyVariables) PRETTY else COMPLETE_NAME,
                EXPRESSIONS,
                operators = operatorSet,
                tagsOptions = tagsOptions,
            )

        /**
         * A [TermFormatter] representing terms in a pretty way, i.e. by representing prefix, postfix, or infix expressions
         * according to the provided [OperatorSet]. Variables are represented in pretty way as well, similarly to what
         * the formatter returned by [prettyVariables] does.
         *
         * So for instance, assuming an infix operator `+` is contained in [operatorSet], the term `A_1 + B_2` is
         * would be represented as `A + B`.
         */
        @JvmStatic
        @JsName("prettyExpressionsPrettyVariables")
        fun prettyExpressions(
            operatorSet: OperatorSet,
            tagsOptions: TagsFormattingOptions = TagsFormattingOptions(),
        ): TermFormatter = prettyExpressions(true, operatorSet, tagsOptions)

        /**
         * A [TermFormatter] representing terms in a pretty way, i.e. by representing prefix, postfix, or infix expressions
         * according to the operators defined in [OperatorSet.DEFAULT]. Variables may be represented either in a pretty
         * way or through their raw representation, depending on the value of the [prettyVariables].
         *
         * So for instance the term `A_1 + B_2` is represented as `A + B`, if [prettyVariables] is `true`,
         * or `A_1 + B_2` otherwise.
         */
        @JvmStatic
        @JsName("prettyExpressionsDefaultOperators")
        fun prettyExpressions(
            prettyVariables: Boolean,
            tagsOptions: TagsFormattingOptions = TagsFormattingOptions(),
        ): TermFormatter = prettyExpressions(prettyVariables, OperatorSet.DEFAULT, tagsOptions)

        /**
         * A [TermFormatter] representing terms in a pretty way, i.e. by representing prefix, postfix, or infix expressions
         * according to the operators defined in [OperatorSet.DEFAULT]. Variables are represented in pretty way as well,
         * similarly to what the formatter returned by [prettyVariables] does.
         *
         * So for instance the term `A_1 + B_2` is represented as `A + B`.
         */
        @JvmStatic
        @JsName("prettyExpressionsPrettyVariablesDefaultOperators")
        fun prettyExpressions(tagsOptions: TagsFormattingOptions = TagsFormattingOptions()): TermFormatter =
            prettyExpressions(true, OperatorSet.DEFAULT, tagsOptions)
    }
}
