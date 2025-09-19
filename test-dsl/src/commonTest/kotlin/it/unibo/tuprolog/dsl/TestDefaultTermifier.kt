package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Scope
import kotlin.test.Test

class TestDefaultTermifier : TestLegacyTermifier() {
    override fun createTermificator(scope: Scope): Termificator = Termificator.default(scope)

    @Test
    override fun testSets() {
        val input = setOf(1, 2)
        assertTermificationWorks(input, { input.map { intOf(it) }.let(::blockOf) })
    }

    override fun testMaps() {
        assertTermificationWorks(
            input = map,
            expected = { map ->
                map.entries.map { structOf(":", atomOf(it.key), intOf(it.value)) }.let(::blockOf)
            },
        )
    }

    override fun testPairs() {
        assertTermificationWorks(pair, { tupleOf(atomOf(it.first), intOf(it.second)) })
    }

    override fun testTriples() {
        assertTermificationWorks(triple, { tupleOf(atomOf(it.first), intOf(it.second), truthOf(it.third)) })
    }

    override fun testKeyValues() {
        assertTermificationWorks(keyValue, { structOf(":", atomOf(it.key), intOf(it.value)) })
    }
}
