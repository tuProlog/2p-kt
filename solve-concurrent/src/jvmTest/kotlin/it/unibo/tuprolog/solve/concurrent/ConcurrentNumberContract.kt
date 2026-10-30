package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes
import it.unibo.tuprolog.solve.yields

fun concurrentNumberContract() =
    concurrentContract {
        case("number/basic-num") { number(3) yields { listOf(it.yes()) } }
        case("number/dec-num") { number(3.3) yields { listOf(it.yes()) } }
        case("number/neg-num") { number(-3) yields { listOf(it.yes()) } }
        case("number/letter-num") { number("a") yields { listOf(it.no()) } }
        case("number/x-num") { number("X") yields { listOf(it.no()) } }
    }
