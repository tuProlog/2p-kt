package it.unibo.tuprolog.utils

import io.kotest.core.spec.style.FunSpec
import kotlin.test.assertEquals
import kotlin.test.assertSame

class OptionalSpec :
    FunSpec({
        test("some/filter-keeps-or-drops-the-value") {
            assertEquals(Optional.some(2), Optional.some(2).filter { it > 1 })
            assertSame(Optional.None, Optional.some(2).filter { it > 5 })
        }

        test("some/as-a-sequence-and-a-string") {
            assertEquals(listOf(2), Optional.some(2).toSequence().toList())
            assertEquals("Some(2)", Optional.some(2).toString())
        }

        test("none/stays-none") {
            val none = Optional.none<Int>()
            assertSame(Optional.None, none.filter { true })
            assertEquals(emptyList(), none.toSequence().toList())
            assertEquals("None", none.toString())
        }

        test("of/a-null-is-none") {
            assertSame(Optional.None, Optional.of<Int>(null))
            assertEquals(Optional.some(1), Optional.of(1))
        }
    })
