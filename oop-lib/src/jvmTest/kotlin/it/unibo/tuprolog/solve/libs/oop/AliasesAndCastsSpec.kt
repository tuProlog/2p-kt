package it.unibo.tuprolog.solve.libs.oop

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.parsing.parseAsStruct
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.exception.LogicError
import it.unibo.tuprolog.solve.library.Runtime
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

private val operators = OOPLib.operators + Solver.prolog.defaultBuiltins.operators

/** The first solution of [query], on a solver with the OOP library. */
private fun solve(query: String): Solution =
    Solver.prolog
        .solverWithDefaultBuiltins(
            otherLibraries = Runtime.of(OOPLib),
        ).solveOnce(query.parseAsStruct(operators))

private class Binding(
    val id: String,
    val query: String,
    val expected: Term,
)

private val bindings =
    listOf(
        Binding(
            "alias-as-method-argument",
            "new_object('java.util.ArrayList', [], L), register(L, l), invoke_method(L, add(\$l), X)",
            Truth.TRUE,
        ),
        Binding(
            "alias-as-property-receiver",
            "new_object('java.util.ArrayList', [], L), register(L, l), X := \$l.size",
            Integer.of(0),
        ),
        Binding(
            "cast-to-a-type-name",
            "new_object('java.util.ArrayList', [], L), invoke_method(L, add(1 as 'java.lang.Long'), _), " +
                "invoke_method(L, get(0), X)",
            Integer.of(1),
        ),
        Binding(
            "cast-to-an-aliased-type",
            "type('java.lang.Long', T), register(T, lg), new_object('java.util.ArrayList', [], L), " +
                "invoke_method(L, add(1 as \$lg), X)",
            Truth.TRUE,
        ),
        Binding("static-property", "type('java.lang.Integer', T), X := T.'MAX_VALUE'", Integer.of(Int.MAX_VALUE)),
        Binding(
            "static-property-assignment",
            "type('${StaticHolder::class.qualifiedName}', T), T.value := 5, X := T.value",
            Integer.of(5),
        ),
    )

private class Failure(
    val id: String,
    val query: String,
    val error: String,
)

private val failures =
    listOf(
        Failure("cast-to-a-non-type", "cast(1, foo, _)", "type_error"),
        Failure("cast-to-an-unknown-alias", "cast(1, \$nope, _)", "existence_error"),
        Failure("invoke-on-an-unknown-alias", "invoke_method(\$nope, size, _)", "existence_error"),
        Failure("invoke-on-a-non-reference", "invoke_method(1, size, _)", "type_error"),
        Failure(
            "unknown-alias-as-argument",
            "new_object('java.util.ArrayList', [], L), invoke_method(L, add(\$nope), _)",
            "representation_error",
        ),
        Failure(
            "cast-to-an-unknown-alias-as-argument",
            "new_object('java.util.ArrayList', [], L), invoke_method(L, add(1 as \$nope), _)",
            "representation_error",
        ),
        Failure(
            "cast-to-a-malformed-type-as-argument",
            "new_object('java.util.ArrayList', [], L), invoke_method(L, add(1 as foo(bar)), _)",
            "representation_error",
        ),
        Failure(
            "method-throwing",
            "new_object('java.util.ArrayList', [], L), invoke_method(L, get(5), _)",
            "system_error",
        ),
        Failure("unbound-type-name", "type(_, _)", "instantiation_error"),
    )

/** A type with a static mutable property, to assign through a type reference. */
object StaticHolder {
    @JvmStatic
    var value: Int = 0
}

class AliasesAndCastsSpec :
    FunSpec({
        withTests(nameFn = { "binds/${it.id}" }, bindings) { case ->
            val solution = solve(case.query)
            assertIs<Solution.Yes>(solution, case.query)
            assertEquals(case.expected, solution.substitution.getByName("X"))
        }

        withTests(nameFn = { "raises/${it.id}" }, failures) { case ->
            val solution = solve(case.query)
            assertIs<Solution.Halt>(solution, case.query)
            val error = (solution.exception as LogicError).errorStruct[0]
            assertTrue(error.toString().startsWith(case.error), "$error")
        }

        test("cast-to-an-unbound-type/enumerates-the-admissible-types-and-their-supertypes") {
            val t = Var.of("T")
            val types =
                Solver.prolog
                    .solverWithDefaultBuiltins(otherLibraries = Runtime.of(OOPLib))
                    .solve(Struct.of("cast", Integer.of(1), t, Var.anonymous()))
                    .map { it.substitution[t] }
                    .toList()
            listOf(Long::class, Int::class, Number::class, Any::class).forEach {
                assertTrue(TypeRef.of(it) in types, "$it in $types")
            }
            assertEquals(types.size, types.toSet().size)
        }

        test("cast-of-an-unbound-term/is-null") {
            assertEquals(ObjectRef.NULL, solve("cast(X, T, R)").substitution.getByName("R"))
        }
    })
