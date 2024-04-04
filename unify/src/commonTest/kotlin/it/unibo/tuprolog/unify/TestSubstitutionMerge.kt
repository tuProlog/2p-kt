package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.unify.Unificator.Companion.mergeWith
import kotlin.test.Test
import kotlin.test.assertEquals

class TestSubstitutionMerge {
    @Test
    fun substitutionMergeNeverFails() {
        Scope.empty {
            val base = unifierOf(varOf("X") to varOf("Y"), varOf("Y") to varOf("Z"))
            val a = atomOf("a")

            for (v in arrayOf("X", "Y", "Z").map { varOf(it) }) {
                val assignment = unifierOf(v to a)
                assertEquals(unifierOf(varOf("X") to a, varOf("Y") to a, varOf("Z") to a), base.mergeWith(assignment))
                assertEquals(unifierOf(varOf("X") to a, varOf("Y") to a, varOf("Z") to a), assignment.mergeWith(base))
            }
        }
    }
}
