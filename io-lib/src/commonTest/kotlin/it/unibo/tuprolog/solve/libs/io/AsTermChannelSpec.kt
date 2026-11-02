package it.unibo.tuprolog.solve.libs.io

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.parsing.ParseException
import it.unibo.tuprolog.solve.channel.InputChannel

class AsTermChannelSpec :
    FunSpec({
        fun channelOf(text: String) = InputChannel.of(text)

        test("reads-successive-terms-lazily") {
            val termChannel = channelOf("foo(1, 2). bar(x).").asTermChannel(OperatorSet.DEFAULT)
            termChannel.available.shouldBeTrue()
            (termChannel.read() as Struct).toString().replace(" ", "") shouldBe "foo(1,2)"
            (termChannel.read() as Struct).toString() shouldBe "bar(x)"
            termChannel.available.shouldBeFalse()
            termChannel.read().shouldBeNull()
        }

        test("caches-the-wrapping-channel-so-repeated-calls-resume") {
            val channel = channelOf("first. second.")
            val termChannel = channel.asTermChannel(OperatorSet.DEFAULT)
            termChannel.read().toString() shouldBe "first"
            // asking again for the wrapper on the same channel must not restart parsing from the top
            val sameTermChannel = channel.asTermChannel(OperatorSet.DEFAULT)
            sameTermChannel.read().toString() shouldBe "second"
            sameTermChannel.read().shouldBeNull()
        }

        test("wraps-syntax-errors-as-parse-exception") {
            val termChannel = channelOf(").").asTermChannel(OperatorSet.DEFAULT)
            shouldThrow<ParseException> { termChannel.read() }
        }
    })
