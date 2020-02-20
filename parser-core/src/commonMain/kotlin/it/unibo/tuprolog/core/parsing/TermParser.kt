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

    fun Term.Companion.parse(input: String, operators: OperatorSet): Term =
        parseTerm(input, operators)

    fun Term.Companion.parse(input: String): Term =
        parseTerm(input)
    
    fun String.parseAsTerm(operators: OperatorSet): Term =
        parseTerm(this, operators)

    fun String.parseAsTerm(): Term =
        parseTerm(this)


    fun parseStruct(input: String, operators: OperatorSet): Struct =
        input.parseAsTerm(operators) as Struct

    fun parseStruct(input: String): Struct =
        input.parseAsTerm() as Struct 

    fun Struct.Companion.parse(input: String, operators: OperatorSet): Struct =
        parseStruct(input, operators)

    fun Struct.Companion.parse(input: String): Struct =
        parseStruct(input)
    
    fun String.parseAsStruct(operators: OperatorSet): Struct =
        parseStruct(this, operators)

    fun String.parseAsStruct(): Struct =
        parseStruct(this)


    fun parseConstant(input: String, operators: OperatorSet): Constant =
        input.parseAsTerm(operators) as Constant

    fun parseConstant(input: String): Constant =
        input.parseAsTerm() as Constant

    fun Constant.Companion.parse(input: String, operators: OperatorSet): Constant =
        parseConstant(input, operators)

    fun Constant.Companion.parse(input: String): Constant =
        parseConstant(input)
    
    fun String.parseAsConstant(operators: OperatorSet): Constant =
        parseConstant(this, operators)

    fun String.parseAsConstant(): Constant =
        parseConstant(this)


    fun parseVar(input: String, operators: OperatorSet): Var =
        input.parseAsTerm(operators) as Var

    fun parseVar(input: String): Var =
        input.parseAsTerm() as Var

    fun Var.Companion.parse(input: String, operators: OperatorSet): Var =
        parseVar(input, operators)

    fun Var.Companion.parse(input: String): Var =
        parseVar(input)

    fun String.parseAsVar(operators: OperatorSet): Var =
        parseVar(this, operators)

    fun String.parseAsVar(): Var =
        parseVar(this)


    fun parseAtom(input: String, operators: OperatorSet): Atom =
        input.parseAsTerm(operators) as Atom

    fun parseAtom(input: String): Atom =
        input.parseAsTerm() as Atom

    fun Atom.Companion.parse(input: String, operators: OperatorSet): Atom =
        parseAtom(input, operators)

    fun Atom.Companion.parse(input: String): Atom =
        parseAtom(input)

    fun String.parseAsAtom(operators: OperatorSet): Atom =
        parseAtom(this, operators)

    fun String.parseAsAtom(): Atom =
        parseAtom(this)


    fun parseNumeric(input: String, operators: OperatorSet): Numeric =
        input.parseAsTerm(operators) as Numeric

    fun parseNumeric(input: String): Numeric =
        input.parseAsTerm() as Numeric

    fun Numeric.Companion.parse(input: String, operators: OperatorSet): Numeric =
        parseNumeric(input, operators)

    fun Numeric.Companion.parse(input: String): Numeric =
        parseNumeric(input)

    fun String.parseAsNumeric(operators: OperatorSet): Numeric =
        parseNumeric(this, operators)

    fun String.parseAsNumeric(): Numeric =
        parseNumeric(this)


    fun parseInteger(input: String, operators: OperatorSet): Integer =
        input.parseAsTerm(operators) as Integer

    fun parseInteger(input: String): Integer =
        input.parseAsTerm() as Integer

    fun Integer.Companion.parse(input: String, operators: OperatorSet): Integer =
        parseInteger(input, operators)

    fun Integer.Companion.parse(input: String): Integer =
        parseInteger(input)

    fun String.parseAsInteger(operators: OperatorSet): Integer =
        parseInteger(this, operators)

    fun String.parseAsInteger(): Integer =
        parseInteger(this)


    fun parseReal(input: String, operators: OperatorSet): Real =
        input.parseAsTerm(operators) as Real

    fun parseReal(input: String): Real =
        input.parseAsTerm() as Real

    fun Real.Companion.parse(input: String, operators: OperatorSet): Real =
        parseReal(input, operators)

    fun Real.Companion.parse(input: String): Real =
        parseReal(input)

    fun String.parseAsReal(operators: OperatorSet): Real =
        parseReal(this, operators)

    fun String.parseAsReal(): Real =
        parseReal(this)


    companion object {
        @JvmStatic
        val withNoOperator = termParserWithNoOperator()

        @JvmStatic
        val withStandardOperators = termParserWithStandardOperators()

        @JvmStatic
        fun withOperators(operators: OperatorSet) = termParserWithOperators(operators)

        @JvmStatic
        fun withOperators(vararg operators: Operator) = termParserWithOperators(*operators)
    }

}