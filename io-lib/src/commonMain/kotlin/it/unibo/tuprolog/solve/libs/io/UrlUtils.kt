@file:JvmName("UrlUtils")

package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.utils.io.Url
import kotlin.jvm.JvmName

expect fun Url.openInputChannel(): InputChannel<String>

expect fun Url.openOutputChannel(append: Boolean = false): OutputChannel<String>
