package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet

interface TermParser {
    
    val defaultOperatorSet: OperatorSet

    fun Term.Companion.parse(input: String, operators: OperatorSet = defaultOperatorSet): Term
    
    fun String.parseAsTerm(operators: OperatorSet = defaultOperatorSet): Term = 
        Term.parse(this, operators)

    fun Struct.Companion.parse(input: String, operators: OperatorSet = defaultOperatorSet): Struct =
        input.parseAsTerm(operators) as Struct
    
    fun String.parseAsStruct(operators: OperatorSet = defaultOperatorSet): Struct =
        Struct.parse(this, operators)
    
    fun Constant.Companion.parse(input: String, operators: OperatorSet = defaultOperatorSet): Constant =
        input.parseAsTerm(operators) as Constant
    
    fun String.parseAsConstant(operators: OperatorSet = defaultOperatorSet): Constant =
        Constant.parse(this, operators)

    fun Var.Companion.parse(input: String, operators: OperatorSet = defaultOperatorSet): Var =
        input.parseAsTerm(operators) as Var

    fun String.parseAsVar(operators: OperatorSet = defaultOperatorSet): Var =
        Var.parse(this, operators)

    fun Atom.Companion.parse(input: String, operators: OperatorSet = defaultOperatorSet): Atom =
        input.parseAsTerm(operators) as Atom

    fun String.parseAsAtom(operators: OperatorSet = defaultOperatorSet): Atom =
        Atom.parse(this, operators)

    fun Numeric.Companion.parse(input: String, operators: OperatorSet = defaultOperatorSet): Numeric =
        input.parseAsTerm(operators) as Numeric

    fun String.parseAsNumeric(operators: OperatorSet = defaultOperatorSet): Numeric =
        Numeric.parse(this, operators)

    fun Integer.Companion.parse(input: String, operators: OperatorSet = defaultOperatorSet): Integer =
        input.parseAsTerm(operators) as Integer

    fun String.parseAsInteger(operators: OperatorSet = defaultOperatorSet): Integer =
        Integer.parse(this, operators)

    fun Real.Companion.parse(input: String, operators: OperatorSet = defaultOperatorSet): Real =
        input.parseAsTerm(operators) as Real

    fun String.parseAsReal(operators: OperatorSet = defaultOperatorSet): Real =
        Real.parse(this, operators)

    companion object

}
expect fun TermParser.Companion.withNoOperator() : TermParser
expect fun TermParser.Companion.withStandardOperators(): TermParser
expect fun TermParser.Companion.withOperators(operators: OperatorSet): TermParser
expect fun TermParser.Companion.withOperators(vararg operators: Operator): TermParser