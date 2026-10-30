@file:OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)

package it.unibo.tuprolog.solve.concurrent

import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ChannelToSequenceSpec :
    FunSpec({
        test("empty-channel") {
            multiRunConcurrentTest {
                val channel: Channel<String> = Channel()
                val emptySeq = channel.toSequence(this)
                assertTrue(channel.close())
                assertTrue(emptySeq.none())
                assertTrue(channel.isClosedForReceive)
                assertTrue(channel.isClosedForSend)
            }
        }

        test("early-close") {
            multiRunConcurrentTest {
                val channel: Channel<String> = Channel()
                channel.close()
                val seq = channel.toSequence(this)
                assertTrue(seq.count() == 0)
                assertTrue(channel.isClosedForReceive)
                assertTrue(channel.isClosedForSend)
            }
        }

        test("concurrent-send") {
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
        }

        test("channel-to-sequence-works") {
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
    })
