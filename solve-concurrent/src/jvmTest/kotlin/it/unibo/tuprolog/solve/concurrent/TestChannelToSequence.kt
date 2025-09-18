package it.unibo.tuprolog.solve.concurrent

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestChannelToSequence {
    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    @Test
    fun testEmptyChannel() =
        multiRunConcurrentTest {
            val channel: Channel<String> = Channel()
            val emptySeq = channel.toSequence(this)
            assertTrue(channel.close())
            assertTrue(emptySeq.none())
            assertTrue(channel.isClosedForReceive)
            assertTrue(channel.isClosedForSend)
        }

    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    @Test
    fun testEarlyClose() =
        multiRunConcurrentTest {
            val channel: Channel<String> = Channel()
            channel.close()
            val seq = channel.toSequence(this)
            assertTrue(seq.count() == 0)
            assertTrue(channel.isClosedForReceive)
            assertTrue(channel.isClosedForSend)
        }

    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    @Test
    fun testConcurrentSend() =
        multiRunConcurrentTest {
            val channel: Channel<String> = Channel()
            launch {
                channel.send("a")
                channel.send("b")
                channel.send("c")
                channel.close()
            }
            val set = channel.toSequence(this).toSet()
            assertEquals(setOf("a", "b", "c"), set)
            assertTrue(channel.isClosedForReceive)
            assertTrue(channel.isClosedForSend)
        }

    @Test
    fun testChannelToSequenceWorks() =
        multiRunConcurrentTest {
            val times = 100
            val channel: Channel<String> = Channel(Channel.UNLIMITED)
            launch {
                for (c in 0 until times) {
                    channel.send("$c")
                }
                channel.close()
            }
            val seq = channel.toSequence(this)
            seq.forEachIndexed { index, s -> assertEquals("$index", s) }
        }
}
