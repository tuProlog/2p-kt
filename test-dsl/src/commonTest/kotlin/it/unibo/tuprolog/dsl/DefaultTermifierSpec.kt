package it.unibo.tuprolog.dsl

import io.kotest.core.spec.style.FunSpec

class DefaultTermifierSpec :
    FunSpec({
        include(
            legacyTermifierTests(
                Termificator::default,
                replaced = setOf("sets", "maps", "pairs", "triples", "key-values"),
            ),
        )

        with(TermificatorTester(Termificator::default)) {
            beforeTest { setup() }

            test("sets") {
                val input = setOf(1, 2)
                assertTermificationWorks(input, { input.map { intOf(it) }.let(::blockOf) })
            }

            test("maps") {
                assertTermificationWorks(
                    input = map,
                    expected = { map ->
                        map.entries.map { structOf(":", atomOf(it.key), intOf(it.value)) }.let(::blockOf)
                    },
                )
            }

            test("pairs") {
                assertTermificationWorks(pair, { tupleOf(atomOf(it.first), intOf(it.second)) })
            }

            test("triples") {
                assertTermificationWorks(triple, { tupleOf(atomOf(it.first), intOf(it.second), truthOf(it.third)) })
            }

            test("key-values") {
                assertTermificationWorks(keyValue, { structOf(":", atomOf(it.key), intOf(it.value)) })
            }
        }
    })
