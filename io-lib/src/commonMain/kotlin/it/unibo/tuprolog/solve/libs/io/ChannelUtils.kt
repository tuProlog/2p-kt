@file:JvmName("ChannelUtils")

package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.channel.InputChannel
import kotlin.jvm.JvmName

expect val KEY: String

expect fun InputChannel<String>.asTermChannel(operators: OperatorSet): InputChannel<Term>
