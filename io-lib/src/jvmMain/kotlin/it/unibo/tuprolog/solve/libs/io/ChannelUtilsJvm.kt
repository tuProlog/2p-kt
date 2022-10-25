package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.parsing.TermReader
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.ReaderChannel

@Synchronized
actual fun InputChannel<String>.asTermChannel(operators: OperatorSet): InputChannel<Term> {
    if (this !is ReaderChannel) {
        throw IllegalStateException("Channel $streamTerm does not supporting reading terms, as it is of type ${this::class.simpleName}")
    }
    return if (containsExtension(KEY)) {
        getExtension(KEY)!!
    } else {
        val termIterator = TermReader.withOperators(operators).readTerms(reader).iterator()
        val termChannel = InputChannel.of(termIterator::next, termIterator::hasNext)
        setExtension(KEY, termChannel)
        termChannel
    }
}
