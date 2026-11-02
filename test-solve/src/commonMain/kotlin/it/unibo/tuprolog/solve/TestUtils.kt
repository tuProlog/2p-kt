/**
 * Miscellaneous utils for testing
 *
 * @author Enrico
 */
@file:JvmName("TestUtils")

package it.unibo.tuprolog.solve

import io.kotest.assertions.fail
import io.kotest.assertions.withClue
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
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

private inline val loggingOn get() = false

/** Utility function to help writing tests; it creates a mapping between the receiver goal struct and the list of given solutions */
fun <S : Solution> Struct.hasSolutions(vararg solution: Struct.() -> S) = this to solution.map { it() }

/** Utility function to help writing tests; it creates a [Solution.Yes] with receiver query and provided substitution */
fun Struct.yes(vararg withSubstitution: Substitution) =
    Solution.yes(
        this,
        Substitution.of(withSubstitution.flatMap { s -> s.map { it.toPair() } }) as Substitution.Unifier,
    )

/** Utility function to help writing tests; it creates a [Solution.No] with receiver query */
fun Struct.no() = Solution.no(this)

/** Utility function to help writing tests; it creates a [Solution.Halt] with receiver query and provided exception */
fun Struct.halt(withException: ResolutionException) = Solution.halt(this, withException)

/** Utility function to help writing tests; it forwards the `copy` method call to subclasses changing only the `query` field */
fun Solution.changeQueryTo(query: Struct) =
    whenIs(
        yes = { it.copy(query) },
        no = { it.copy(query) },
        halt = { it.copy(query) },
    )

/** Utility function to help writing tests; applies [changeQueryTo] to all [Solution]s in receiver iterable */
fun Iterable<Solution>.changeQueriesTo(query: Struct) = map { it.changeQueryTo(query) }

/** Utility function to assert [assertion] over thrown exception by [throwExpression] */
inline fun <reified E : Throwable> assertOverFailure(
    throwExpression: () -> Unit,
    assertion: (E) -> Unit,
) = try {
    throwExpression()
    fail("Expected an Exception to be thrown!")
} catch (error: Throwable) {
    withClue("Thrown error `${error::class}` is not of expected type `${E::class}`") { error.shouldBeInstanceOf<E>() }
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
fun assertSolutionEquals(
    expected: Solution,
    actual: Solution,
) {
    fun reportMsg(
        expected: Any,
        actual: Any,
        motivation: String = "",
    ) = "Expected: `$expected`\nActual\t: `$actual`" + if (motivation.isNotBlank()) " ($motivation)" else ""

    fun assertSameClass(
        expected: Solution,
        actual: Solution,
    ) = withClue(reportMsg(expected, actual)) { actual::class shouldBe expected::class }

    fun assertSameQuery(
        expected: Solution,
        actual: Solution,
    ) = withClue(reportMsg(expected, actual)) { actual.query shouldBe expected.query }

    when {
        expected is Solution.Halt -> {
            assertSameClass(expected, actual)
            assertSameQuery(expected, actual)
            withClue(reportMsg(expected, actual, "Wrong substitution")) {
                actual.substitution shouldBe
                    expected.substitution
            }
            withClue(reportMsg(expected, actual, "Solution is not Halt")) { actual.shouldBeInstanceOf<Solution.Halt>() }
            withClue(reportMsg(expected, actual, "Wrong exception type")) {
                (actual as Solution.Halt).exception::class shouldBe
                    expected.exception::class
            }
            when (val expectedEx = expected.exception) {
                is LogicError -> {
                    withClue(
                        reportMsg(
                            expected,
                            actual,
                            "Exception is not LogicError",
                        ),
                    ) { actual.exception.shouldBeInstanceOf<LogicError>() }
                    val actualEx = actual.exception as LogicError
                    withClue(reportMsg(expected, actual, "The error structs do not match")) {
                        expectedEx.errorStruct.equals(actualEx.errorStruct, false).shouldBeTrue()
                    }
                    withClue(reportMsg(expected, actual, "Different messages")) {
                        actualEx.message shouldBe
                            expectedEx.message
                    }
                }
            }
        }

        expected.substitution.values
            .asSequence()
            .flatMap { it.variables }
            .any() -> {
            assertSameClass(expected, actual)
            assertSameQuery(expected, actual)
            withClue(reportMsg(expected, actual)) { actual.substitution.count() shouldBe expected.substitution.count() }
            withClue(reportMsg(expected, actual)) { actual.substitution.keys shouldBe expected.substitution.keys }

            expected.substitution.forEach { (varExpected, termExpected) ->
                actual.substitution[varExpected]!!.let { termActual ->
                    withClue(
                        reportMsg(termExpected, termActual),
                    ) { termActual.structurallyEquals(termExpected).shouldBeTrue() }

                    // if the substitution contain variables, compare only names, because instances will be different
                    withClue("Comparing variable names of expected `$expected` with `$actual`") {
                        (termActual.variables.map { it.name }.toList()) shouldBe
                            (termExpected.variables.map { it.name }.toList())
                    }
                }
            }
        }
        else -> actual shouldBe expected
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
    equalityAssertion: (Solution, Solution) -> Unit = ::assertSolutionEquals,
) {
    withClue("Expected: `${expected.toList()}`\nActual: `${actual.toList()}`") {
        actual.count() shouldBe
            expected.count()
    }

    expected.zip(actual).forEach { (expected, actual) -> equalityAssertion(expected, actual) }
}

/** Utility method to solve goals in [goalToSolutions] with [solver] and check if solutions are as expected by means of [assertSolutionEquals] */
fun assertSolverSolutionsCorrect(
    solver: Solver,
    goalToSolutions: List<Pair<Struct, List<Solution>>>,
    maxDuration: TimeDuration,
) {
    goalToSolutions.forEach { (goal, solutionList) ->
        if (loggingOn) solver.logKBs()

        val solutions = solver.solve(goal, maxDuration).toList()
        assertSolutionEquals(solutionList, solutions)

        if (loggingOn) logGoalAndSolutions(goal, solutions)
    }
}

/** Asserts that the receiver [Solver]'s API ([Solver.libraries]) exposes [rule] (a rule-based built-in predicate). */
fun Solver.assertHasPredicateInAPI(rule: RuleWrapper<*>) {
    assertHasPredicateInAPI(rule.signature)
}

/** Asserts that the receiver [Solver]'s API ([Solver.libraries]) exposes [primitive] (a primitive built-in). */
fun Solver.assertHasPredicateInAPI(primitive: PrimitiveWrapper<*>) {
    assertHasPredicateInAPI(primitive.signature)
}

/** Asserts that the receiver [Solver]'s API ([Solver.libraries]) exposes a predicate matching [signature]. */
fun Solver.assertHasPredicateInAPI(signature: Signature) {
    assertHasPredicateInAPI(signature.name, signature.arity, signature.vararg)
}

/** Asserts that the receiver [Solver]'s API ([Solver.libraries]) exposes a predicate `functor/arity`. */
fun Solver.assertHasPredicateInAPI(
    functor: String,
    arity: Int,
    vararg: Boolean = false,
) {
    val varargMsg = if (vararg) "(vararg) " else ""
    withClue("Missing predicate $functor/$arity ${varargMsg}in solver API") {
        (
            Signature(functor, arity, vararg) in
                libraries
        ).shouldBeTrue()
    }
    if (loggingOn) println("Solver has predicate $functor/$arity ${varargMsg}in its API")
}

/** Utility function to log loaded Solver databases */
fun Solver.logKBs() {
    println(if (staticKb.clauses.any()) staticKb.toString(true) else "")
    println(if (dynamicKb.clauses.any()) dynamicKb.toString(true) else "")
}

/** Utility function to log passed goal and solutions */
fun logGoalAndSolutions(
    goal: Struct,
    solutions: Iterable<Solution>,
) {
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

/**
 * Renders the internal state of the value returned by [x] for diagnostic logging: `toString()` on the JVM,
 * `JSON.stringify` on JS.
 */
expect fun internalsOf(x: () -> Any): String

/** Logs the value returned by [x] to the platform's error/debug console (`System.err` on the JVM, `console.log` on JS). */
expect fun log(x: () -> Any): Unit

/**
 * Asserts that [klass]'s runtime name matches the expected [name]. Comparison is platform-specific because Kotlin/JS
 * does not preserve fully qualified class names: the JVM `actual` compares [klass]'s qualified name against [name]
 * verbatim, while the JS `actual` compares only the last, dot-separated segment of [name] against [klass]'s simple
 * name.
 */
expect fun <T : Any> assertClassNameIs(
    klass: KClass<T>,
    name: String,
)

/**
 * Asserts that the receiver [Solver] was built with exactly the given [libraries], [staticKb], [dynamicKb], [flags],
 * [inputs] and [outputs], used by [solverConstructionContract] to check a `Solver`/`MutableSolver` built via either
 * [SolverFactory.solverOf]-style factory methods or the builder API ends up with the expected configuration.
 */
fun Solver.assertHas(
    libraries: Runtime,
    staticKb: Theory,
    dynamicKb: Theory,
    flags: FlagStore,
    inputs: InputStore,
    outputs: OutputStore,
) {
    assertRuntimesAreEqual(libraries, this.libraries)
    this.staticKb shouldBe staticKb
    this.dynamicKb shouldBe dynamicKb
    this.flags shouldBe flags
    assertChannelStoresAreEquals(inputs, this.inputChannels)
    assertChannelStoresAreEquals(outputs, this.outputChannels)
}

/** Asserts that [expected] and [actual] hold the same libraries, under the same aliases (see [assertLibrariesAreEqual]). */
internal fun assertRuntimesAreEqual(
    expected: Runtime,
    actual: Runtime,
) {
    actual.keys shouldBe expected.keys
    for ((alias, e) in expected) {
        val a = actual[alias]
        a.shouldNotBeNull()
        assertLibrariesAreEqual(e, a)
    }
}

/** Asserts that [expected] and [actual] have the same alias, clauses, operators, primitives and functions. */
internal fun assertLibrariesAreEqual(
    expected: Library,
    actual: Library,
) {
    actual.alias shouldBe expected.alias
    actual.clauses shouldBe expected.clauses
    actual.operators shouldBe expected.operators
    actual.primitives.keys shouldBe expected.primitives.keys
    for ((signature, e) in expected.primitives) {
        val a = actual.primitives[signature]
        withClue("Wrong primitive: $signature. Expected $e, actual $a.") { a shouldBe e }
    }
    actual.functions.keys shouldBe expected.functions.keys
    for ((signature, e) in expected.functions) {
        val a = actual.functions[signature]
        withClue("Wrong function: $signature. Expected $e, actual $a.") { a shouldBe e }
    }
}

/** Asserts that [expected] and [actual] are channels of the same runtime type and open/closed state. */
internal fun <C : Channel<*>> assertChannelAreEquals(
    expected: C,
    actual: C,
) {
    withClue("Wrong channel type. Expected ${expected::class}, actual ${actual::class}") {
        actual::class shouldBe
            expected::class
    }
    actual.isClosed shouldBe expected.isClosed
}

/** Asserts that [expected] and [actual] declare the same aliases, each bound to an equal channel (see [assertChannelAreEquals]). */
internal fun <C : Channel<*>, CS : ChannelStore<*, C, CS>> assertChannelStoresAreEquals(
    expected: CS,
    actual: CS,
) {
    actual.keys shouldBe expected.keys
    for (alias in expected.keys) {
        assertChannelAreEquals(expected[alias]!!, actual[alias]!!)
    }
}
