package it.unibo.tuprolog.solve.libs.oop

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.assertExamplesHold
import it.unibo.tuprolog.solve.assertFullyDocumented
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.library.Runtime
import kotlin.test.assertEquals
import kotlin.test.assertIs

class OOPLibSpec :
    FunSpec({
        test("self-equality") {
            assertEquals(OOPLib, OOPLib)
        }

        test("item-equality") {
            assertEquals(Runtime.of(OOPLib), Runtime.of(OOPLib))
        }

        test("every-item-is-documented") {
            assertFullyDocumented(OOPLib)
        }

        test("argument-errors-are-not-wrapped-into-system-errors") {
            val solver = Solver.prolog.solverWithDefaultBuiltins(otherLibraries = Runtime.of(OOPLib))
            val solution = solver.solveOnce(Struct.of("type", Var.of("Name"), Atom.of("java.lang.String")))
            assertIs<TypeError>((solution as? Solution.Halt)?.exception, "$solution")
        }

        withTests(
            nameFn = { "calls-matching-no-overload-raise-existence-errors/${it.id}" },
            listOf(
                NoOverloadCase("constructor") { tooManyArguments, list ->
                    Struct.of("new_object", Atom.of("java.util.ArrayList"), tooManyArguments, list)
                },
                NoOverloadCase("method") { tooManyArguments, list ->
                    Struct.of(
                        ",",
                        Struct.of("new_object", Atom.of("java.util.ArrayList"), List.empty(), list),
                        Struct.of("invoke_method", list, Struct.of("add", tooManyArguments.toList()), Var.of("R")),
                    )
                },
            ),
        ) { case ->
            val solver = Solver.prolog.solverWithDefaultBuiltins(otherLibraries = Runtime.of(OOPLib))
            val solution = solver.solveOnce(case.goal(List.of((1..3).map { Integer.of(it) }), Var.of("L")))
            assertIs<ExistenceError>((solution as? Solution.Halt)?.exception, "$solution")
        }

        withTests(
            nameFn = { "numbers-are-converted-back-to-numbers/${it.replace('_', '-')}" },
            listOf("array_items", "list_items", "set_items"),
        ) { functor ->
            val solver = Solver.prolog.solverWithDefaultBuiltins(otherLibraries = Runtime.of(OOPLib))
            val items = List.of(Integer.of(1), Real.of(2.5), Atom.of("a"))
            val ref = Var.of("Ref")
            val back = Var.of("Back")
            val goal = Struct.of(",", Struct.of(functor, ref, items), Struct.of(functor, ref, back))
            val solution = solver.solveOnce(goal)
            assertEquals(items, solution.substitution[back], "$functor: $solution")
        }

        test("documentation-examples-hold") {
            assertExamplesHold(OOPLib, skip = emptySet()) {
                Solver.prolog.solverWithDefaultBuiltins(otherLibraries = Runtime.of(OOPLib))
            }
        }
    })

private class NoOverloadCase(
    val id: String,
    val goal: (tooManyArguments: List, list: Var) -> Struct,
)
