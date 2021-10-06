package it.unibo.tuprolog.solve.concurrent

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestChannelToSequence {

    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)

    @Test
    fun testEmptyChannel() {
        val channel: Channel<String> = Channel()
        val job = scope.launch { channel.close() }
        val emptySeq = channel.toSequence(scope)
        assertTrue(emptySeq.count() == 0)
        scope.launch {
            job.join()
            assertTrue(channel.isClosedForReceive)
            assertTrue(channel.isClosedForSend)
        }
    }

    @Test
    fun testEarlyClose() {
        val channel: Channel<String> = Channel()
        scope.launch {
            channel.close()
            val seq = channel.toSequence(scope)
            assertTrue(seq.count() == 0)
            assertTrue(channel.isClosedForReceive)
            assertTrue(channel.isClosedForSend)
        }
    }

    @Test
    fun testChannelToSequence() {
        val times = 100
        val channel: Channel<String> = Channel(Channel.UNLIMITED)
        scope.launch {
            for (c in 0 until times)
                channel.send("$c")
            channel.close()
        }
        val seq = channel.toSequence(scope)
        seq.forEachIndexed { index, s -> assertEquals("$index", s) }
    }
}
