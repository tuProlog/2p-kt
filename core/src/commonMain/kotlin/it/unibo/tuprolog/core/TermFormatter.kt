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
        ): TermFormatter {
            val quoted = funcFormat == QUOTED_IF_NECESSARY
            val ignoreOps = opFormat == IGNORE_OPERATORS
            val inner =
                when (varFormat) {
                    COMPLETE_NAME -> SimpleTermFormatter(quoted, numberVars, ignoreOps)
                    UNDERSCORE -> TermFormatterWithAnonymousVariables(quoted, numberVars, ignoreOps)
                    PRETTY -> TermFormatterWithPrettyVariables(quoted, numberVars, ignoreOps)
                }
            return when (opFormat) {
                EXPRESSIONS -> TermFormatterWithPrettyExpressions(inner, operators, quoted, numberVars, ignoreOps)
                else -> inner
            }
        }

        @JvmStatic
        @JsName("default")
        fun default(operators: OperatorSet = OperatorSet.DEFAULT): TermFormatter =
            of(UNDERSCORE, EXPRESSIONS, LITERAL, true, operators)

        @JvmStatic
        @JsName("canonical")
        fun canonical(): TermFormatter = of(UNDERSCORE, IGNORE_OPERATORS, QUOTED_IF_NECESSARY, numberVars = false)

        @JvmStatic
        @JsName("readable")
        fun readable(operators: OperatorSet = OperatorSet.DEFAULT): TermFormatter =
            of(PRETTY, EXPRESSIONS, QUOTED_IF_NECESSARY, numberVars = true, operators)

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
        fun prettyVariables(): TermFormatter = of(PRETTY, COLLECTIONS)

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
        ): TermFormatter = of(if (prettyVariables) PRETTY else COMPLETE_NAME, EXPRESSIONS, operators = operatorSet)

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
        fun prettyExpressions(operatorSet: OperatorSet): TermFormatter = prettyExpressions(true, operatorSet)

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
        fun prettyExpressions(prettyVariables: Boolean): TermFormatter =
            prettyExpressions(prettyVariables, OperatorSet.DEFAULT)

        /**
         * A [TermFormatter] representing terms in a pretty way, i.e. by representing prefix, postfix, or infix expressions
         * according to the operators defined in [OperatorSet.DEFAULT]. Variables are represented in pretty way as well,
         * similarly to what the formatter returned by [prettyVariables] does.
         *
         * So for instance the term `A_1 + B_2` is represented as `A + B`.
         */
        @JvmStatic
        @JsName("prettyExpressionsPrettyVariablesDefaultOperators")
        fun prettyExpressions(): TermFormatter = prettyExpressions(true, OperatorSet.DEFAULT)
    }
}
