@file:JvmName("ChannelUtils")

package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.parsing.PrologTermParserVisitor
import it.unibo.tuprolog.core.parsing.toOperatorTable
import it.unibo.tuprolog.core.parsing.toParseException
import it.unibo.tuprolog.parser.TextChunkSource
import it.unibo.tuprolog.parser.buildParserFor
import it.unibo.tuprolog.parser.exceptions.PrologSyntaxException
import it.unibo.tuprolog.solve.channel.InputChannel
import kotlin.jvm.JvmName

// WARN: plain map, not weak — channels opened per Prolog session are few; a long-lived process
// that opens and abandons (without closing) many streams would leak entries here.
// TODO: consider using a weak map, or add eviction policy
private val cache = mutableMapOf<InputChannel<String>, InputChannel<Term>>()

/**
 * Wraps this channel of raw characters into a channel of [Term]s, parsed lazily off it using
 * [operators]. Repeated calls on the same channel return the same wrapped channel, so parsing
 * resumes where the previous call left off.
 */
fun InputChannel<String>.asTermChannel(operators: OperatorSet): InputChannel<Term> =
    cache.getOrPut(this) {
        val termIterator =
            buildParserFor(source = TextChunkSource { read() }) { parser, lexedSource ->
                val session = parser.openSession(lexedSource, operators.toOperatorTable())
                val visitor = PrologTermParserVisitor(Scope.empty())
                sequence {
                    try {
                        var term = session.parseNextTerm()
                        while (term != null) {
                            yield(term.root.accept(visitor))
                            term = session.parseNextTerm()
                        }
                    } catch (e: PrologSyntaxException) {
                        val input = lexedSource.source.let { it.id ?: it.text() }
                        throw e.toParseException(input)
                    }
                }
            }.iterator()
        InputChannel.of({ if (termIterator.hasNext()) termIterator.next() else null }, termIterator::hasNext)
    }
