package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.parsing.TermParser
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.impl.InputChannelFromString
import kotlin.jvm.Synchronized

@Synchronized
actual fun InputChannel<String>.asTermChannel(operators: OperatorSet): InputChannel<Term> {
    if (this !is InputChannelFromString) {
        throw IllegalStateException("Channel $streamTerm does not supporting reading terms, as it is of type ${this::class.simpleName}")
    }
    return if (containsExtension(KEY)) {
        getExtension(KEY)!!
    } else {
        val termIterator = TermParser.withOperators(operators).parseTerms(string).iterator()
        val termChannel = InputChannel.of(termIterator::next, termIterator::hasNext)
        setExtension(KEY, termChannel)
        termChannel
    }
}
