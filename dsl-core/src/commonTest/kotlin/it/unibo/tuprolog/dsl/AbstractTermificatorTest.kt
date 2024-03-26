package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

abstract class AbstractTermificatorTest {
    private lateinit var scope: Scope
    private lateinit var termificator: Termificator

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
}
