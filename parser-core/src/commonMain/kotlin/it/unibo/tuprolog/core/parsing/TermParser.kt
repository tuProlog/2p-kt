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
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import kotlin.js.JsName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

interface TermParser {

    @JsName("scope")
    val scope: Scope

    @JsName("defaultOperatorSet")
    val defaultOperatorSet: OperatorSet

    @JsName("parseTermWithOperators")
    fun parseTerm(input: String, operators: OperatorSet): Term

    @JsName("parseTermsWithOperators")
    fun parseTerms(input: String, operators: OperatorSet): Sequence<Term>

    @JsName("parseTerms")
    fun parseTerms(input: String): Sequence<Term> =
        parseTerms(input, defaultOperatorSet)

    @JsName("parseTerm")
    fun parseTerm(input: String): Term =
        parseTerm(input, defaultOperatorSet)

    @JsName("parseStructWithOperators")
    fun parseStruct(input: String, operators: OperatorSet): Struct =
        parseAs(input, operators)

    @JsName("parseStruct")
    fun parseStruct(input: String): Struct =
        parseStruct(input, defaultOperatorSet)

    @JsName("parseConstantWithOperators")
    fun parseConstant(input: String, operators: OperatorSet): Constant =
        parseAs(input, operators)

    @JsName("parseConstant")
    fun parseConstant(input: String): Constant =
        parseConstant(input, defaultOperatorSet)

    @JsName("parseVarWithOperators")
    fun parseVar(input: String, operators: OperatorSet): Var =
        parseAs(input, operators)

    @JsName("parseVar")
    fun parseVar(input: String): Var =
        parseVar(input, defaultOperatorSet)

    @JsName("parseAtomWithOperators")
    fun parseAtom(input: String, operators: OperatorSet): Atom =
        parseAs(input, operators)

    @JsName("parseAtom")
    fun parseAtom(input: String): Atom =
        parseAtom(input, defaultOperatorSet)

    @JsName("parseNumericWithOperators")
    fun parseNumeric(input: String, operators: OperatorSet): Numeric =
        parseAs(input, operators)

    @JsName("parseNumeric")
    fun parseNumeric(input: String): Numeric =
        parseNumeric(input, defaultOperatorSet)

    @JsName("parseIntegerWithOperators")
    fun parseInteger(input: String, operators: OperatorSet): Integer =
        parseAs(input, operators)

    @JsName("parseInteger")
    fun parseInteger(input: String): Integer =
        parseInteger(input, defaultOperatorSet)

    @JsName("parseRealWithOperators")
    fun parseReal(input: String, operators: OperatorSet): Real =
        parseAs(input, operators)

    @JsName("parseReal")
    fun parseReal(input: String): Real =
        parseReal(input, defaultOperatorSet)

    @JsName("parseClauseWithOperators")
    fun parseClause(input: String, operators: OperatorSet): Clause =
        parseAs(input, operators)

    @JsName("parseClause")
    fun parseClause(input: String): Clause =
        parseClause(input, defaultOperatorSet)

    @JsName("parseRuleWithOperators")
    fun parseRule(input: String, operators: OperatorSet): Rule =
        parseAs(input, operators)

    @JsName("parseRule")
    fun parseRule(input: String): Rule =
        parseRule(input, defaultOperatorSet)

    @JsName("parseFactWithOperators")
    fun parseFact(input: String, operators: OperatorSet): Fact =
        parseAs(input, operators)

    @JsName("parseFact")
    fun parseFact(input: String): Fact =
        parseFact(input, defaultOperatorSet)

    @JsName("parseDirectiveWithOperators")
    fun parseDirective(input: String, operators: OperatorSet): Directive =
        parseAs(input, operators)

    @JsName("parseDirective")
    fun parseDirective(input: String): Directive =
        parseDirective(input, defaultOperatorSet)

    companion object {
        @JvmStatic
        @JsName("withNoOperator")
        @JvmOverloads
        fun withNoOperator(scope: Scope = Scope.empty()) = withOperators(OperatorSet.EMPTY, scope)

        @JvmStatic
        @JsName("withStandardOperators")
        @JvmOverloads
        fun withStandardOperators(scope: Scope = Scope.empty()) = withOperators(OperatorSet.STANDARD, scope)

        @JvmStatic
        @JsName("withDefaultOperators")
        @JvmOverloads
        fun withDefaultOperators(scope: Scope = Scope.empty()) = withOperators(OperatorSet.DEFAULT, scope)

        @JvmStatic
        @JsName("withOperatorSet")
        @JvmOverloads
        fun withOperators(operators: OperatorSet, scope: Scope = Scope.empty()) =
            termParserWithOperators(operators, scope)

        @JvmStatic
        @JsName("withOperators")
        @JvmOverloads
        fun withOperators(vararg operators: Operator, scope: Scope = Scope.empty()) =
            withOperators(OperatorSet(*operators), scope)

        private inline fun <reified T : Term> TermParser.parseAs(input: String, operators: OperatorSet) =
            parseTerm(input, operators).let {
                it as? T ?: throw InvalidTermTypeException(input, it, T::class)
            }
    }
}
