package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.parsing.TermReader
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.ReaderChannel
import java.util.WeakHashMap

private val cache = WeakHashMap<InputChannel<String>, InputChannel<Term>>()

/**
 * JVM implementation of [it.unibo.tuprolog.solve.libs.io.asTermChannel]: requires the receiver to be a
 * [ReaderChannel] (i.e. backed by a `java.io.Reader`) and parses terms out of it, one at a time, via
 * [it.unibo.tuprolog.core.parsing.TermReader]. The resulting term channel is cached (weakly, keyed by the receiver),
 * so repeated calls on the same character channel keep reading forward rather than restarting from its beginning.
 *
 * @throws IllegalStateException if the receiver is not a [ReaderChannel].
 */
@Suppress("UnsafeCallOnNullableType")
@Synchronized
actual fun InputChannel<String>.asTermChannel(operators: OperatorSet): InputChannel<Term> {
    if (this !is ReaderChannel) {
        error("Channel $streamTerm does not supporting reading terms, as it is of type ${this::class.simpleName}")
    }
    return if (cache.containsKey(this)) {
        cache[this]!!
    } else {
        val termIterator = TermReader.withOperators(operators).readTerms(reader).iterator()
        val termChannel = InputChannel.of(termIterator::next, termIterator::hasNext)
        cache[this] = termChannel
        termChannel
    }
}
