package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes
import it.unibo.tuprolog.solve.yields

fun concurrentAtomCodesContract() =
    concurrentContract {
        case("atom-codes/atom-codes-second-is-var1") {
            atom_codes("abc", "X") yields {
                listOf(it.yes("X" to logicListOf(97, 98, 99)))
            }
        }

        case("atom-codes/atom-codes-second-is-var") {
            atom_codes("test", "X") yields {
                listOf(it.yes("X" to logicListOf(116, 101, 115, 116)))
            }
        }

        case("atom-codes/atom-codes-first-is-var") {
            atom_codes("X", logicListOf(97, 98, 99)) yields {
                listOf(it.yes("X" to "abc"))
            }
        }

        case("atom-codes/atom-codes-no-var") {
            atom_codes("test", logicListOf(116, 101, 115, 116)) yields {
                listOf(it.yes())
            }
        }

        case("atom-codes/atom-codes-fail") {
            atom_codes("test", logicListOf(112, 101, 115, 116)) yields {
                listOf(it.no())
            }
        }
    }
