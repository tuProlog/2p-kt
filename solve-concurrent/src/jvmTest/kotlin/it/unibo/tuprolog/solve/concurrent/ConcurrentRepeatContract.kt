package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yields

fun concurrentRepeatContract() =
    concurrentContract(
        disabled =
            setOf(
                "repeat/repeat", // does not terminate when concurrent
            ),
    ) {
        case("repeat/repeat") { (repeat and (cut and fail)) yields { listOf(it.no()) } }
    }
