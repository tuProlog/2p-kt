package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Base class for test suites exercising a [Termificator] implementation, such as [Termificator.default] or
 * [Termificator.legacy]: concrete subclasses implement [createTermificator] and then write `@Test` methods using
 * [assertTermificationWorks]/[assertTermificationFails] to check how individual values get converted.
 *
 * The [companion object][Companion] pre-builds one representative instance of each input shape a [Termificator]
 * is expected to handle (an `Array`, a `List`, a `Set`, a `Sequence`, a `Pair`, a `Triple`, a `Map` and one of
 * its `Map.Entry`s), plus [unconvertible], a value with no registered conversion — so subclasses (and further
 * subclasses overriding individual `@Test` methods, as `TestDefaultTermifier` does over `TestLegacyTermifier`)
 * can assert on the same fixtures while differing only in the expected [Term] shape.
 *
 * A typical concrete test looks like:
 * ```
 * class TestLegacyTermifier : AbstractTermificatorTest() {
 *     override fun createTermificator(scope: Scope): Termificator = Termificator.legacy(scope)
 *
 *     @Test
 *     open fun testSets() {
 *         assertTermificationWorks(set, { set.map { intOf(it) }.let(this::logicListOf) })
 *     }
 * }
 * ```
 */
abstract class AbstractTermificatorTest {
    private lateinit var scope: Scope
    private lateinit var termificator: Termificator

    companion object {
        /** Sample `Array<Int>` input, shared by every subclass's `@Test` methods. */
        val array = arrayOf(1, 2)

        /** Sample `List<Int>` input ([array] as a list), shared by every subclass's `@Test` methods. */
        val list = array.toList()

        /** Sample `Set<Int>` input ([array] as a set), shared by every subclass's `@Test` methods. */
        val set = array.toSet()

        /** Sample `Sequence<Int>` input (over [array]), shared by every subclass's `@Test` methods. */
        val sequence = sequenceOf(*array)

        /** Sample `Pair<String, Int>` input, shared by every subclass's `@Test` methods. */
        val pair = "a" to 1

        /** Sample `Triple<String, Int, Boolean>` input, shared by every subclass's `@Test` methods. */
        val triple = Triple("a", 1, true)

        /** Sample `Map<String, Int>` input (containing [pair]), shared by every subclass's `@Test` methods. */
        val map = mapOf(pair, "b" to 2)

        /** Sample `Map.Entry<String, Int>` input (the first entry of [map]), shared by every subclass's `@Test` methods. */
        val keyValue = map.entries.first()

        /** A value with no registered conversion, used to exercise [assertTermificationFails]. */
        val unconvertible = StringBuilder()
    }

    /** Creates the [Termificator] under test, backed by [scope]. Called once per test, from [setup]. */
    protected abstract fun createTermificator(scope: Scope): Termificator

    @BeforeTest
    fun setup() {
        termificator = createTermificator(Scope.empty())
        scope = termificator.scope
    }

    /**
     * Asserts that converting [input] via [actual] (defaulting to [Termificator.termify]) produces the [Term]
     * built by [expected] out of the same backing [Scope] the [Termificator] under test uses — so both sides
     * share the same [it.unibo.tuprolog.core.Var] instances for equal variable names.
     */
    protected fun <T> assertTermificationWorks(
        input: T,
        expected: Scope.(T) -> Term,
        actual: Termificator.(T) -> Term = Termificator::termify,
    ) = assertEquals(scope.expected(input), termificator.actual(input))

    /** Asserts that converting [input] via [actual] (defaulting to [Termificator.termify]) produces [expected]. */
    protected fun <T> assertTermificationWorks(
        input: T,
        expected: Term,
        actual: Termificator.(T) -> Term = Termificator::termify,
    ) = assertEquals(expected, termificator.actual(input))

    /**
     * Asserts that converting [input] via [actual] (defaulting to [Termificator.termify]) throws
     * [IllegalArgumentException] with a message starting with `"Cannot convert"`, per [Termificator.termify]'s
     * contract for unconvertible values (see [unconvertible]).
     *
     * @throws AssertionError (via [fail]) if the conversion succeeds instead of throwing.
     */
    protected fun <T> assertTermificationFails(
        input: T,
        actual: Termificator.(T) -> Term = Termificator::termify,
    ) = try {
        val result = termificator.actual(input)
        fail("Termifying $input should fail, while it worked producing $result")
    } catch (e: IllegalArgumentException) {
        assertTrue(
            message = "Wrong message for expression: '${e.message}'",
            actual = e.message?.startsWith("Cannot convert") ?: false,
        )
    }
}
