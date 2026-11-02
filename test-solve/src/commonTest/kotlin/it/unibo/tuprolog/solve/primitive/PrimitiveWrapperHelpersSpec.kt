package it.unibo.tuprolog.solve.primitive

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.LogicError
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.SystemError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper.Companion.ensuringArgumentIsConstant
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper.Companion.ensuringArgumentIsGround
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper.Companion.ensuringArgumentIsSpecifier
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper.Companion.ensuringArgumentIsWellFormedClause
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper.Companion.ensuringArgumentIsWellFormedIndicator
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper.Companion.ensuringArgumentIsWellFormedList
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper.Companion.ensuringTermIsWellFormedList
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper.Companion.notImplemented
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper.Companion.unify
import it.unibo.tuprolog.solve.primitive.testutils.PrimitiveWrapperUtils.createPrimitiveRequest
import it.unibo.tuprolog.solve.primitive.testutils.PrimitiveWrapperUtils.primitive
import kotlin.reflect.KClass
import it.unibo.tuprolog.core.List as LogicList

private typealias Request = Solve.Request<ExecutionContext>

private fun request(vararg arguments: Term): Request =
    createPrimitiveRequest(Signature("p", arguments.size), arguments.toList())

/**
 * A guard, an argument it accepts, one it rejects, and the error it must raise for the latter. No accepted argument
 * when accepting needs more than the test context offers (e.g. a well-formed indicator is checked against the
 * `max_arity` flag, and the test context has no flags).
 */
private class GuardCase(
    val id: String,
    val accepted: Term?,
    val rejected: Term,
    val error: KClass<out LogicError>,
    val guard: Request.() -> Request,
)

private val wellFormed = LogicList.of(Integer.of(1))
private val partial = LogicList.from(listOf(Integer.of(1)), Var.of("T"))

private val guardCases =
    listOf(
        GuardCase("constant", Atom.of("a"), Struct.of("f", Atom.of("a")), TypeError::class) {
            ensuringArgumentIsConstant(0)
        },
        GuardCase("well-formed-list/partial", wellFormed, partial, DomainError::class) {
            ensuringTermIsWellFormedList(arguments[0])
        },
        GuardCase("well-formed-list/not-a-list", wellFormed, Atom.of("a"), DomainError::class) {
            ensuringTermIsWellFormedList(arguments[0])
        },
        GuardCase("well-formed-list-argument/partial", wellFormed, partial, DomainError::class) {
            ensuringArgumentIsWellFormedList(0)
        },
        GuardCase("well-formed-list-argument/not-a-list", wellFormed, Atom.of("a"), TypeError::class) {
            ensuringArgumentIsWellFormedList(0)
        },
        GuardCase("ground", Struct.of("f", Atom.of("a")), Struct.of("f", Var.of("X")), InstantiationError::class) {
            ensuringArgumentIsGround(0)
        },
        GuardCase("specifier/not-an-atom", Atom.of("xfx"), Integer.of(1), DomainError::class) {
            ensuringArgumentIsSpecifier(0)
        },
        GuardCase("indicator/unbound-name", null, Indicator.of(Var.of("N"), Integer.of(1)), InstantiationError::class) {
            ensuringArgumentIsWellFormedIndicator(0)
        },
        GuardCase(
            "indicator/unbound-arity",
            null,
            Indicator.of(Atom.of("p"), Var.of("A")),
            InstantiationError::class,
        ) {
            ensuringArgumentIsWellFormedIndicator(0)
        },
        GuardCase("clause/not-callable", Struct.of("f", Atom.of("a")), Integer.of(1), TypeError::class) {
            ensuringArgumentIsWellFormedClause(0)
        },
    )

class PrimitiveWrapperHelpersSpec :
    FunSpec({
        withTests(nameFn = { "guards/${it.id}/accepts" }, guardCases.filter { it.accepted != null }) { case ->
            val request = request(case.accepted!!)
            request.(case.guard)() shouldBeSameInstanceAs request
        }

        withTests(nameFn = { "guards/${it.id}/rejects" }, guardCases) { case ->
            val error = shouldThrow<LogicError> { request(case.rejected).(case.guard)() }
            withClue("${error::class} is not a ${case.error}") { case.error.isInstance(error).shouldBeTrue() }
        }

        test("unify/uses-the-context-unificator") {
            val request = request()
            request.unify(Struct.of("f", Var.of("X")), Struct.of("f", Atom.of("a"))) shouldBe
                Struct.of("f", Atom.of("a"))
            request.unify(Atom.of("a"), Atom.of("b")).shouldBeNull()
        }

        test("not-implemented/raises-a-system-error") {
            val error = shouldThrow<SystemError> { request().notImplemented() }
            withClue("cause: ${error.cause}") { error.cause.shouldBeInstanceOf<NotImplementedError>() }
        }

        test("wrap/by-name-and-arity") {
            (PrimitiveWrapper.wrap<ExecutionContext>("p", 2, primitive).signature) shouldBe Signature("p", 2)
        }

        test("wrap/by-name-arity-and-vararg") {
            (PrimitiveWrapper.wrap<ExecutionContext>("p", 1, true, primitive).signature) shouldBe
                Signature("p", 1, vararg = true)
        }
    })
