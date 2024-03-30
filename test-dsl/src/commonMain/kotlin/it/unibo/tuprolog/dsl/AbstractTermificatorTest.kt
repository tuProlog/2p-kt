package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

abstract class AbstractTermificatorTest {
    private lateinit var scope: Scope
    private lateinit var termificator: Termificator

    companion object {
        val array = arrayOf(1, 2)
        val list = array.toList()
        val set = array.toSet()
        val sequence = sequenceOf(*array)
        val pair = "a" to 1
        val triple = Triple("a", 1, true)
        val map = mapOf(pair, "b" to 2)
        val keyValue = map.entries.first()
        val unconvertible = StringBuilder()
    }

    protected abstract fun createTermificator(scope: Scope): Termificator

    @BeforeTest
    fun setup() {
        termificator = createTermificator(Scope.empty())
        scope = termificator.scope
    }

    protected fun <T> assertTermificationWorks(
        input: T,
        expected: Scope.(T) -> Term,
        actual: Termificator.(T) -> Term = Termificator::termify,
    ) = assertEquals(scope.expected(input), termificator.actual(input))

    protected fun <T> assertTermificationWorks(
        input: T,
        expected: Term,
        actual: Termificator.(T) -> Term = Termificator::termify,
    ) = assertEquals(expected, termificator.actual(input))

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
