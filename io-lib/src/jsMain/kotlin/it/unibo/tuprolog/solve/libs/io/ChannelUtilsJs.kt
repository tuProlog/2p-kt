package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.channel.InputChannel

actual fun InputChannel<String>.asTermChannel(operators: OperatorSet): InputChannel<Term> {
    TODO("reading terms is still not supported for JS")
}
