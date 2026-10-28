package it.unibo.tuprolog.unify

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.unify.Unificator.Companion.mergeWith
import kotlin.test.assertEquals

class SubstitutionMergeSpec :
    FunSpec({
        withTests(nameFn = { "substitution-merge-never-fails/bind-${it.lowercase()}" }, listOf("X", "Y", "Z")) { v ->
            Scope.empty {
                val base = unifierOf("X" to varOf("Y"), "Y" to varOf("Z"))
                val a = atomOf("a")
                val assignment = unifierOf(v to a)
                assertEquals(unifierOf("X" to a, "Y" to a, "Z" to a), base.mergeWith(assignment))
                assertEquals(unifierOf("X" to a, "Y" to a, "Z" to a), assignment.mergeWith(base))
            }
        }
    })
