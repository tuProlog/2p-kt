@file:JvmName("ChannelUtils")

package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.channel.InputChannel
import kotlin.jvm.JvmName

/**
 * Wraps this character-level [InputChannel] into an [InputChannel] of parsed [Term]s, so that `read/1,2` and
 * `read_term/2,3` (see `IOPrimitiveUtils.readTermAndReply`) can pull whole terms out of it instead of characters.
 *
 * Parsing uses [operators] to resolve operator notation, exactly as `it.unibo.tuprolog.theory.parsing.ClausesParser`
 * and `it.unibo.tuprolog.core.parsing.TermParser` do elsewhere in 2P-Kt. Repeated calls on the same receiver return
 * the same term channel (platform-dependent caching), so that reading progresses across calls instead of restarting.
 *
 * @throws IllegalStateException on the JVM, if this channel does not support character-based reading (i.e. is not
 * backed by a `Reader`). On JS, this operation always throws [NotImplementedError], as reading terms is not yet
 * supported there (see the JS `Read1`/`Read2`/`ReadTerm2`/`ReadTerm3` primitives).
 */
expect fun InputChannel<String>.asTermChannel(operators: OperatorSet): InputChannel<Term>
