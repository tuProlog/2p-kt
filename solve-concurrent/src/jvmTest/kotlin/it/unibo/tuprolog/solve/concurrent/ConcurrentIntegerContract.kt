package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes
import it.unibo.tuprolog.solve.yields

fun concurrentIntegerContract() =
    concurrentContract {
        case("integer/int-positive-num") { integer(3) yields { listOf(it.yes()) } }
        case("integer/int-negative-num") { integer(intOf(-3)) yields { listOf(it.yes()) } }
        case("integer/int-dec-num") { integer(3.3) yields { listOf(it.no()) } }
        case("integer/int-x") { integer("X") yields { listOf(it.no()) } }
        case("integer/int-atom") { integer("atom") yields { listOf(it.no()) } }
    }
