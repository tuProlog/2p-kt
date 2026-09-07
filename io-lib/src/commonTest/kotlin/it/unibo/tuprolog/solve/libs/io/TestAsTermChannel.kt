package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.parsing.ParseException
import it.unibo.tuprolog.solve.channel.InputChannel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TestAsTermChannel {
    private fun channelOf(text: String) = InputChannel.of(text)

    @Test
    fun readsSuccessiveTermsLazily() {
        val termChannel = channelOf("foo(1, 2). bar(x).").asTermChannel(OperatorSet.DEFAULT)
        assertTrue(termChannel.available)
        assertEquals("foo(1,2)", (termChannel.read() as Struct).toString().replace(" ", ""))
        assertEquals("bar(x)", (termChannel.read() as Struct).toString())
        assertFalse(termChannel.available)
        assertNull(termChannel.read())
    }

    @Test
    fun cachesTheWrappingChannelSoRepeatedCallsResume() {
        val channel = channelOf("first. second.")
        val termChannel = channel.asTermChannel(OperatorSet.DEFAULT)
        assertEquals("first", termChannel.read().toString())
        // asking again for the wrapper on the same channel must not restart parsing from the top
        val sameTermChannel = channel.asTermChannel(OperatorSet.DEFAULT)
        assertEquals("second", sameTermChannel.read().toString())
        assertNull(sameTermChannel.read())
    }

    @Test
    fun wrapsSyntaxErrorsAsParseException() {
        val termChannel = channelOf(").").asTermChannel(OperatorSet.DEFAULT)
        assertFailsWith<ParseException> { termChannel.read() }
    }
}
