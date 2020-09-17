package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Constant
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface TermParser {

    @JsName("defaultOperatorSet")
    val defaultOperatorSet: OperatorSet

    @JsName("parseTermWithOperators")
    fun parseTerm(input: String, operators: OperatorSet): Term

    @JsName("parseTerm")
    fun parseTerm(input: String): Term =
        parseTerm(input, defaultOperatorSet)

    @JsName("parseStructWithOperators")
    fun parseStruct(input: String, operators: OperatorSet): Struct =
        parseTerm(input, operators) as Struct

    @JsName("parseStruct")
    fun parseStruct(input: String): Struct =
        parseStruct(input, defaultOperatorSet)

    @JsName("parseConstantWithOperators")
    fun parseConstant(input: String, operators: OperatorSet): Constant =
        parseTerm(input, operators) as Constant

    @JsName("parseConstant")
    fun parseConstant(input: String): Constant =
        parseConstant(input, defaultOperatorSet)

    @JsName("parseVarWithOperators")
    fun parseVar(input: String, operators: OperatorSet): Var =
        parseTerm(input, operators) as Var

    @JsName("parseVar")
    fun parseVar(input: String): Var =
        parseVar(input, defaultOperatorSet)

    @JsName("parseAtomWithOperators")
    fun parseAtom(input: String, operators: OperatorSet): Atom =
        parseTerm(input, operators) as Atom

    @JsName("parseAtom")
    fun parseAtom(input: String): Atom =
        parseAtom(input, defaultOperatorSet)

    @JsName("parseNumericWithOperators")
    fun parseNumeric(input: String, operators: OperatorSet): Numeric =
        parseTerm(input, operators) as Numeric

    @JsName("parseNumeric")
    fun parseNumeric(input: String): Numeric =
        parseNumeric(input, defaultOperatorSet)

    @JsName("parseIntegerWithOperators")
    fun parseInteger(input: String, operators: OperatorSet): Integer =
        parseTerm(input, operators) as Integer

    @JsName("parseInteger")
    fun parseInteger(input: String): Integer =
        parseInteger(input, defaultOperatorSet)

    @JsName("parseRealWithOperators")
    fun parseReal(input: String, operators: OperatorSet): Real =
        parseTerm(input, operators) as Real

    @JsName("parseReal")
    fun parseReal(input: String): Real =
        parseReal(input, defaultOperatorSet)

    @JsName("parseClauseWithOperators")
    fun parseClause(input: String, operators: OperatorSet): Clause {
        require(operators.any { it.functor == Clause.FUNCTOR }) {
            "Error while parsing string as Clause: the provided operator set has no " +
                "operator for '${Clause.FUNCTOR}'/1 or '${Clause.FUNCTOR}'/1"
        }
        return parseTerm(input, operators).toClause()
    }

    @JsName("parseClause")
    fun parseClause(input: String): Clause =
        parseClause(input, defaultOperatorSet)

    companion object {
        @JvmStatic
        @JsName("withNoOperator")
        val withNoOperator = termParserWithOperators(OperatorSet.EMPTY)

        @JvmStatic
        @JsName("withStandardOperators")
        val withStandardOperators = withOperators(OperatorSet.STANDARD)

        @JvmStatic
        @JsName("withDefaultOperators")
        val withDefaultOperators = withOperators(OperatorSet.DEFAULT)

        @JvmStatic
        @JsName("withOperatorSet")
        fun withOperators(operators: OperatorSet) = termParserWithOperators(operators)

        @JvmStatic
        @JsName("withOperators")
        fun withOperators(vararg operators: Operator) = withOperators(OperatorSet(*operators))
    }
}
