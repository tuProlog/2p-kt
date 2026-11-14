@file:OptIn(ExperimentalJsExport::class)

package it.unibo.tuprolog.solve.libs.oop

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.NullRef
import it.unibo.tuprolog.core.ObjectRef
import it.unibo.tuprolog.core.Ref
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.Runtime
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/** A host class reachable on every platform: by reflection on the JVM, by its exported members on JS. */
@JsExport
class Counter {
    var count: Int = 0

    fun increment(by: Int): Int {
        count += by
        return count
    }
}

/** An opaque handle of a foreign object, e.g. a Python one, that only [ForeignBackend] reaches. */
class ForeignHandle(
    val id: String,
)

/** A backend for [ForeignHandle]s, standing for a bridge to another runtime: it records what it is asked. */
class ForeignBackend(
    private val reply: String = "foreign",
) : HostInteropBackend {
    val calls = mutableListOf<String>()

    // its objects, and its types, which it creates objects of
    override fun supports(receiver: ObjectRef): Boolean =
        receiver.value is ForeignHandle || (receiver as? TypeRef)?.type == ForeignHandle::class

    override fun invoke(
        receiver: ObjectRef,
        method: String,
        arguments: kotlin.collections.List<Term>,
        converter: TermToObjectConverter,
    ): Result {
        calls += method
        return Result.Value("$reply:${(receiver.value as ForeignHandle).id}.$method/${arguments.size}")
    }

    override fun assign(
        receiver: ObjectRef,
        property: String,
        value: Term,
        converter: TermToObjectConverter,
    ) {
        calls += "$property:="
    }

    override fun create(
        type: ObjectRef,
        arguments: kotlin.collections.List<Term>,
        converter: TermToObjectConverter,
    ): Result = Result.Value(ForeignHandle("new"))
}

private fun solverWith(library: Library) = Solver.prolog.solverWithDefaultBuiltins(otherLibraries = Runtime.of(library))

private fun Solver.answer(
    goal: Struct,
    variable: Var,
): Term? = solveOnce(goal).substitution[variable]

/** Host objects are reached through backends: the platform's one by default, or injected ones, tried in order. */
class HostInteropBackendSpec :
    FunSpec({
        test("platform-backend-creates-invokes-and-assigns") {
            val counter = Var.of("C")
            val result = Var.of("R")
            val goal =
                Tuple.of(
                    Struct.of("new_object", TypeRef.of(Counter::class), List.empty(), counter),
                    Struct.of("invoke_method", counter, Struct.of("increment", Integer.of(2)), Var.anonymous()),
                    Struct.of("assign", counter, Atom.of("count"), Integer.of(5)),
                    Struct.of("invoke_method", counter, Struct.of("increment", Integer.of(1)), result),
                )
            solverWith(OOPLib).answer(goal, result) shouldBe Integer.of(6)
        }

        test("platform-backend-reads-properties") {
            val counter = Counter().also { it.count = 3 }
            val result = Var.of("R")
            val goal = Struct.of("invoke_method", Ref.of(counter), Atom.of("count"), result)
            solverWith(OOPLib).answer(goal, result) shouldBe Integer.of(3)
        }

        test("injected-backend-reaches-its-objects") {
            val result = Var.of("R")
            val goal =
                Struct.of(
                    "invoke_method",
                    Ref.of(ForeignHandle("h")),
                    Struct.of("greet", Atom.of("bob")),
                    result,
                )
            solverWith(OOPLib.withBackends(ForeignBackend())).answer(goal, result) shouldBe Atom.of("foreign:h.greet/1")
        }

        test("injected-backend-assigns-and-creates") {
            val backend = ForeignBackend()
            val solver = solverWith(OOPLib.withBackends(backend))
            solver
                .solveOnce(
                    Struct.of("assign", Ref.of(ForeignHandle("h")), Atom.of("name"), Atom.of("x")),
                ).isYes shouldBe
                true
            backend.calls shouldBe listOf("name:=")
            val created = Var.of("C")
            solver
                .answer(
                    Struct.of("new_object", TypeRef.of(ForeignHandle::class), List.empty(), created),
                    created,
                ).shouldBeInstanceOf<ObjectRef>()
        }

        test("backends-are-tried-in-order") {
            val result = Var.of("R")
            val goal = Struct.of("invoke_method", Ref.of(ForeignHandle("h")), Atom.of("m"), result)
            val library = OOPLib.withBackends(ForeignBackend("first"), ForeignBackend("second"))
            solverWith(library).answer(goal, result) shouldBe Atom.of("first:h.m/0")
        }

        test("other-objects-fall-back-to-the-platform-backend") {
            val result = Var.of("R")
            val goal = Struct.of("invoke_method", Ref.of(Counter()), Struct.of("increment", Integer.of(4)), result)
            solverWith(OOPLib.withBackends(ForeignBackend())).answer(goal, result) shouldBe Integer.of(4)
        }

        test("null-receivers-never-reach-backends") {
            val backend =
                object : HostInteropBackend by ForeignBackend() {
                    val calls = mutableListOf<String>()

                    override fun supports(receiver: ObjectRef): Boolean = true.also { calls += "supports" }
                }
            val goal = Struct.of("invoke_method", NullRef.instance, Atom.of("m"), Var.of("R"))
            solverWith(OOPLib.withBackends(backend)).solveOnce(goal).shouldBeInstanceOf<Solution.Halt>()
            backend.calls.shouldBeEmpty()
        }

        test("solvers-do-not-share-backends") {
            val result = Var.of("R")
            val goal = Struct.of("invoke_method", Ref.of(ForeignHandle("h")), Atom.of("id"), result)
            solverWith(OOPLib.withBackends(ForeignBackend())).answer(goal, result) shouldBe Atom.of("foreign:h.id/0")
            solverWith(OOPLib).answer(goal, result) shouldNotBe Atom.of("foreign:h.id/0")
        }
    })
