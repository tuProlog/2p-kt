package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes
import it.unibo.tuprolog.solve.yields

fun concurrentFloatContract() =
    concurrentContract {
        case("float/float-dec") { float(3.3) yields { listOf(it.yes()) } }
        case("float/float-dec-neg") { float(-3.3) yields { listOf(it.yes()) } }
        case("float/float-nat") { float(3) yields { listOf(it.no()) } }
        case("float/float-atom") { float("atom") yields { listOf(it.no()) } }
        case("float/float-x") { float("X") yields { listOf(it.no()) } }
    }
