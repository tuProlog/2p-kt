package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet

interface TermParser {
    
    val defaultOperatorSet: OperatorSet

    fun parseTerm(input: String, operators: OperatorSet = defaultOperatorSet): Term

    fun Term.Companion.parse(input: String, operators: OperatorSet = defaultOperatorSet): Term =
        parseTerm(input, operators)
    
    fun String.parseAsTerm(operators: OperatorSet = defaultOperatorSet): Term =
        parseTerm(this, operators)


    fun parseStruct(input: String, operators: OperatorSet = defaultOperatorSet): Struct =
        input.parseAsTerm(operators) as Struct

    fun Struct.Companion.parse(input: String, operators: OperatorSet = defaultOperatorSet): Struct =
        parseStruct(input, operators)
    
    fun String.parseAsStruct(operators: OperatorSet = defaultOperatorSet): Struct =
        parseStruct(this, operators)


    fun parseConstant(input: String, operators: OperatorSet = defaultOperatorSet): Constant =
        input.parseAsTerm(operators) as Constant

    fun Constant.Companion.parse(input: String, operators: OperatorSet = defaultOperatorSet): Constant =
        parseConstant(input, operators)
    
    fun String.parseAsConstant(operators: OperatorSet = defaultOperatorSet): Constant =
        parseConstant(this, operators)


    fun parseVar(input: String, operators: OperatorSet = defaultOperatorSet): Var =
        input.parseAsTerm(operators) as Var

    fun Var.Companion.parse(input: String, operators: OperatorSet = defaultOperatorSet): Var =
        parseVar(input, operators)

    fun String.parseAsVar(operators: OperatorSet = defaultOperatorSet): Var =
        parseVar(this, operators)


    fun parseAtom(input: String, operators: OperatorSet = defaultOperatorSet): Atom =
        input.parseAsTerm(operators) as Atom

    fun Atom.Companion.parse(input: String, operators: OperatorSet = defaultOperatorSet): Atom =
        parseAtom(input, operators)

    fun String.parseAsAtom(operators: OperatorSet = defaultOperatorSet): Atom =
        parseAtom(this, operators)


    fun parseNumeric(input: String, operators: OperatorSet = defaultOperatorSet): Numeric =
        input.parseAsTerm(operators) as Numeric

    fun Numeric.Companion.parse(input: String, operators: OperatorSet = defaultOperatorSet): Numeric =
        parseNumeric(input, operators)

    fun String.parseAsNumeric(operators: OperatorSet = defaultOperatorSet): Numeric =
        parseNumeric(this, operators)


    fun parseInteger(input: String, operators: OperatorSet = defaultOperatorSet): Integer =
        input.parseAsTerm(operators) as Integer

    fun Integer.Companion.parse(input: String, operators: OperatorSet = defaultOperatorSet): Integer =
        parseInteger(input, operators)

    fun String.parseAsInteger(operators: OperatorSet = defaultOperatorSet): Integer =
        parseInteger(this, operators)


    fun parseReal(input: String, operators: OperatorSet = defaultOperatorSet): Real =
        input.parseAsTerm(operators) as Real

    fun Real.Companion.parse(input: String, operators: OperatorSet = defaultOperatorSet): Real =
        parseReal(input, operators)

    fun String.parseAsReal(operators: OperatorSet = defaultOperatorSet): Real =
        parseReal(this, operators)


    companion object {
        val withNoOperator = termParserWithNoOperator()

        val withStandardOperators = termParserWithStandardOperators()

        fun withOperators(operators: OperatorSet) = termParserWithOperators(operators)

        fun withOperators(vararg operators: Operator) = termParserWithOperators(*operators)
    }

}

fun termParserWithNoOperator(): TermParser =
    termParserWithOperators(OperatorSet.EMPTY)

fun termParserWithStandardOperators(): TermParser =
    termParserWithOperators(OperatorSet.DEFAULT)

expect fun termParserWithOperators(operators: OperatorSet): TermParser

fun termParserWithOperators(vararg operators: Operator): TermParser =
    termParserWithOperators(OperatorSet(operators.asSequence()))