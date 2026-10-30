package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes
import it.unibo.tuprolog.solve.yields

fun concurrentNotUnifyContract() =
    concurrentContract {
        case("not-unify/number-not-unify") { 1 notEqualsTo 1 yields { listOf(it.no()) } }
        case("not-unify/number-x-not-unify") { "X" notEqualsTo 1 yields { listOf(it.no()) } }
        case("not-unify/xy-not-unify") { "X" notEqualsTo "Y" yields { listOf(it.no()) } }

        case("not-unify/double-not-unify") {
            (("X" notEqualsTo "Y") and ("X" notEqualsTo "abc")) yields {
                listOf(it.no())
            }
        }

        case("not-unify/f-def-not-unify") { "f"("X", "def") notEqualsTo ("f"("def", "Y")) yields { listOf(it.no()) } }
        case("not-unify/diff-number-not-unify") { 1 notEqualsTo 2 yields { listOf(it.yes()) } }
        case("not-unify/dec-number-not-unify") { intOf(1) notEqualsTo realOf(1.0) yields { listOf(it.yes()) } }
        case("not-unify/g-not-unify-fx") { ("g"("X")) notEqualsTo ("f"("a"("X"))) yields { listOf(it.yes()) } }
        case("not-unify/f-not-unify") { ("f"("X", 1)) notEqualsTo ("f"("a"("X"))) yields { listOf(it.yes()) } }

        case("not-unify/f-multiple-term-not-unify") {
            ("f"("X", "Y", "X")) notEqualsTo ("f"("a"("X"), "a"("Y"), "Y", 2)) yields {
                listOf(it.yes())
            }
        }
    }
