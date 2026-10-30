package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.yes
import it.unibo.tuprolog.solve.yields

fun concurrentTrueContract() =
    concurrentContract {
        case("true/true") { atomOf("true") yields { listOf(it.yes()) } }
    }
