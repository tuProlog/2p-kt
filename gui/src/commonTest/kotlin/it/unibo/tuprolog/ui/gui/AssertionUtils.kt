package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermComparator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.classic.stdlib.DefaultBuiltins
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.libs.io.IOLib
import it.unibo.tuprolog.theory.Theory
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

private const val DEBUG = false

fun <T> Iterable<T>.assertions(
    debug: Boolean = DEBUG,
    scope: EventsAsserter<T>.() -> Unit
): EventsAsserter<T>.Checkpoint {
    return EventsAsserter(this).also { if (debug) it.debug() }.also(scope).checkpoint()
}

fun <T> Iterable<T>.beginAssertions(): EventsAsserter<T>.Checkpoint {
    return EventsAsserter(this).checkpoint()
}

fun <T> EventsAsserter<T>.Checkpoint.assertions(
    debug: Boolean = DEBUG,
    scope: EventsAsserter<T>.() -> Unit
): EventsAsserter<T>.Checkpoint = restore().also { if (debug) it.debug() }.also(scope).checkpoint()

fun <T> EventsAsserter<Event<Any>>.assertNextIsEvent(name: String, event: T) {
    assertNextEquals(Event.of(name, event as Any))
}

fun assertSolutionsEquals(expected: Solution, actual: Solution) {
    assertEquals(expected::class, actual::class)
    assertTrue { expected.query.equals(actual.query, useVarCompleteName = false) }
    if (expected is Solution.Yes && actual is Solution.Yes) {
        assertEquals(expected.substitution.size, actual.substitution.size)
        assertEquals(
            expected.substitution.keys.map { it.name }.toSet(),
            actual.substitution.keys.map { it.name }.toSet(),
        )
        expected.substitution.values.sortedWith(TermComparator.DefaultComparator)
            .zip(actual.substitution.values.sortedWith(TermComparator.DefaultComparator))
            .all { (e, a) -> e.equals(a, useVarCompleteName = false) }
            .let { assertTrue(it) }
    } else if (expected is Solution.Halt && actual is Solution.Halt) {
        assertEquals(expected.exception::class, actual.exception::class)
        assertEquals(expected.exception.message, actual.exception.message)
    }
}

fun <T> EventsAsserter<Event<Any>>.assertNextIsSolveEvent(
    name: String,
    event: T,
    operators: OperatorSet = OperatorSet.DEFAULT,
    libraries: Runtime = Runtime.of(IOLib, DefaultBuiltins),
    flags: FlagStore = FlagStore.DEFAULT,
    staticKb: Theory = Theory.empty(),
    dynamicKb: Theory = Theory.empty()
) = aboutNext {
    assertEquals(name, it.name)
    when (event) {
        is Solution -> assertSolutionsEquals(
            event,
            it.event as? Solution ?: throw AssertionError("Not a ${Solution::class.simpleName}: ${it.event}")
        )
        is Term -> assertTrue {
            event.equals(
                it.event as? Term ?: throw AssertionError("Not a ${Term::class.simpleName}: ${it.event}"),
                useVarCompleteName = false
            )
        }
        else -> assertEquals(event as Any, it.event)
    }
    assertIs<SolverEvent<*>>(it)
    assertEquals(operators, it.operators)
    assertEquals(libraries, it.libraries)
    assertEquals(flags, it.flags)
    assertTrue(staticKb.equals(it.staticKb, useVarCompleteName = false))
    assertTrue(dynamicKb.equals(it.dynamicKb, useVarCompleteName = false))
    assertFalse(it.standardInput.isClosed)
    assertFalse(it.standardOutput.isClosed)
    assertFalse(it.standardError.isClosed)
    assertFalse(it.warnings.isClosed)
}
