@file:OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)

package it.unibo.tuprolog.solve.concurrent

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

class ChannelToSequenceSpec :
    FunSpec({
        test("empty-channel") {
            multiRunConcurrentTest {
                val channel: Channel<String> = Channel()
                val emptySeq = channel.toSequence(this)
                channel.close().shouldBeTrue()
                emptySeq.none().shouldBeTrue()
                channel.isClosedForReceive.shouldBeTrue()
                channel.isClosedForSend.shouldBeTrue()
            }
        }

        test("early-close") {
            multiRunConcurrentTest {
                val channel: Channel<String> = Channel()
                channel.close()
                val seq = channel.toSequence(this)
                seq.count() shouldBe 0
                channel.isClosedForReceive.shouldBeTrue()
                channel.isClosedForSend.shouldBeTrue()
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
                set shouldBe setOf("a", "b", "c")
                channel.isClosedForReceive.shouldBeTrue()
                channel.isClosedForSend.shouldBeTrue()
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
                seq.forEachIndexed { index, s -> s shouldBe "$index" }
            }
        }
    })
