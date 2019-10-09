package it.unibo.tuprolog.dsl.theory

import kotlin.test.Test

class Prova {

    @Test
    fun testProlog() {
        prolog {
            println("f"("X") mguWith structOf("f", 1))
        }
    }
}