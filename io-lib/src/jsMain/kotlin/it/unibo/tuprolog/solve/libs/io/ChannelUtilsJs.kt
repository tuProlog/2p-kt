package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.utils.io.exceptions.IOException

actual fun InputChannel<String>.asTermChannel(operators: OperatorSet): InputChannel<Term> {
    throw IOException("Reading terms is still not supported for JS")
}
