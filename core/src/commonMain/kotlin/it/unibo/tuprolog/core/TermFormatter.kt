package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.SimpleTermFormatter
import it.unibo.tuprolog.core.impl.TermFormatterWithPrettyExpressions
import it.unibo.tuprolog.core.impl.TermFormatterWithPrettyVariables
import it.unibo.tuprolog.core.operators.OperatorSet
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * A particular sort of [Formatter]s aimed at representing terms
 */
interface TermFormatter : Formatter<Term>, TermVisitor<String> {

    /**
     * Converts a [Term] into a [String]
     * @param value is the [Term] to be converted in [String]
     * @return a [String] representing the [Term] provided as argument
     */
    override fun format(value: Term): String {
        return value.accept(this)
    }

    companion object {
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
        fun prettyVariables(): TermFormatter =
            TermFormatterWithPrettyVariables()

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
        fun prettyExpressions(prettyVariables: Boolean, operatorSet: OperatorSet): TermFormatter {
            return if (prettyVariables) {
                TermFormatterWithPrettyExpressions(TermFormatterWithPrettyVariables(), operatorSet)
            } else {
                TermFormatterWithPrettyExpressions(SimpleTermFormatter, operatorSet)
            }
        }

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
        fun prettyExpressions(operatorSet: OperatorSet): TermFormatter {
            return prettyExpressions(true, operatorSet)
        }

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
        fun prettyExpressions(prettyVariables: Boolean): TermFormatter {
            return prettyExpressions(prettyVariables, OperatorSet.DEFAULT)
        }

        /**
         * A [TermFormatter] representing terms in a pretty way, i.e. by representing prefix, postfix, or infix expressions
         * according to the operators defined in [OperatorSet.DEFAULT]. Variables are represented in pretty way as well,
         * similarly to what the formatter returned by [prettyVariables] does.
         *
         * So for instance the term `A_1 + B_2` is represented as `A + B`.
         */
        @JvmStatic
        @JsName("prettyExpressionsPrettyVariablesDefaultOperators")
        fun prettyExpressions(): TermFormatter {
            return prettyExpressions(true, OperatorSet.DEFAULT)
        }
    }
}
