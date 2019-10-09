package it.unibo.tuprolog.dsl

import kotlin.test.Test

class Prova {

    @Test
    fun testProlog() {
        println(prolog {
            "member"("X", consOf("X", `_`)) `if` true
        })

        println(prolog {
            rule { "member"("X", consOf(`_`, "T")) `if` "member"("X", "T") }
        })
    }
}