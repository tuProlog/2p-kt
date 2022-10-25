@file:JvmName("ChannelUtils")

package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.currentPlatform
import it.unibo.tuprolog.solve.channel.InputChannel
import kotlin.jvm.JvmName

internal val KEY by lazy {
    if (currentPlatform().isJavaScript) {
        "it.unibo.tuprolog.solve.libs.io."
    } else {
        IOLib::class.let { it.qualifiedName!!.replace(it.simpleName!!, "") }
    } + "term-reader"
}

expect fun InputChannel<String>.asTermChannel(operators: OperatorSet): InputChannel<Term>
