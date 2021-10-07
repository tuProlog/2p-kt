package it.unibo.tuprolog.solve.concurrent

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestChannelToSequence {

//    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testEmptyChannel() = runBlockingTest {
        val channel: Channel<String> = Channel()
        val emptySeq = channel.toSequence(this)
        assertTrue(channel.close())
        assertTrue(emptySeq.none())
        assertTrue(channel.isClosedForReceive)
        assertTrue(channel.isClosedForSend)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testEarlyClose() = runBlockingTest {
        val channel: Channel<String> = Channel()
        channel.close()
        val seq = channel.toSequence(this)
        assertTrue(seq.count() == 0)
        assertTrue(channel.isClosedForReceive)
        assertTrue(channel.isClosedForSend)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testChannelToSequence() = runBlockingTest {
        val times = 100
        val channel: Channel<String> = Channel(Channel.UNLIMITED)
        launch {
            for (c in 0 until times)
                channel.send("$c")
            channel.close()
        }
        val seq = channel.toSequence(this)
        seq.forEachIndexed { index, s -> assertEquals("$index", s) }
    }
}
