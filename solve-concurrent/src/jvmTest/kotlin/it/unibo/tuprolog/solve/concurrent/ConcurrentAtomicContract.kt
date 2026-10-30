package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes
import it.unibo.tuprolog.solve.yields

fun concurrentAtomicContract() =
    concurrentContract {
        case("atomic/atomic-atom") { atomic("atom") yields { listOf(it.yes()) } }
        case("atomic/atomic-aof-b") { atomic("a"("b")) yields { listOf(it.no()) } }
        case("atomic/atomic-var") { atomic("Var") yields { listOf(it.no()) } }
        case("atomic/atomic-empty-list") { atomic(emptyLogicList) yields { listOf(it.yes()) } }
        case("atomic/atomic-num") { atomic(6) yields { listOf(it.yes()) } }
        case("atomic/atomic-num-dec") { atomic(3.3) yields { listOf(it.yes()) } }
    }
