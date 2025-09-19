package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith
import kotlin.test.Test
import kotlin.test.assertEquals

class ListUnificationTest {
    @Test
    fun testListUnificationInMember() {
        val scope = Scope.empty()
        val actual =
            scope.with {
                structOf("member", atomOf("a"), logicListOf(arrayOf("a", "b", "c").map { atomOf(it) }))
            }
        val pattern =
            scope.with {
                structOf("member", varOf("H"), consOf(varOf("H"), varOf("T")))
            }
        val mgu1 = actual mguWith pattern
        val mgu2 = pattern mguWith actual
        assertEquals(mgu1, mgu2)
        scope.with {
            assertEquals(
                Substitution.unifier(
                    varOf("H") to atomOf("a"),
                    varOf("T") to logicListOf(arrayOf("b", "c").map { atomOf(it) }),
                ),
                mgu1,
            )
        }
    }

    @Test
    fun testListUnification() {
        val scope = Scope.empty()
        val actual = scope.with { logicListOf(arrayOf("a", "b", "c").map { atomOf(it) }) }
        val pattern = scope.with { consOf(varOf("H"), varOf("T")) }
        val mgu1 = actual mguWith pattern
        val mgu2 = pattern mguWith actual
        assertEquals(mgu1, mgu2)
        scope.with {
            assertEquals(
                Substitution.unifier(
                    varOf("H") to atomOf("a"),
                    varOf("T") to logicListOf(arrayOf("b", "c").map { atomOf(it) }),
                ),
                mgu1,
            )
        }
    }
}
