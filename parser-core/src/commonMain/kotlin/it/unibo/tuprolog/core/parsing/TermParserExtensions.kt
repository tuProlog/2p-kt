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
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.operators.OperatorSet
import kotlin.js.JsName
import kotlin.jvm.JvmName

private val defaultParser: TermParser
    get() = TermParser.withDefaultOperators()

/**
 * Converts this term to a clause, promoting a [Struct] to a fact when necessary.
 *
 * @throws InvalidTermTypeException if this term is neither a [Clause] nor a [Struct]
 */
@JsName("termToClause")
fun Term.toClause(
    source: Any? = null,
    line: Int = 0,
    column: Int = 0,
): Clause =
    when (this) {
        is Clause -> this
        is Struct -> Fact.of(this)
        else -> throw InvalidTermTypeException(source, this, Clause::class, line = line, column = column)
    }

/**
 * Parses [input] as a term using [operators].
 *
 * @throws ParseException if parsing fails
 */
@JsName("parseTermWithOperators")
fun Term.Companion.parse(
    input: String,
    operators: OperatorSet,
): Term = defaultParser.parseTerm(input, operators)

/**
 * Parses [input] as a term using default operators.
 *
 * @throws ParseException if parsing fails
 */
@JsName("parseTerm")
fun Term.Companion.parse(input: String): Term = defaultParser.parseTerm(input)

/**
 * Parses this string as a term using [operators].
 *
 * @throws ParseException if parsing fails
 */
@JsName("parseStringAsTermWithOperators")
fun String.parseAsTerm(operators: OperatorSet): Term = defaultParser.parseTerm(this, operators)

/**
 * Parses this string as a term using default operators.
 *
 * @throws ParseException if parsing fails
 */
@JsName("parseStringAsTerm")
fun String.parseAsTerm(): Term = defaultParser.parseTerm(this)

/**
 * Parses [input] as a structure using [operators].
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseStructWithOperators")
fun Struct.Companion.parse(
    input: String,
    operators: OperatorSet,
): Struct = defaultParser.parseStruct(input, operators)

/**
 * Parses [input] as a structure.
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseStruct")
fun Struct.Companion.parse(input: String): Struct = defaultParser.parseStruct(input)

/**
 * Parses this string as a structure using [operators].
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseStringAsStructWithOperators")
fun String.parseAsStruct(operators: OperatorSet): Struct = defaultParser.parseStruct(this, operators)

/**
 * Parses this string as a structure.
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseStringAsStruct")
fun String.parseAsStruct(): Struct = defaultParser.parseStruct(this)

/**
 * Parses [input] as a constant using [operators].
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseConstantWithOperators")
fun Constant.Companion.parse(
    input: String,
    operators: OperatorSet,
): Constant = defaultParser.parseConstant(input, operators)

/**
 * Parses [input] as a constant.
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseConstant")
fun Constant.Companion.parse(input: String): Constant = defaultParser.parseConstant(input)

/**
 * Parses this string as a constant using [operators].
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseStringAsConstantWithOperators")
fun String.parseAsConstant(operators: OperatorSet): Constant = defaultParser.parseConstant(this, operators)

/**
 * Parses this string as a constant.
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseStringAsConstant")
fun String.parseAsConstant(): Constant = defaultParser.parseConstant(this)

/**
 * Parses [input] as a variable using [operators].
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseVarWithOperators")
fun Var.Companion.parse(
    input: String,
    operators: OperatorSet,
): Var = defaultParser.parseVar(input, operators)

/**
 * Parses [input] as a variable.
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseVar")
fun Var.Companion.parse(input: String): Var = defaultParser.parseVar(input)

/**
 * Parses this string as a variable using [operators].
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseStringAsVarWithOperators")
fun String.parseAsVar(operators: OperatorSet): Var = defaultParser.parseVar(this, operators)

/**
 * Parses this string as a variable.
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseStringAs")
fun String.parseAsVar(): Var = defaultParser.parseVar(this)

/**
 * Parses [input] as an atom using [operators].
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseAtomWithOperators")
fun Atom.Companion.parse(
    input: String,
    operators: OperatorSet,
): Atom = defaultParser.parseAtom(input, operators)

/**
 * Parses [input] as an atom.
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseAtom")
fun Atom.Companion.parse(input: String): Atom = defaultParser.parseAtom(input)

/**
 * Parses this string as an atom using [operators].
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseStringAsAtomWithOperators")
fun String.parseAsAtom(operators: OperatorSet): Atom = defaultParser.parseAtom(this, operators)

/**
 * Parses this string as an atom.
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseStringAsAtom")
fun String.parseAsAtom(): Atom = defaultParser.parseAtom(this)

/**
 * Parses [input] as a numeric term using [operators].
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseNumericWithOperators")
fun Numeric.Companion.parse(
    input: String,
    operators: OperatorSet,
): Numeric = defaultParser.parseNumeric(input, operators)

/**
 * Parses [input] as a numeric term.
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseNumeric")
fun Numeric.Companion.parse(input: String): Numeric = defaultParser.parseNumeric(input)

/**
 * Parses this string as a numeric term using [operators].
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseStringAsNumericWithOperators")
fun String.parseAsNumeric(operators: OperatorSet): Numeric = defaultParser.parseNumeric(this, operators)

/**
 * Parses this string as a numeric term.
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseStringAsNumeric")
fun String.parseAsNumeric(): Numeric = defaultParser.parseNumeric(this)

/**
 * Parses [input] as an integer using [operators].
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseIntegerWithOperators")
fun Integer.Companion.parse(
    input: String,
    operators: OperatorSet,
): Integer = defaultParser.parseInteger(input, operators)

/**
 * Parses [input] as an integer.
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseInteger")
fun Integer.Companion.parse(input: String): Integer = defaultParser.parseInteger(input)

/**
 * Parses this string as an integer using [operators].
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseStringAsIntegerWithOperators")
fun String.parseAsInteger(operators: OperatorSet): Integer = defaultParser.parseInteger(this, operators)

/**
 * Parses this string as an integer.
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseStringAsInteger")
fun String.parseAsInteger(): Integer = defaultParser.parseInteger(this)

/**
 * Parses [input] as a real using [operators].
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseRealWithOperators")
fun Real.Companion.parse(
    input: String,
    operators: OperatorSet,
): Real = defaultParser.parseReal(input, operators)

/**
 * Parses [input] as a real.
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseReal")
fun Real.Companion.parse(input: String): Real = defaultParser.parseReal(input)

/**
 * Parses this string as a real using [operators].
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseStringAsRealWithOperators")
fun String.parseAsReal(operators: OperatorSet): Real = defaultParser.parseReal(this, operators)

/**
 * Parses this string as a real.
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseStringAsReal")
fun String.parseAsReal(): Real = defaultParser.parseReal(this)

/**
 * Parses [input] as a clause using [operators].
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseClauseWithOperators")
fun Clause.Companion.parse(
    input: String,
    operators: OperatorSet,
): Clause = defaultParser.parseClause(input, operators)

/**
 * Parses [input] as a clause.
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseClause")
fun Clause.Companion.parse(input: String): Clause = defaultParser.parseClause(input)

/**
 * Parses this string as a clause using [operators].
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseStringAsClauseWithOperators")
fun String.parseAsClause(operators: OperatorSet): Clause = defaultParser.parseClause(this, operators)

/**
 * Parses this string as a clause.
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseStringAsClause")
fun String.parseAsClause(): Clause = defaultParser.parseClause(this)

/**
 * Parses [input] as a rule using [operators].
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseRuleWithOperators")
fun Rule.Companion.parse(
    input: String,
    operators: OperatorSet,
): Rule = defaultParser.parseRule(input, operators)

/**
 * Parses [input] as a rule.
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseRule")
fun Rule.Companion.parse(input: String): Rule = defaultParser.parseRule(input)

/**
 * Parses this string as a rule using [operators].
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseStringAsRuleWithOperators")
fun String.parseAsRule(operators: OperatorSet): Rule = defaultParser.parseRule(this, operators)

/**
 * Parses this string as a rule.
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseStringAsRule")
fun String.parseAsRule(): Rule = defaultParser.parseRule(this)

/**
 * Parses [input] as a fact using [operators].
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseFactWithOperators")
fun Fact.Companion.parse(
    input: String,
    operators: OperatorSet,
): Fact = defaultParser.parseFact(input, operators)

/**
 * Parses [input] as a fact.
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseFact")
fun Fact.Companion.parse(input: String): Fact = defaultParser.parseFact(input)

/**
 * Parses this string as a fact using [operators].
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseStringAsFactWithOperators")
fun String.parseAsFact(operators: OperatorSet): Fact = defaultParser.parseFact(this, operators)

/**
 * Parses this string as a fact.
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseStringAsFact")
fun String.parseAsFact(): Fact = defaultParser.parseFact(this)

/**
 * Parses [input] as a directive using [operators].
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseDirectiveWithOperators")
fun Directive.Companion.parse(
    input: String,
    operators: OperatorSet,
): Directive = defaultParser.parseDirective(input, operators)

/**
 * Parses [input] as a directive.
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseDirective")
fun Directive.Companion.parse(input: String): Directive = defaultParser.parseDirective(input)

/**
 * Parses this string as a directive using [operators].
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseStringAsDirectiveWithOperators")
fun String.parseAsDirective(operators: OperatorSet): Directive = defaultParser.parseDirective(this, operators)

/**
 * Parses this string as a directive.
 *
 * @throws ParseException if parsing or type checking fails
 */
@JsName("parseStringAsDirective")
fun String.parseAsDirective(): Directive = defaultParser.parseDirective(this)
