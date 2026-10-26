package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes
import it.unibo.tuprolog.solve.yields

fun concurrentAtomContract() =
    concurrentContract {
        case("atom/atom-atom") { atom("atom") yields { listOf(it.yes()) } }
        case("atom/atom-string") { atom("string") yields { listOf(it.yes()) } }
        case("atom/atom-aof-b") { atom("a"("b")) yields { listOf(it.no()) } }
        case("atom/atom-var") { atom("Var") yields { listOf(it.no()) } }
        case("atom/atom-empty-list") { atom(emptyLogicList) yields { listOf(it.yes()) } }
        case("atom/atom-num") { atom(6) yields { listOf(it.no()) } }
        case("atom/atom-num-dec") { atom(3.3) yields { listOf(it.no()) } }
    }
