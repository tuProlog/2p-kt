/**
 * Miscellaneous utils for testing
 *
 * @author Enrico
 */
@file:JvmName("TestUtils")

package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.solve.channel.Channel
import it.unibo.tuprolog.solve.channel.ChannelStore
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.exception.LogicError
import it.unibo.tuprolog.solve.exception.ResolutionException
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper
import it.unibo.tuprolog.solve.rule.RuleWrapper
import it.unibo.tuprolog.theory.Theory
import kotlin.jvm.JvmName
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.fail

private inline val loggingOn get() = false

fun <T> ktListConcat(l1: List<T>, l2: List<T>): List<T> = l1 + l2

/** Utility function to help writing tests; it creates a mapping between the receiver goal struct and the list of given solutions */
fun <S : Solution> Struct.hasSolutions(vararg solution: Struct.() -> S) =
    this to solution.map { it() }

/** Utility function to help writing tests; it creates a [Solution.Yes] with receiver query and provided substitution */
fun Struct.yes(vararg withSubstitution: Substitution) = Solution.yes(
    this,
    Substitution.of(withSubstitution.flatMap { s -> s.map { it.toPair() } }) as Substitution.Unifier
)

/** Utility function to help writing tests; it creates a [Solution.No] with receiver query */
fun Struct.no() = Solution.no(this)

/** Utility function to help writing tests; it creates a [Solution.Halt] with receiver query and provided exception */
fun Struct.halt(withException: ResolutionException) = Solution.halt(this, withException)

/** Utility function to help writing tests; it forwards the `copy` method call to subclasses changing only the `query` field */
fun Solution.changeQueryTo(query: Struct) = whenIs(
    yes = { it.copy(query) },
    no = { it.copy(query) },
    halt = { it.copy(query) },
)

/** Utility function to help writing tests; applies [changeQueryTo] to all [Solution]s in receiver iterable */
fun Iterable<Solution>.changeQueriesTo(query: Struct) = map { it.changeQueryTo(query) }

/** Utility function to assert [assertion] over thrown exception by [throwExpression] */
inline fun <reified E : Throwable> assertOverFailure(throwExpression: () -> Unit, assertion: (E) -> Unit) =
    try {
        throwExpression()
        fail("Expected an Exception to be thrown!")
    } catch (error: Throwable) {
        assertTrue("Thrown error `${error::class}` is not of expected type `${E::class}`") { error is E }
        assertion(error as E)
    }

/**
 * Utility method to assert that two [Solution]s are equals, with some exceptions.
 *
 * 1) In case of a [Solution.halt], the contained exception is checked only to be of the correct expected class
 *
 * 2) In case a substitution points to a variable or a term containing variables (i.e. `X/Y` or `X/a(Y)` ),
 * **these variables are compared only by name**, because instances will differ
 */
fun assertSolutionEquals(expected: Solution, actual: Solution) {
    fun reportMsg(expected: Any, actual: Any, motivation: String = "") =
        "Expected: `$expected`\nActual\t: `$actual`" + if (motivation.isNotBlank()) " ($motivation)" else ""

    fun assertSameClass(expected: Solution, actual: Solution) =
        assertEquals(expected::class, actual::class, reportMsg(expected, actual))

    fun assertSameQuery(expected: Solution, actual: Solution) =
        assertEquals(expected.query, actual.query, reportMsg(expected, actual))

    when {
        expected is Solution.Halt -> {
            assertSameClass(expected, actual)
            assertSameQuery(expected, actual)
            assertEquals(expected.substitution, actual.substitution, reportMsg(expected, actual, "Wrong substitution"))
            assertTrue(reportMsg(expected, actual, "Solution is not Halt")) { actual is Solution.Halt }
            assertEquals(
                expected.exception::class,
                (actual as Solution.Halt).exception::class,
                reportMsg(expected, actual, "Wrong exception type")
            )
            when (val expectedEx = expected.exception) {
                is LogicError -> {
                    assertTrue(
                        reportMsg(
                            expected,
                            actual,
                            "Exception is not LogicError"
                        )
                    ) { actual.exception is LogicError }
                    val actualEx = actual.exception as LogicError
                    assertTrue(reportMsg(expected, actual, "The error structs do not match")) {
                        expectedEx.errorStruct.equals(actualEx.errorStruct, false)
                    }
                    assertEquals(
                        expectedEx.message,
                        actualEx.message,
                        reportMsg(expected, actual, "Different messages")
                    )
                }
            }
        }

        expected.substitution.values.asSequence().flatMap { it.variables }.any() -> {
            assertSameClass(expected, actual)
            assertSameQuery(expected, actual)
            assertEquals(expected.substitution.count(), actual.substitution.count(), reportMsg(expected, actual))
            assertEquals(expected.substitution.keys, actual.substitution.keys, reportMsg(expected, actual))

            expected.substitution.forEach { (varExpected, termExpected) ->
                actual.substitution[varExpected]!!.let { termActual ->
                    assertTrue(reportMsg(termExpected, termActual)) { termActual.structurallyEquals(termExpected) }

                    // if the substitution contain variables, compare only names, because instances will be different
                    assertEquals(
                        termExpected.variables.map { it.name }.toList(),
                        termActual.variables.map { it.name }.toList(),
                        "Comparing variable names of expected `$expected` with `$actual`"
                    )
                }
            }
        }
        else -> assertEquals(expected, actual)
    }
}

/**
 * Utility method to assert that two solution iterables are equal by means of given [equalityAssertion],
 * called for each expected-actual solution pair
 *
 * @param equalityAssertion the equality assertion is delegated to [assertSolutionEquals] by default
 */
inline fun assertSolutionEquals(
    expected: Iterable<Solution>,
    actual: Iterable<Solution>,
    equalityAssertion: (Solution, Solution) -> Unit = ::assertSolutionEquals
) {
    assertEquals(expected.count(), actual.count(), "Expected: `${expected.toList()}`\nActual: `${actual.toList()}`")

    expected.zip(actual).forEach { (expected, actual) -> equalityAssertion(expected, actual) }
}

/** Utility method to solve goals in [goalToSolutions] with [solver] and check if solutions are as expected by means of [assertSolutionEquals] */
fun assertSolverSolutionsCorrect(
    solver: Solver,
    goalToSolutions: List<Pair<Struct, List<Solution>>>,
    maxDuration: TimeDuration
) {
    goalToSolutions.forEach { (goal, solutionList) ->
        if (loggingOn) solver.logKBs()

        val solutions = solver.solve(goal, maxDuration).toList()
        assertSolutionEquals(solutionList, solutions)

        if (loggingOn) logGoalAndSolutions(goal, solutions)
    }
}

fun Solver.assertHasPredicateInAPI(rule: RuleWrapper<*>) {
    assertHasPredicateInAPI(rule.signature)
}

fun Solver.assertHasPredicateInAPI(primitive: PrimitiveWrapper<*>) {
    assertHasPredicateInAPI(primitive.signature)
}

fun Solver.assertHasPredicateInAPI(signature: Signature) {
    assertHasPredicateInAPI(signature.name, signature.arity, signature.vararg)
}

fun Solver.assertHasPredicateInAPI(functor: String, arity: Int, vararg: Boolean = false) {
    val varargMsg = if (vararg) "(vararg) " else ""
    assertTrue("Missing predicate $functor/$arity ${varargMsg}in solver API") {
        Signature(functor, arity, vararg) in libraries
    }
    if (loggingOn) println("Solver has predicate $functor/$arity ${varargMsg}in its API")
}

/** Utility function to log loaded Solver databases */
fun Solver.logKBs() {
    println(if (staticKb.clauses.any()) staticKb.toString(true) else "")
    println(if (dynamicKb.clauses.any()) dynamicKb.toString(true) else "")
}

/** Utility function to log passed goal and solutions */
fun logGoalAndSolutions(goal: Struct, solutions: Iterable<Solution>) {
    println("?- $goal.")
    solutions.forEach {
        when (it) {
            is Solution.Yes -> {
                println("yes.\n\t${it.solvedQuery}")
                it.substitution.forEach { vt -> println("\t${vt.key} / ${vt.value}") }
            }
            is Solution.Halt -> println("halt.\n\t${it.exception}")
            is Solution.No -> println("no.")
        }
    }
    println("".padEnd(80, '-'))
}

expect fun internalsOf(x: () -> Any): String

expect fun log(x: () -> Any): Unit

expect fun <T : Any> assertClassNameIs(`class`: KClass<T>, name: String)

fun Solver.assertHas(
    libraries: Runtime,
    staticKb: Theory,
    dynamicKb: Theory,
    flags: FlagStore,
    inputs: InputStore,
    outputs: OutputStore,
) {
    assertRuntimesAreEqual(libraries, this.libraries)
    assertEquals(staticKb, this.staticKb)
    assertEquals(dynamicKb, this.dynamicKb)
    assertEquals(flags, this.flags)
    assertChannelStoresAreEquals(inputs, this.inputChannels)
    assertChannelStoresAreEquals(outputs, this.outputChannels)
}

internal fun assertRuntimesAreEqual(expected: Runtime, actual: Runtime) {
    assertEquals(expected.keys, actual.keys)
    for ((alias, e) in expected) {
        val a = actual[alias]
        assertNotNull(a)
        assertLibrariesAreEqual(e, a)
    }
}

internal fun assertLibrariesAreEqual(expected: Library, actual: Library) {
    assertEquals(expected.alias, actual.alias)
    assertEquals(expected.clauses, actual.clauses)
    assertEquals(expected.operators, actual.operators)
    assertEquals(expected.primitives.keys, actual.primitives.keys)
    for ((signature, e) in expected.primitives) {
        val a = actual.primitives[signature]
        assertEquals(
            expected = e,
            actual = a,
            message = "Wrong primitive: $signature. Expected $e, actual $a."
        )
    }
    assertEquals(expected.functions.keys, actual.functions.keys)
    for ((signature, e) in expected.functions) {
        val a = actual.functions[signature]
        assertEquals(
            expected = e,
            actual = a,
            message = "Wrong function: $signature. Expected $e, actual $a."
        )
    }
}

internal fun <C : Channel<*>> assertChannelAreEquals(expected: C, actual: C) {
    assertEquals(
        expected::class,
        actual::class,
        message = "Wrong channel type. Expected ${expected::class}, actual ${actual::class}"
    )
    assertEquals(expected.isClosed, actual.isClosed)
}

internal fun <C : Channel<*>, CS : ChannelStore<*, C, CS>> assertChannelStoresAreEquals(expected: CS, actual: CS) {
    assertEquals(expected.keys, actual.keys)
    for (alias in expected.keys) {
        assertChannelAreEquals(expected[alias]!!, actual[alias]!!)
    }
}
