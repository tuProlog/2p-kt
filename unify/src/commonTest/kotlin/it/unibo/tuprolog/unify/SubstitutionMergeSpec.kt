package it.unibo.tuprolog.unify

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.unify.Unificator.Companion.mergeWith
import kotlin.test.assertEquals

class SubstitutionMergeSpec :
    FunSpec({
        test("substitution-merge-never-fails") {
            Scope.empty {
                val base = unifierOf("X" to varOf("Y"), "Y" to varOf("Z"))
                val a = atomOf("a")

                for (v in arrayOf("X", "Y", "Z")) {
                    val assignment = unifierOf(v to a)
                    assertEquals(unifierOf("X" to a, "Y" to a, "Z" to a), base.mergeWith(assignment))
                    assertEquals(unifierOf("X" to a, "Y" to a, "Z" to a), assignment.mergeWith(base))
                }
            }
        }
    })
