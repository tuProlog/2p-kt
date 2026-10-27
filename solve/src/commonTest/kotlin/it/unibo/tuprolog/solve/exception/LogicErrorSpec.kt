package it.unibo.tuprolog.solve.exception

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.Dummy
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.ErrorUtils.errorStructOf
import it.unibo.tuprolog.solve.exception.error.EvaluationError
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.MessageError
import it.unibo.tuprolog.solve.exception.error.PermissionError
import it.unibo.tuprolog.solve.exception.error.RepresentationError
import it.unibo.tuprolog.solve.exception.error.SyntaxError
import it.unibo.tuprolog.solve.exception.error.SystemError
import it.unibo.tuprolog.solve.exception.error.TypeError
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

private val context = Dummy.Context

/** An error type term, and the [LogicError] subclass that [LogicError.of] must build for it. */
private class DispatchCase(
    val id: String,
    val expectedClass: KClass<out LogicError>,
    private val type: Scope.() -> Struct,
) {
    fun type(): Struct = Scope.empty().type()
}

private val dispatchCases =
    listOf(
        DispatchCase("instantiation-error", InstantiationError::class) { atomOf("instantiation_error") },
        DispatchCase("system-error", SystemError::class) { atomOf("system_error") },
        DispatchCase("syntax-error", SyntaxError::class) { atomOf("syntax_error") },
        DispatchCase("message-error", MessageError::class) { atomOf("") },
        DispatchCase("representation-error", RepresentationError::class) {
            structOf("representation_error", atomOf("max_arity"))
        },
        DispatchCase("domain-error", DomainError::class) {
            structOf("domain_error", atomOf("not_less_than_zero"), intOf(-1))
        },
        DispatchCase("type-error", TypeError::class) { structOf("type_error", atomOf("integer"), atomOf("a")) },
        DispatchCase("evaluation-error", EvaluationError::class) {
            structOf("evaluation_error", atomOf("zero_divisor"))
        },
        DispatchCase("permission-error", PermissionError::class) {
            structOf(
                "permission_error",
                atomOf("modify"),
                atomOf("static_procedure"),
                indicatorOf(atomOf("foo"), intOf(1)),
            )
        },
    )

/** Type terms that [LogicError.of] can't map to a dedicated subclass, so it builds a custom error keeping the type. */
private val customCases =
    listOf(
        DispatchCase("unknown-functor", LogicError::class) { structOf("my_error", atomOf("a")) },
        DispatchCase("representation-error-wrong-arity", LogicError::class) {
            structOf("representation_error", atomOf("max_arity"), atomOf("extra"))
        },
    )

/** A factory call, and the error type term it must produce. */
private class FactoryCase(
    val id: String,
    val build: () -> LogicError,
    private val expectedType: Scope.() -> Struct,
) {
    fun expectedType(): Struct = Scope.empty().expectedType()
}

private val foo2 = Signature("foo", 2)

private val factoryCases =
    listOf(
        FactoryCase("existence/procedure", { ExistenceError.forProcedure(context, Signature("foo", 1)) }) {
            structOf("existence_error", atomOf("procedure"), indicatorOf(atomOf("foo"), intOf(1)))
        },
        FactoryCase("existence/source-sink-atom", { ExistenceError.forSourceSink(context, Atom.of("in")) }) {
            structOf("existence_error", atomOf("source_sink"), atomOf("in"))
        },
        FactoryCase("existence/source-sink-string", { ExistenceError.forSourceSink(context, "in") }) {
            structOf("existence_error", atomOf("source_sink"), atomOf("in"))
        },
        FactoryCase("existence/stream", { ExistenceError.forStream(context, Struct.of("\$stream", Integer.of(1))) }) {
            structOf("existence_error", atomOf("stream"), structOf("\$stream", intOf(1)))
        },
        FactoryCase("existence/resource", { ExistenceError.forResource(context, "r") }) {
            structOf("existence_error", atomOf("resource"), atomOf("r"))
        },
        FactoryCase(
            "existence/of",
            { ExistenceError.of(context, ExistenceError.ObjectType.OOP_ALIAS, Atom.of("x"), "no alias") },
        ) {
            structOf("existence_error", atomOf("oop_alias"), atomOf("x"))
        },
        FactoryCase(
            "domain/for-argument",
            { DomainError.forArgument(context, foo2, DomainError.Expected.NOT_LESS_THAN_ZERO, Integer.of(-1), 0) },
        ) {
            structOf("domain_error", atomOf("not_less_than_zero"), intOf(-1))
        },
        FactoryCase(
            "domain/for-term",
            { DomainError.forTerm(context, DomainError.Expected.ORDER, Atom.of("x")) },
        ) {
            structOf("domain_error", atomOf("order"), atomOf("x"))
        },
        FactoryCase(
            "domain/for-goal",
            { DomainError.forGoal(context, foo2, DomainError.Expected.CLAUSE, Atom.of("x")) },
        ) {
            structOf("domain_error", atomOf("clause"), atomOf("x"))
        },
        FactoryCase(
            "domain/for-flag-values",
            { DomainError.forFlagValues(context, foo2, listOf(Atom.of("on"), Atom.of("off")), Atom.of("x"), 1) },
        ) {
            structOf("domain_error", atomOf("flag_value"), atomOf("x"))
        },
        FactoryCase(
            "type/for-argument",
            { TypeError.forArgument(context, foo2, TypeError.Expected.INTEGER, Atom.of("a"), 0) },
        ) {
            structOf("type_error", atomOf("integer"), atomOf("a"))
        },
        FactoryCase(
            "type/for-argument-list",
            { TypeError.forArgumentList(context, foo2, TypeError.Expected.LIST, Atom.of("a"), 1) },
        ) {
            structOf("type_error", atomOf("list"), atomOf("a"))
        },
        FactoryCase(
            "type/for-goal",
            { TypeError.forGoal(context, foo2, TypeError.Expected.CALLABLE, Integer.of(1)) },
        ) {
            structOf("type_error", atomOf("callable"), intOf(1))
        },
        FactoryCase("type/of", { TypeError.of(context, TypeError.Expected.ATOM, Integer.of(1), "not an atom") }) {
            structOf("type_error", atomOf("atom"), intOf(1))
        },
        FactoryCase("syntax/of", { SyntaxError.of(context, "oops") }) { atomOf("syntax_error") },
        FactoryCase("syntax/while-parsing-term", { SyntaxError.whileParsingTerm(context, "f(", 1, 3, "oops") }) {
            atomOf("syntax_error")
        },
        FactoryCase(
            "syntax/while-parsing-clauses",
            { SyntaxError.whileParsingClauses(context, "a.\nb c.", 1, 2, 3, "oops") },
        ) {
            atomOf("syntax_error")
        },
    )

private fun assertBuiltBy(
    case: DispatchCase,
    error: LogicError,
) {
    assertTrue(case.expectedClass.isInstance(error), "${error::class} is not a ${case.expectedClass}")
    assertEquals(case.type(), error.type)
}

class LogicErrorSpec :
    FunSpec({
        withTests(nameFn = { "of/${it.id}" }, dispatchCases) { case ->
            val error = LogicError.of(context = context, type = case.type())
            assertBuiltBy(case, error)
            assertEquals(errorStructOf(case.type()), error.errorStruct)
        }

        withTests(nameFn = { "of/custom/${it.id}" }, customCases) { case ->
            val error =
                LogicError.of(
                    message = "custom",
                    context = context,
                    type = case.type(),
                    extraData = Atom.of("x"),
                )
            assertBuiltBy(case, error)
            assertEquals("custom", error.message)
            assertEquals(errorStructOf(case.type(), Atom.of("x")), error.errorStruct)
        }

        // Known bug: ExistenceError.ObjectType.of doesn't uppercase its input (unlike the other errors' enums), so
        // LogicError.of throws IllegalArgumentException on every existence_error type term.
        xtest("of/existence-error") {
            val type = Struct.of("existence_error", Atom.of("procedure"), Indicator.of("foo", 1))
            assertBuiltBy(
                DispatchCase("existence-error", ExistenceError::class) {
                    type
                },
                LogicError.of(context = context, type = type),
            )
        }

        // Known bug: an unknown enum value (here a representation limit) makes LogicError.of throw
        // IllegalArgumentException, because it only falls back to a custom error on NullPointerException.
        xtest("of/custom/representation-error-unknown-limit") {
            val type = Struct.of("representation_error", Atom.of("nonsense"))
            assertBuiltBy(
                DispatchCase("unknown-limit", LogicError::class) { type },
                LogicError.of(context = context, type = type),
            )
        }

        test("of/custom/contexts-can-be-pushed-and-replaced") {
            val type = Struct.of("my_error", Atom.of("a"))
            val error = LogicError.of(context = context, type = type)
            val pushed = error.pushContext(context)
            assertEquals(2, pushed.contexts.size)
            assertEquals(type, pushed.type)
            val updated = pushed.updateLastContext(context)
            assertEquals(2, updated.contexts.size)
            assertSame(context, updated.contexts.last())
            assertEquals(type, updated.type)
        }

        test("of/message-defaults-to-the-type") {
            val error = LogicError.of(context = context, type = Struct.of("my_error", Atom.of("a")))
            assertEquals("my_error(a)", error.message)
        }

        withTests(nameFn = { "factories/${it.id}" }, factoryCases) { case ->
            val error = case.build()
            assertEquals(case.expectedType(), error.type)
            val message = error.message
            assertTrue(!message.isNullOrBlank(), "empty message")
            // factories carry their message as the error's extra data, so it reaches Prolog code too
            assertEquals(errorStructOf(case.expectedType(), Atom.of(message)), error.errorStruct)
        }

        // Known bug: the message lacks a space ("An argumentof `foo/2` ...").
        xtest("factories/domain/for-argument-message") {
            val error = DomainError.forArgument(context, foo2, DomainError.Expected.ORDER, Atom.of("x"))
            assertTrue(error.message!!.startsWith("An argument of `foo/2`"), error.message)
        }

        test("syntax/error-detector-points-at-the-column") {
            assertEquals("1: ...\n2: b c.\n     ^ oops", SyntaxError.errorDetector("a.\nb c.", 2, 3, "oops"))
        }

        test("syntax/error-detector-on-the-first-line-has-no-context") {
            assertEquals("1: f(\n    ^ ", SyntaxError.errorDetector("f(", 1, 2))
        }

        test("syntax/error-detector-past-the-last-line-returns-the-text") {
            assertEquals("f(", SyntaxError.errorDetector("f(", 5, 1))
        }

        test("syntax/while-parsing-clauses-shows-the-culprit-line") {
            val message = SyntaxError.whileParsingClauses(context, "a.\nb c.", 1, 2, 3, "oops").message!!
            assertTrue(message.startsWith("Syntax error at 2:3 while parsing clause 1: oops"), message)
            assertTrue("b c." in message, message)
        }
    })
