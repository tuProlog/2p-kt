package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes
import it.unibo.tuprolog.solve.yields

fun concurrentAtomConcatContract() =
    concurrentContract {
        case("atom-concat/atom-concat-third-is-var") {
            atom_concat("test", "concat", "X") yields {
                listOf(it.yes("X" to atomOf("testconcat")))
            }
        }

        case("atom-concat/atom-concat-fails") { atom_concat("test", "concat", "test") yields { listOf(it.no()) } }

        case("atom-concat/atom-concat-second-is-var") {
            atom_concat("test", "X", "testTest") yields {
                listOf(it.yes("X" to atomOf("Test")))
            }
        }

        case("atom-concat/atom-concat-first-is-var") {
            atom_concat("X", "query", "testquery") yields {
                listOf(it.yes("X" to atomOf("test")))
            }
        }
    }
