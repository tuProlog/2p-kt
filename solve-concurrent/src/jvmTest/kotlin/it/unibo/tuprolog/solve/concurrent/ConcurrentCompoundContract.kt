package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes
import it.unibo.tuprolog.solve.yields

fun concurrentCompoundContract() =
    concurrentContract {
        case("compound/compound-dec") { compound(33.3) yields { listOf(it.no()) } }
        case("compound/compound-neg-dec") { compound(-33.3) yields { listOf(it.no()) } }
        case("compound/compound-neg-a") { compound("-"("a")) yields { listOf(it.yes()) } }
        case("compound/compound-any") { compound(`_`) yields { listOf(it.no()) } }
        case("compound/compound-a") { compound("a") yields { listOf(it.no()) } }
        case("compound/compound-a-of-b") { compound("a"("b")) yields { listOf(it.yes()) } }
        case("compound/compound-list-a") { compound(logicListOf("a")) yields { listOf(it.yes()) } }
    }
