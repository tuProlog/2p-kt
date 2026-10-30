package it.unibo.tuprolog.core.parsing.test

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
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
import it.unibo.tuprolog.core.operators.Specifier
import it.unibo.tuprolog.core.parsing.InvalidTermTypeException
import it.unibo.tuprolog.core.parsing.TermParser
import it.unibo.tuprolog.core.parsing.parse
import it.unibo.tuprolog.core.parsing.parseAsAtom
import it.unibo.tuprolog.core.parsing.parseAsClause
import it.unibo.tuprolog.core.parsing.parseAsConstant
import it.unibo.tuprolog.core.parsing.parseAsDirective
import it.unibo.tuprolog.core.parsing.parseAsFact
import it.unibo.tuprolog.core.parsing.parseAsInteger
import it.unibo.tuprolog.core.parsing.parseAsNumeric
import it.unibo.tuprolog.core.parsing.parseAsReal
import it.unibo.tuprolog.core.parsing.parseAsRule
import it.unibo.tuprolog.core.parsing.parseAsStruct
import it.unibo.tuprolog.core.parsing.parseAsTerm
import it.unibo.tuprolog.core.parsing.parseAsVar
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import it.unibo.tuprolog.core.List as LogicList

/** `++` is not a default operator, so inputs using it only parse when an operator set is passed explicitly. */
private val customOperators = OperatorSet.DEFAULT + Operator("++", Specifier.XFX, 700)

/** One of the three ways to reach a typed parse: a [TermParser] method, `X.parse`, or `"...".parseAsX`. */
private enum class EntryPoint { PARSER, COMPANION, STRING }

/**
 * One kind of term with its typed entry points. `parse(entry, input, null)` uses the default operators,
 * `parse(entry, input, operators)` the overload taking an operator set.
 */
private class KindCase(
    val id: String,
    val type: KClass<out Term>,
    val input: String,
    val inputWithCustomOperator: String,
    val expected: Scope.() -> Term,
    val expectedWithCustomOperator: Scope.() -> Term,
    /** Input that parses, but to a term of another type; `null` for `Term`, which accepts anything. */
    val mismatch: String?,
    val parse: (EntryPoint, String, OperatorSet?) -> Term,
)

private val parser = TermParser.withDefaultOperators()

private fun <T : Term> entryPoints(
    viaParser: TermParser.(String, OperatorSet?) -> T,
    viaCompanion: (String, OperatorSet?) -> T,
    viaString: String.(OperatorSet?) -> T,
): (EntryPoint, String, OperatorSet?) -> Term =
    { entry, input, operators ->
        when (entry) {
            EntryPoint.PARSER -> parser.viaParser(input, operators)
            EntryPoint.COMPANION -> viaCompanion(input, operators)
            EntryPoint.STRING -> input.viaString(operators)
        }
    }

private val kinds =
    listOf(
        KindCase(
            "term",
            Term::class,
            "f(X, a)",
            "X ++ a",
            { structOf("f", varOf("X"), atomOf("a")) },
            { structOf("++", varOf("X"), atomOf("a")) },
            null,
            entryPoints(
                { s, ops -> if (ops == null) parseTerm(s) else parseTerm(s, ops) },
                { s, ops -> if (ops == null) Term.parse(s) else Term.parse(s, ops) },
                { ops -> if (ops == null) parseAsTerm() else parseAsTerm(ops) },
            ),
        ),
        KindCase(
            "struct",
            Struct::class,
            "f(a)",
            "a ++ b",
            { structOf("f", atomOf("a")) },
            { structOf("++", atomOf("a"), atomOf("b")) },
            "X",
            entryPoints(
                { s, ops -> if (ops == null) parseStruct(s) else parseStruct(s, ops) },
                { s, ops -> if (ops == null) Struct.parse(s) else Struct.parse(s, ops) },
                { ops -> if (ops == null) parseAsStruct() else parseAsStruct(ops) },
            ),
        ),
        KindCase(
            "constant",
            Constant::class,
            "a",
            "1",
            { atomOf("a") },
            { intOf(1) },
            "f(a)",
            entryPoints(
                { s, ops -> if (ops == null) parseConstant(s) else parseConstant(s, ops) },
                { s, ops -> if (ops == null) Constant.parse(s) else Constant.parse(s, ops) },
                { ops -> if (ops == null) parseAsConstant() else parseAsConstant(ops) },
            ),
        ),
        KindCase(
            "var",
            Var::class,
            "X",
            "_Y",
            { varOf("X") },
            { varOf("_Y") },
            "a",
            entryPoints(
                { s, ops -> if (ops == null) parseVar(s) else parseVar(s, ops) },
                { s, ops -> if (ops == null) Var.parse(s) else Var.parse(s, ops) },
                { ops -> if (ops == null) parseAsVar() else parseAsVar(ops) },
            ),
        ),
        KindCase(
            "atom",
            Atom::class,
            "'hello world'",
            "(++)",
            { atomOf("hello world") },
            { atomOf("++") },
            "1",
            entryPoints(
                { s, ops -> if (ops == null) parseAtom(s) else parseAtom(s, ops) },
                { s, ops -> if (ops == null) Atom.parse(s) else Atom.parse(s, ops) },
                { ops -> if (ops == null) parseAsAtom() else parseAsAtom(ops) },
            ),
        ),
        KindCase(
            "numeric",
            Numeric::class,
            "42",
            "2.5",
            { intOf(42) },
            { realOf(2.5) },
            "a",
            entryPoints(
                { s, ops -> if (ops == null) parseNumeric(s) else parseNumeric(s, ops) },
                { s, ops -> if (ops == null) Numeric.parse(s) else Numeric.parse(s, ops) },
                { ops -> if (ops == null) parseAsNumeric() else parseAsNumeric(ops) },
            ),
        ),
        KindCase(
            "integer",
            Integer::class,
            "42",
            "0'a",
            { intOf(42) },
            { intOf('a'.code) },
            "4.2",
            entryPoints(
                { s, ops -> if (ops == null) parseInteger(s) else parseInteger(s, ops) },
                { s, ops -> if (ops == null) Integer.parse(s) else Integer.parse(s, ops) },
                { ops -> if (ops == null) parseAsInteger() else parseAsInteger(ops) },
            ),
        ),
        KindCase(
            "real",
            Real::class,
            "2.5",
            "1.0e3",
            { realOf(2.5) },
            { realOf(1000.0) },
            "42",
            entryPoints(
                { s, ops -> if (ops == null) parseReal(s) else parseReal(s, ops) },
                { s, ops -> if (ops == null) Real.parse(s) else Real.parse(s, ops) },
                { ops -> if (ops == null) parseAsReal() else parseAsReal(ops) },
            ),
        ),
        KindCase(
            "clause",
            Clause::class,
            "p(X) :- q(X)",
            "p(X) :- X ++ a",
            { ruleOf(structOf("p", varOf("X")), structOf("q", varOf("X"))) },
            { ruleOf(structOf("p", varOf("X")), structOf("++", varOf("X"), atomOf("a"))) },
            "X",
            entryPoints(
                { s, ops -> if (ops == null) parseClause(s) else parseClause(s, ops) },
                { s, ops -> if (ops == null) Clause.parse(s) else Clause.parse(s, ops) },
                { ops -> if (ops == null) parseAsClause() else parseAsClause(ops) },
            ),
        ),
        KindCase(
            "rule",
            Rule::class,
            "p :- q, r",
            "p :- a ++ b",
            { ruleOf(atomOf("p"), atomOf("q"), atomOf("r")) },
            { ruleOf(atomOf("p"), structOf("++", atomOf("a"), atomOf("b"))) },
            "p(a)",
            entryPoints(
                { s, ops -> if (ops == null) parseRule(s) else parseRule(s, ops) },
                { s, ops -> if (ops == null) Rule.parse(s) else Rule.parse(s, ops) },
                { ops -> if (ops == null) parseAsRule() else parseAsRule(ops) },
            ),
        ),
        KindCase(
            "fact",
            Fact::class,
            "p(a) :- true",
            "a ++ b :- true",
            { factOf(structOf("p", atomOf("a"))) },
            { factOf(structOf("++", atomOf("a"), atomOf("b"))) },
            ":- p",
            entryPoints(
                { s, ops -> if (ops == null) parseFact(s) else parseFact(s, ops) },
                { s, ops -> if (ops == null) Fact.parse(s) else Fact.parse(s, ops) },
                { ops -> if (ops == null) parseAsFact() else parseAsFact(ops) },
            ),
        ),
        KindCase(
            "directive",
            Directive::class,
            ":- dynamic(p/1)",
            ":- a ++ b",
            { directiveOf(structOf("dynamic", structOf("/", atomOf("p"), intOf(1)))) },
            { directiveOf(structOf("++", atomOf("a"), atomOf("b"))) },
            "p :- q",
            entryPoints(
                { s, ops -> if (ops == null) parseDirective(s) else parseDirective(s, ops) },
                { s, ops -> if (ops == null) Directive.parse(s) else Directive.parse(s, ops) },
                { ops -> if (ops == null) parseAsDirective() else parseAsDirective(ops) },
            ),
        ),
    )

private class EntryCase(
    val kind: KindCase,
    val entry: EntryPoint,
) {
    val id = "${kind.id}/${entry.name.lowercase()}"
}

private val entryCases = kinds.flatMap { kind -> EntryPoint.entries.map { EntryCase(kind, it) } }

private fun assertParsedAs(
    type: KClass<out Term>,
    expected: Term,
    actual: Term,
) {
    assertTrue(type.isInstance(actual), "$actual is not a ${type.simpleName}")
    assertTermsAreEqual(expected, actual)
}

class TypedParsingSpec :
    FunSpec({
        withTests(nameFn = { "${it.id}/default-operators" }, entryCases) { case ->
            val kind = case.kind
            assertParsedAs(kind.type, Scope.empty().(kind.expected)(), kind.parse(case.entry, kind.input, null))
        }

        withTests(nameFn = { "${it.id}/given-operators" }, entryCases) { case ->
            val kind = case.kind
            val actual = kind.parse(case.entry, kind.inputWithCustomOperator, customOperators)
            assertParsedAs(kind.type, Scope.empty().(kind.expectedWithCustomOperator)(), actual)
        }

        withTests(nameFn = { "${it.id}/rejects-other-types" }, entryCases.filter { it.kind.mismatch != null }) { case ->
            val kind = case.kind
            val error = assertFailsWith<InvalidTermTypeException> { kind.parse(case.entry, kind.mismatch!!, null) }
            assertEquals(kind.type, error.type)
        }

        test("clause/promotes-a-plain-struct-to-a-fact") {
            assertEquals(Fact.of(Struct.of("p", Atom.of("a"))), parser.parseClause("p(a)"))
            assertEquals(Fact.of(Struct.of("p", Atom.of("a"))), "p(a)".parseAsClause())
        }

        test("atom/bare-operator-as-argument") {
            assertEquals(Struct.of("f", Atom.of("+")), parser.parseTerm("f(+)"))
            assertEquals(Struct.of("f", Atom.of("-"), Atom.of("=")), parser.parseTerm("f(-, =)"))
            assertEquals(LogicList.of(Atom.of("+"), Atom.of("*")), parser.parseTerm("[+, *]"))
            assertEquals(Struct.of("=", Atom.of("x"), Atom.of("-")), parser.parseTerm("x = -"))
        }

        test("parsers-with-operators-use-them-by-default") {
            val plusPlus = Operator("++", Specifier.XFX, 700)
            val expected = Struct.of("++", Atom.of("a"), Atom.of("b"))
            assertEquals(expected, TermParser.withOperators(plusPlus).parseTerm("a ++ b"))
            assertEquals(expected, TermParser.withOperators(OperatorSet(plusPlus)).parseTerm("a ++ b"))
        }

        test("parsers-allocate-variables-in-the-given-scope") {
            val scope = Scope.of("X")
            val parsed = TermParser.withDefaultOperators(scope).parseStruct("f(X)")
            assertEquals(scope.varOf("X"), parsed[0])
        }
    })
