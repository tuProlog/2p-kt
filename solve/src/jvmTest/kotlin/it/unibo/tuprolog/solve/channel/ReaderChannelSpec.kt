package it.unibo.tuprolog.solve.channel

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.io.StringReader

class ReaderChannelSpec :
    FunSpec({
        test("reads-one-character-at-a-time") {
            val channel = ReaderChannel(StringReader("ab"))
            listOf(channel.read(), channel.read(), channel.read()) shouldBe listOf("a", "b", null)
        }

        test("reads-an-astral-character-whole") {
            val channel = ReaderChannel(StringReader("a😀b"))
            listOf(channel.read(), channel.read(), channel.read(), channel.read()) shouldBe
                listOf("a", "😀", "b", null)
        }
    })
