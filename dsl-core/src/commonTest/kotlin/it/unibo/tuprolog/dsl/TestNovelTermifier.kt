package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Scope
import kotlin.test.Test

class TestNovelTermifier : TestDefaultTermifier() {
    override fun createTermificator(scope: Scope): Termificator = Termificator.novel(scope)

    @Test
    override fun testSets() {
        val input = setOf(1, 2)
        assertTermificationWorks(input, { input.map { intOf(it) }.let(::blockOf) })
    }
}
