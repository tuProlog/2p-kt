package it.unibo.tuprolog.solve.exception

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotBeBlank
import io.kotest.matchers.string.shouldNotContain
import io.kotest.matchers.types.shouldBeSameInstanceAs
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
        DispatchCase("syntax-error-with-description", SyntaxError::class) {
            structOf("syntax_error", atomOf("oops"))
        },
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
        FactoryCase("syntax/of", { SyntaxError.of(context, "oops") }) { structOf("syntax_error", atomOf("oops")) },
        FactoryCase("syntax/while-parsing-term", { SyntaxError.whileParsingTerm(context, "f(", 1, 3, "oops") }) {
            structOf("syntax_error", atomOf("oops"))
        },
        FactoryCase(
            "syntax/while-parsing-clauses",
            { SyntaxError.whileParsingClauses(context, "a.\nb c.", 1, 2, 3, "oops") },
        ) {
            structOf("syntax_error", atomOf("oops"))
        },
    )

private fun assertBuiltBy(
    case: DispatchCase,
    error: LogicError,
) {
    withClue("${error::class} is not a ${case.expectedClass}") { case.expectedClass.isInstance(error).shouldBeTrue() }
    error.type shouldBe case.type()
}

class LogicErrorFactoriesSpec :
    FunSpec({
        withTests(nameFn = { "of/${it.id}" }, dispatchCases) { case ->
            val error = LogicError.of(context = context, type = case.type())
            assertBuiltBy(case, error)
            error.errorStruct shouldBe errorStructOf(case.type())
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
            error.message shouldBe "custom"
            error.errorStruct shouldBe errorStructOf(case.type(), Atom.of("x"))
        }

        test("of/existence-error") {
            val type = Struct.of("existence_error", Atom.of("procedure"), Indicator.of("foo", 1))
            assertBuiltBy(
                DispatchCase("existence-error", ExistenceError::class) {
                    type
                },
                LogicError.of(context = context, type = type),
            )
        }

        test("of/custom/representation-error-unknown-limit") {
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
            pushed.contexts.size shouldBe 2
            pushed.type shouldBe type
            val updated = pushed.updateLastContext(context)
            updated.contexts.size shouldBe 2
            updated.contexts.last() shouldBeSameInstanceAs context
            updated.type shouldBe type
        }

        test("of/message-defaults-to-the-type") {
            val error = LogicError.of(context = context, type = Struct.of("my_error", Atom.of("a")))
            error.message shouldBe "my_error(a)"
        }

        withTests(nameFn = { "factories/${it.id}" }, factoryCases) { case ->
            val error = case.build()
            error.type shouldBe case.expectedType()
            val message = error.message
            message.shouldNotBeNull().shouldNotBeBlank()
            // factories carry their message as the error's extra data, so it reaches Prolog code too
            error.errorStruct shouldBe errorStructOf(case.expectedType(), Atom.of(message))
        }

        test("factories/domain/for-argument-message") {
            val error = DomainError.forArgument(context, foo2, DomainError.Expected.ORDER, Atom.of("x"))
            withClue(error.message) { error.message!!.startsWith("An argument of `foo/2`").shouldBeTrue() }
        }

        test("syntax/error-detector-points-at-the-column") {
            SyntaxError.errorDetector("a.\nb c.", 2, 3, "oops") shouldBe "1: ...\n2: b c.\n     ^ oops"
        }

        test("syntax/error-detector-on-the-first-line-has-no-context") {
            SyntaxError.errorDetector("f(", 1, 2) shouldBe "1: f(\n    ^ "
        }

        test("syntax/error-detector-past-the-last-line-returns-the-text") {
            SyntaxError.errorDetector("f(", 5, 1) shouldBe "f("
        }

        test("syntax/while-parsing-clauses-shows-the-culprit-line") {
            val message = SyntaxError.whileParsingClauses(context, "a.\nb c.", 1, 2, 3, "oops").message!!
            withClue(message) { message.startsWith("Syntax error at 2:3 while parsing clause 1: oops").shouldBeTrue() }
            withClue(message) { ("b c." in message).shouldBeTrue() }
        }

        test("message/representation-error-without-a-cause-has-no-null") {
            RepresentationError.of(context, foo2, RepresentationError.Limit.MAX_ARITY).message.shouldNotBeNull() shouldNotContain
                "null"
        }

        test("message/evaluation-error-names-its-type") {
            EvaluationError(context = context, errorType = EvaluationError.Type.ZERO_DIVISOR)
                .message
                .shouldNotBeNull() shouldContain "zero_divisor"
        }
    })
