@file:JvmName("TermParserExtensions")

package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Constant
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.operators.OperatorSet
import kotlin.js.JsName
import kotlin.jvm.JvmName

internal expect fun termParserWithOperators(operators: OperatorSet, scope: Scope = Scope.empty()): TermParser

private val defaultParser: TermParser
    get() = TermParser.withDefaultOperators

@JsName("termToClause")
fun Term.toClause(source: Any? = null, line: Int = 0, column: Int = 0): Clause =
    when (this) {
        is Clause -> this
        is Struct -> Fact.of(this)
        else -> throw InvalidTermTypeException(source, this, Clause::class, line = line, column = column)
    }

@JsName("parseTermWithOperators")
fun Term.Companion.parse(input: String, operators: OperatorSet): Term =
    defaultParser.parseTerm(input, operators)

@JsName("parseTerm")
fun Term.Companion.parse(input: String): Term =
    defaultParser.parseTerm(input)

@JsName("parseStringAsTermWithOperators")
fun String.parseAsTerm(operators: OperatorSet): Term =
    defaultParser.parseTerm(this, operators)

@JsName("parseStringAsTerm")
fun String.parseAsTerm(): Term =
    defaultParser.parseTerm(this)

@JsName("parseStructWithOperators")
fun Struct.Companion.parse(input: String, operators: OperatorSet): Struct =
    defaultParser.parseStruct(input, operators)

@JsName("parseStruct")
fun Struct.Companion.parse(input: String): Struct =
    defaultParser.parseStruct(input)

@JsName("parseStringAsStructWithOperators")
fun String.parseAsStruct(operators: OperatorSet): Struct =
    defaultParser.parseStruct(this, operators)

@JsName("parseStringAsStruct")
fun String.parseAsStruct(): Struct =
    defaultParser.parseStruct(this)

@JsName("parseConstantWithOperators")
fun Constant.Companion.parse(input: String, operators: OperatorSet): Constant =
    defaultParser.parseConstant(input, operators)

@JsName("parseConstant")
fun Constant.Companion.parse(input: String): Constant =
    defaultParser.parseConstant(input)

@JsName("parseStringAsConstantWithOperators")
fun String.parseAsConstant(operators: OperatorSet): Constant =
    defaultParser.parseConstant(this, operators)

@JsName("parseStringAsConstant")
fun String.parseAsConstant(): Constant =
    defaultParser.parseConstant(this)

@JsName("parseVarWithOperators")
fun Var.Companion.parse(input: String, operators: OperatorSet): Var =
    defaultParser.parseVar(input, operators)

@JsName("parseVar")
fun Var.Companion.parse(input: String): Var =
    defaultParser.parseVar(input)

@JsName("parseStringAsVarWithOperators")
fun String.parseAsVar(operators: OperatorSet): Var =
    defaultParser.parseVar(this, operators)

@JsName("parseStringAs")
fun String.parseAsVar(): Var =
    defaultParser.parseVar(this)

@JsName("parseAtomWithOperators")
fun Atom.Companion.parse(input: String, operators: OperatorSet): Atom =
    defaultParser.parseAtom(input, operators)

@JsName("parseAtom")
fun Atom.Companion.parse(input: String): Atom =
    defaultParser.parseAtom(input)

@JsName("parseStringAsAtomWithOperators")
fun String.parseAsAtom(operators: OperatorSet): Atom =
    defaultParser.parseAtom(this, operators)

@JsName("parseStringAsAtom")
fun String.parseAsAtom(): Atom =
    defaultParser.parseAtom(this)

@JsName("parseNumericWithOperators")
fun Numeric.Companion.parse(input: String, operators: OperatorSet): Numeric =
    defaultParser.parseNumeric(input, operators)

@JsName("parseNumeric")
fun Numeric.Companion.parse(input: String): Numeric =
    defaultParser.parseNumeric(input)

@JsName("parseStringAsNumericWithOperators")
fun String.parseAsNumeric(operators: OperatorSet): Numeric =
    defaultParser.parseNumeric(this, operators)

@JsName("parseStringAsNumeric")
fun String.parseAsNumeric(): Numeric =
    defaultParser.parseNumeric(this)

@JsName("parseIntegerWithOperators")
fun Integer.Companion.parse(input: String, operators: OperatorSet): Integer =
    defaultParser.parseInteger(input, operators)

@JsName("parseInteger")
fun Integer.Companion.parse(input: String): Integer =
    defaultParser.parseInteger(input)

@JsName("parseStringAsIntegerWithOperators")
fun String.parseAsInteger(operators: OperatorSet): Integer =
    defaultParser.parseInteger(this, operators)

@JsName("parseStringAsInteger")
fun String.parseAsInteger(): Integer =
    defaultParser.parseInteger(this)

@JsName("parseRealWithOperators")
fun Real.Companion.parse(input: String, operators: OperatorSet): Real =
    defaultParser.parseReal(input, operators)

@JsName("parseReal")
fun Real.Companion.parse(input: String): Real =
    defaultParser.parseReal(input)

@JsName("parseStringAsRealWithOperators")
fun String.parseAsReal(operators: OperatorSet): Real =
    defaultParser.parseReal(this, operators)

@JsName("parseStringAsReal")
fun String.parseAsReal(): Real =
    defaultParser.parseReal(this)

@JsName("parseClauseWithOperators")
fun Clause.Companion.parse(input: String, operators: OperatorSet): Clause =
    defaultParser.parseClause(input, operators)

@JsName("parseClause")
fun Clause.Companion.parse(input: String): Clause =
    defaultParser.parseClause(input)

@JsName("parseStringAsClauseWithOperators")
fun String.parseAsClause(operators: OperatorSet): Clause =
    defaultParser.parseClause(this, operators)

@JsName("parseStringAsClause")
fun String.parseAsClause(): Clause =
    defaultParser.parseClause(this)

@JsName("parseRuleWithOperators")
fun Rule.Companion.parse(input: String, operators: OperatorSet): Rule =
    defaultParser.parseRule(input, operators)

@JsName("parseRule")
fun Rule.Companion.parse(input: String): Rule =
    defaultParser.parseRule(input)

@JsName("parseStringAsRuleWithOperators")
fun String.parseAsRule(operators: OperatorSet): Rule =
    defaultParser.parseRule(this, operators)

@JsName("parseStringAsRule")
fun String.parseAsRule(): Rule =
    defaultParser.parseRule(this)

@JsName("parseFactWithOperators")
fun Fact.Companion.parse(input: String, operators: OperatorSet): Fact =
    defaultParser.parseFact(input, operators)

@JsName("parseFact")
fun Fact.Companion.parse(input: String): Fact =
    defaultParser.parseFact(input)

@JsName("parseStringAsFactWithOperators")
fun String.parseAsFact(operators: OperatorSet): Fact =
    defaultParser.parseFact(this, operators)

@JsName("parseStringAsFact")
fun String.parseAsFact(): Fact =
    defaultParser.parseFact(this)

@JsName("parseDirectiveWithOperators")
fun Directive.Companion.parse(input: String, operators: OperatorSet): Directive =
    defaultParser.parseDirective(input, operators)

@JsName("parseDirective")
fun Directive.Companion.parse(input: String): Directive =
    defaultParser.parseDirective(input)

@JsName("parseStringAsDirectiveWithOperators")
fun String.parseAsDirective(operators: OperatorSet): Directive =
    defaultParser.parseDirective(this, operators)

@JsName("parseStringAsDirective")
fun String.parseAsDirective(): Directive =
    defaultParser.parseDirective(this)
