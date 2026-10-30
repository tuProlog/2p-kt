package it.unibo.tuprolog.solve.libs.io

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.parsing.ParseException
import it.unibo.tuprolog.solve.channel.InputChannel
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AsTermChannelSpec :
    FunSpec({
        fun channelOf(text: String) = InputChannel.of(text)

        test("reads-successive-terms-lazily") {
            val termChannel = channelOf("foo(1, 2). bar(x).").asTermChannel(OperatorSet.DEFAULT)
            assertTrue(termChannel.available)
            assertEquals("foo(1,2)", (termChannel.read() as Struct).toString().replace(" ", ""))
            assertEquals("bar(x)", (termChannel.read() as Struct).toString())
            assertFalse(termChannel.available)
            assertNull(termChannel.read())
        }

        test("caches-the-wrapping-channel-so-repeated-calls-resume") {
            val channel = channelOf("first. second.")
            val termChannel = channel.asTermChannel(OperatorSet.DEFAULT)
            assertEquals("first", termChannel.read().toString())
            // asking again for the wrapper on the same channel must not restart parsing from the top
            val sameTermChannel = channel.asTermChannel(OperatorSet.DEFAULT)
            assertEquals("second", sameTermChannel.read().toString())
            assertNull(sameTermChannel.read())
        }

        test("wraps-syntax-errors-as-parse-exception") {
            val termChannel = channelOf(").").asTermChannel(OperatorSet.DEFAULT)
            assertFailsWith<ParseException> { termChannel.read() }
        }
    })
