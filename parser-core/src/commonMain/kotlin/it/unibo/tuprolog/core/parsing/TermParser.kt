package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

interface TermParser {
    
    val defaultOperatorSet: OperatorSet

    fun parseTerm(input: String, operators: OperatorSet): Term

    fun parseTerm(input: String): Term =
        parseTerm(input, defaultOperatorSet)


    fun parseStruct(input: String, operators: OperatorSet): Struct =
        parseTerm(input, operators) as Struct

    fun parseStruct(input: String): Struct =
        parseStruct(input, defaultOperatorSet)
    

    fun parseConstant(input: String, operators: OperatorSet): Constant =
        parseTerm(input, operators) as Constant

    fun parseConstant(input: String): Constant =
        parseConstant(input, defaultOperatorSet)


    fun parseVar(input: String, operators: OperatorSet): Var =
        parseTerm(input, operators) as Var

    fun parseVar(input: String): Var =
        parseVar(input, defaultOperatorSet)


    fun parseAtom(input: String, operators: OperatorSet): Atom =
        parseTerm(input, operators) as Atom

    fun parseAtom(input: String): Atom =
        parseAtom(input, defaultOperatorSet)


    fun parseNumeric(input: String, operators: OperatorSet): Numeric =
        parseTerm(input, operators) as Numeric

    fun parseNumeric(input: String): Numeric =
        parseNumeric(input, defaultOperatorSet)


    fun parseInteger(input: String, operators: OperatorSet): Integer =
        parseTerm(input, operators) as Integer

    fun parseInteger(input: String): Integer =
        parseInteger(input, defaultOperatorSet)


    fun parseReal(input: String, operators: OperatorSet): Real =
        parseTerm(input, operators) as Real

    fun parseReal(input: String): Real =
        parseReal(input, defaultOperatorSet)


    fun parseClause(input: String, operators: OperatorSet): Clause {
        require(operators.any { it.functor == Clause.FUNCTOR }) {
            "The provided operator set has no operator for '${Clause.FUNCTOR}'/1 or '${Clause.FUNCTOR}'/1"
        }
        return when (val term = parseTerm(input, operators)) {
            is Clause -> term
            else -> Fact.of(term as Struct)
        }
    }

    fun parseClause(input: String): Clause =
        parseClause(input, defaultOperatorSet)

    companion object {
        @JvmStatic
        val withNoOperator = termParserWithOperators(OperatorSet.EMPTY)

        @JvmStatic
        val withStandardOperators = withOperators(OperatorSet.STANDARD)

        @JvmStatic
        val withDefaultOperators = withOperators(OperatorSet.DEFAULT)

        @JvmStatic
        fun withOperators(operators: OperatorSet) = termParserWithOperators(operators)

        @JvmStatic
        fun withOperators(vararg operators: Operator) = withOperators(OperatorSet(*operators))
    }

}