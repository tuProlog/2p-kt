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

/**
 * Parses one Prolog expression from a [String] and maps it to the tuProlog term model.
 *
 * Parsing is performed with a lossless concrete-syntax parser and then converted using [scope].
 * Low-level lexical and syntactic exceptions are wrapped in [ParseException]. The typed entry
 * points additionally verify the resulting runtime type and throw [InvalidTermTypeException] on a
 * mismatch.
 *
 * ```kotlin
 * val parser = TermParser.withStandardOperators()
 * val clause = parser.parseClause("ancestor(X, Y) :- parent(X, Y)")
 * val custom = parser.parseTerm("a ++ b", OperatorSet(Operator("++", Specifier.YFX, 500)))
 * ```
 *
 * @property scope scope used to allocate terms and preserve variable identity within one parse
 * @property defaultOperatorSet operators used by overloads without an explicit [OperatorSet]
 */
interface TermParser {
    /** Scope used to construct parsed terms. */
    @JsName("scope")
    val scope: Scope

    /** Operator set used when an overload does not receive one explicitly. */
    @JsName("defaultOperatorSet")
    val defaultOperatorSet: OperatorSet

    /**
     * Parses [input] as any [Term] using [operators].
     *
     * @throws ParseException if the input is lexically or syntactically invalid
     */
    @JsName("parseTermWithOperators")
    fun parseTerm(
        input: String,
        operators: OperatorSet,
    ): Term

    /**
     * @throws ParseException if [input] is lexically or syntactically invalid
     */
    @JsName("parseTerm")
    fun parseTerm(input: String): Term = parseTerm(input, defaultOperatorSet)

    /**
     * Parses [input] as a [Struct] using [operators].
     *
     * @throws ParseException if parsing fails
     * @throws InvalidTermTypeException if the parsed term is not a [Struct]
     */
    @JsName("parseStructWithOperators")
    fun parseStruct(
        input: String,
        operators: OperatorSet,
    ): Struct = parseAs(input, operators)

    /**
     * @throws ParseException if parsing fails
     * @throws InvalidTermTypeException if [input] does not denote a [Struct]
     */
    @JsName("parseStruct")
    fun parseStruct(input: String): Struct = parseStruct(input, defaultOperatorSet)

    /**
     * Parses [input] as a [Constant] using [operators].
     *
     * @throws ParseException if parsing fails
     * @throws InvalidTermTypeException if the parsed term is not a [Constant]
     */
    @JsName("parseConstantWithOperators")
    fun parseConstant(
        input: String,
        operators: OperatorSet,
    ): Constant = parseAs(input, operators)

    /**
     * @throws ParseException if parsing fails
     * @throws InvalidTermTypeException if [input] does not denote a [Constant]
     */
    @JsName("parseConstant")
    fun parseConstant(input: String): Constant = parseConstant(input, defaultOperatorSet)

    /**
     * Parses [input] as a [Var] using [operators].
     *
     * @throws ParseException if parsing fails
     * @throws InvalidTermTypeException if the parsed term is not a [Var]
     */
    @JsName("parseVarWithOperators")
    fun parseVar(
        input: String,
        operators: OperatorSet,
    ): Var = parseAs(input, operators)

    /**
     * @throws ParseException if parsing fails
     * @throws InvalidTermTypeException if [input] does not denote a [Var]
     */
    @JsName("parseVar")
    fun parseVar(input: String): Var = parseVar(input, defaultOperatorSet)

    /**
     * Parses [input] as an [Atom] using [operators].
     *
     * @throws ParseException if parsing fails
     * @throws InvalidTermTypeException if the parsed term is not an [Atom]
     */
    @JsName("parseAtomWithOperators")
    fun parseAtom(
        input: String,
        operators: OperatorSet,
    ): Atom = parseAs(input, operators)

    /**
     * @throws ParseException if parsing fails
     * @throws InvalidTermTypeException if [input] does not denote an [Atom]
     */
    @JsName("parseAtom")
    fun parseAtom(input: String): Atom = parseAtom(input, defaultOperatorSet)

    /**
     * Parses [input] as a [Numeric] term using [operators].
     *
     * @throws ParseException if parsing fails
     * @throws InvalidTermTypeException if the parsed term is not numeric
     */
    @JsName("parseNumericWithOperators")
    fun parseNumeric(
        input: String,
        operators: OperatorSet,
    ): Numeric = parseAs(input, operators)

    /**
     * @throws ParseException if parsing fails
     * @throws InvalidTermTypeException if [input] does not denote a [Numeric]
     */
    @JsName("parseNumeric")
    fun parseNumeric(input: String): Numeric = parseNumeric(input, defaultOperatorSet)

    /**
     * Parses [input] as an [Integer] using [operators].
     *
     * @throws ParseException if parsing fails
     * @throws InvalidTermTypeException if the parsed term is not an [Integer]
     */
    @JsName("parseIntegerWithOperators")
    fun parseInteger(
        input: String,
        operators: OperatorSet,
    ): Integer = parseAs(input, operators)

    /**
     * @throws ParseException if parsing fails
     * @throws InvalidTermTypeException if [input] does not denote an [Integer]
     */
    @JsName("parseInteger")
    fun parseInteger(input: String): Integer = parseInteger(input, defaultOperatorSet)

    /**
     * Parses [input] as a [Real] using [operators].
     *
     * @throws ParseException if parsing fails
     * @throws InvalidTermTypeException if the parsed term is not a [Real]
     */
    @JsName("parseRealWithOperators")
    fun parseReal(
        input: String,
        operators: OperatorSet,
    ): Real = parseAs(input, operators)

    /**
     * @throws ParseException if parsing fails
     * @throws InvalidTermTypeException if [input] does not denote a [Real]
     */
    @JsName("parseReal")
    fun parseReal(input: String): Real = parseReal(input, defaultOperatorSet)

    /**
     * Parses [input] as a [Clause] using [operators]. A final full stop is optional.
     *
     * Structures are promoted to facts; rules and directives retain their specialized types.
     *
     * @throws ParseException if parsing fails
     * @throws InvalidTermTypeException if the parsed term cannot represent a clause
     */
    @JsName("parseClauseWithOperators")
    fun parseClause(
        input: String,
        operators: OperatorSet,
    ): Clause = parseAs(input, operators)

    /**
     * @throws ParseException if parsing fails
     * @throws InvalidTermTypeException if [input] cannot represent a [Clause]
     */
    @JsName("parseClause")
    fun parseClause(input: String): Clause = parseClause(input, defaultOperatorSet)

    /**
     * Parses [input] as a [Rule] using [operators].
     *
     * @throws ParseException if parsing fails
     * @throws InvalidTermTypeException if the parsed term is not a [Rule]
     */
    @JsName("parseRuleWithOperators")
    fun parseRule(
        input: String,
        operators: OperatorSet,
    ): Rule = parseAs(input, operators)

    /**
     * @throws ParseException if parsing fails
     * @throws InvalidTermTypeException if [input] does not denote a [Rule]
     */
    @JsName("parseRule")
    fun parseRule(input: String): Rule = parseRule(input, defaultOperatorSet)

    /**
     * Parses [input] as a [Fact] using [operators].
     *
     * @throws ParseException if parsing fails
     * @throws InvalidTermTypeException if the parsed term is not a [Fact]
     */
    @JsName("parseFactWithOperators")
    fun parseFact(
        input: String,
        operators: OperatorSet,
    ): Fact = parseAs(input, operators)

    /**
     * @throws ParseException if parsing fails
     * @throws InvalidTermTypeException if [input] does not denote a [Fact]
     */
    @JsName("parseFact")
    fun parseFact(input: String): Fact = parseFact(input, defaultOperatorSet)

    /**
     * Parses [input] as a [Directive] using [operators].
     *
     * @throws ParseException if parsing fails
     * @throws InvalidTermTypeException if the parsed term is not a [Directive]
     */
    @JsName("parseDirectiveWithOperators")
    fun parseDirective(
        input: String,
        operators: OperatorSet,
    ): Directive = parseAs(input, operators)

    /**
     * @throws ParseException if parsing fails
     * @throws InvalidTermTypeException if [input] does not denote a [Directive]
     */
    @JsName("parseDirective")
    fun parseDirective(input: String): Directive = parseDirective(input, defaultOperatorSet)

    companion object {
        /** Creates a parser whose default operator set is empty. */
        @JvmStatic
        @JsName("withNoOperator")
        @JvmOverloads
        fun withNoOperator(scope: Scope = Scope.empty()) = withOperators(OperatorSet.EMPTY, scope)

        /** Creates a parser using the ISO-style standard operator set. */
        @JvmStatic
        @JsName("withStandardOperators")
        @JvmOverloads
        fun withStandardOperators(scope: Scope = Scope.empty()) = withOperators(OperatorSet.STANDARD, scope)

        /** Creates a parser using the library's complete default operator set. */
        @JvmStatic
        @JsName("withDefaultOperators")
        @JvmOverloads
        fun withDefaultOperators(scope: Scope = Scope.empty()) = withOperators(OperatorSet.DEFAULT, scope)

        /** Creates a parser using [operators] as its default and allocating terms in [scope]. */
        @JvmStatic
        @JsName("withOperatorSet")
        @JvmOverloads
        fun withOperators(
            operators: OperatorSet,
            scope: Scope = Scope.empty(),
        ) = TermParserImpl(scope, operators)

        /** Creates a parser from individual default [operators] and the supplied [scope]. */
        @JvmStatic
        @JsName("withOperators")
        @JvmOverloads
        fun withOperators(
            vararg operators: Operator,
            scope: Scope = Scope.empty(),
        ) = withOperators(OperatorSet(*operators), scope)

        private inline fun <reified T : Term> TermParser.parseAs(
            input: String,
            operators: OperatorSet,
        ) = parseTerm(input, operators).let {
            it as? T ?: throw InvalidTermTypeException(input, it, T::class)
        }
    }
}
