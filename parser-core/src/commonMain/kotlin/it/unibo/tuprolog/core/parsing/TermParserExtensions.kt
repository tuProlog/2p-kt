@file:JvmName("TermParserExtensions")

package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.operators.OperatorSet
import kotlin.jvm.JvmName

internal expect fun termParserWithOperators(operators: OperatorSet): TermParser

private val defaultParser = TermParser.withDefaultOperators

fun Term.toClause(source: Any? = null, line: Int = 0, column: Int = 0): Clause =
    when(this) {
        is Clause -> this
        is Struct -> Fact.of(this)
        else -> throw InvalidTermTypeException(source, toString(), Clause::class, line, column)
    }

fun Term.Companion.parse(input: String, operators: OperatorSet): Term =
    defaultParser.parseTerm(input, operators)

fun Term.Companion.parse(input: String): Term =
    defaultParser.parseTerm(input)

fun String.parseAsTerm(operators: OperatorSet): Term =
    defaultParser.parseTerm(this, operators)

fun String.parseAsTerm(): Term =
    defaultParser.parseTerm(this)

fun Struct.Companion.parse(input: String, operators: OperatorSet): Struct =
    defaultParser.parseStruct(input, operators)

fun Struct.Companion.parse(input: String): Struct =
    defaultParser.parseStruct(input)

fun String.parseAsStruct(operators: OperatorSet): Struct =
    defaultParser.parseStruct(this, operators)

fun String.parseAsStruct(): Struct =
    defaultParser.parseStruct(this)

fun Constant.Companion.parse(input: String, operators: OperatorSet): Constant =
    defaultParser.parseConstant(input, operators)

fun Constant.Companion.parse(input: String): Constant =
    defaultParser.parseConstant(input)

fun String.parseAsConstant(operators: OperatorSet): Constant =
    defaultParser.parseConstant(this, operators)

fun String.parseAsConstant(): Constant =
    defaultParser.parseConstant(this)

fun Var.Companion.parse(input: String, operators: OperatorSet): Var =
    defaultParser.parseVar(input, operators)

fun Var.Companion.parse(input: String): Var =
    defaultParser.parseVar(input)

fun String.parseAsVar(operators: OperatorSet): Var =
    defaultParser.parseVar(this, operators)

fun String.parseAsVar(): Var =
    defaultParser.parseVar(this)

fun Atom.Companion.parse(input: String, operators: OperatorSet): Atom =
    defaultParser.parseAtom(input, operators)

fun Atom.Companion.parse(input: String): Atom =
    defaultParser.parseAtom(input)

fun String.parseAsAtom(operators: OperatorSet): Atom =
    defaultParser.parseAtom(this, operators)

fun String.parseAsAtom(): Atom =
    defaultParser.parseAtom(this)


fun Numeric.Companion.parse(input: String, operators: OperatorSet): Numeric =
    defaultParser.parseNumeric(input, operators)

fun Numeric.Companion.parse(input: String): Numeric =
    defaultParser.parseNumeric(input)

fun String.parseAsNumeric(operators: OperatorSet): Numeric =
    defaultParser.parseNumeric(this, operators)

fun String.parseAsNumeric(): Numeric =
    defaultParser.parseNumeric(this)


fun Integer.Companion.parse(input: String, operators: OperatorSet): Integer =
    defaultParser.parseInteger(input, operators)

fun Integer.Companion.parse(input: String): Integer =
    defaultParser.parseInteger(input)

fun String.parseAsInteger(operators: OperatorSet): Integer =
    defaultParser.parseInteger(this, operators)

fun String.parseAsInteger(): Integer =
    defaultParser.parseInteger(this)

fun Real.Companion.parse(input: String, operators: OperatorSet): Real =
    defaultParser.parseReal(input, operators)

fun Real.Companion.parse(input: String): Real =
    defaultParser.parseReal(input)

fun String.parseAsReal(operators: OperatorSet): Real =
    defaultParser.parseReal(this, operators)

fun String.parseAsReal(): Real =
    defaultParser.parseReal(this)

fun Clause.Companion.parse(input: String, operators: OperatorSet): Clause =
    defaultParser.parseClause(input, operators)

fun Clause.Companion.parse(input: String): Clause =
    defaultParser.parseClause(input)

fun String.parseAsClause(operators: OperatorSet): Clause =
    defaultParser.parseClause(this, operators)

fun String.parseAsClause(): Clause =
    defaultParser.parseClause(this)