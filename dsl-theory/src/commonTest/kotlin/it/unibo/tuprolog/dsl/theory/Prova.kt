package it.unibo.tuprolog.dsl.theory

import kotlin.test.Test

class Prova {
    @Test
    fun testProlog() {
        logicProgramming {
            println(
                theoryOf(
                    fact { "member"("X", consOf("X", `_`)) },
                    rule { "member"("X", consOf(`_`, "T")) impliedBy "member"("X", "T") },
                ),
            )
        }
    }
}
