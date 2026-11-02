package it.unibo.tuprolog.solve.channel

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import it.unibo.tuprolog.solve.channel.OutputStore.Companion.STDERR
import it.unibo.tuprolog.solve.channel.OutputStore.Companion.STDOUT

private class Fixture {
    val out = OutputChannel.of<String> {}
    val err = OutputChannel.of<String> {}
    val log = OutputChannel.of<String> {}
    val trace = OutputChannel.of<String> {}
    val store = OutputStore.of(mapOf(STDOUT to out, STDERR to err))
}

/** One `plus` overload, adding `log` (as "log") and `trace` (as "trace"). */
private class PlusCase(
    val id: String,
    val plus: Fixture.() -> OutputStore,
)

private val plusCases =
    listOf(
        PlusCase("map") { store + mapOf("log" to log, "trace" to trace) },
        PlusCase("iterable") { store + listOf("log" to log, "trace" to trace) },
        PlusCase("sequence") { store + sequenceOf("log" to log, "trace" to trace) },
        PlusCase("vararg") { store.plus("log" to log, "trace" to trace) },
        PlusCase("pairs") { store + ("log" to log) + ("trace" to trace) },
    )

/** One `minus` overload, removing "log" and "trace" from a store that has them. */
private class MinusCase(
    val id: String,
    val minus: OutputStore.() -> OutputStore,
)

private val minusCases =
    listOf(
        MinusCase("iterable") { this - listOf("log", "trace") },
        MinusCase("sequence") { this - sequenceOf("log", "trace") },
        MinusCase("vararg") { minus("log", "trace") },
        MinusCase("single") { this - "log" - "trace" },
    )

class ChannelStoreSpec :
    FunSpec({
        test("current/defaults-to-stdout") {
            with(Fixture()) {
                store.current shouldBeSameInstanceAs out
                store.currentAliases.toList() shouldBe listOf(STDOUT)
            }
        }

        test("current/can-be-set-by-alias-or-channel") {
            with(Fixture()) {
                store.setCurrent(STDERR).current shouldBeSameInstanceAs err
                store.setCurrent(err).current shouldBeSameInstanceAs err
                store.setCurrent("no-such-alias").current shouldBeSameInstanceAs out
            }
        }

        withTests(nameFn = { "plus/${it.id}" }, plusCases) { case ->
            with(Fixture()) {
                val extended = case.plus(this)
                extended["log"] shouldBeSameInstanceAs log
                extended["trace"] shouldBeSameInstanceAs trace
                extended.stdOut shouldBeSameInstanceAs out
                extended.aliasesOf(log).toList() shouldBe listOf("log")
            }
        }

        withTests(nameFn = { "minus/${it.id}" }, minusCases) { case ->
            with(Fixture()) {
                val extended = store.plus("log" to log, "trace" to trace)
                case.minus(extended) shouldBe store
            }
        }

        test("close/closes-the-channel-and-drops-its-aliases") {
            with(Fixture()) {
                val extended = store.plus("log" to log, "log2" to log)
                val closed = extended.close(log)
                log.isClosed.shouldBeTrue()
                ("log" !in closed && "log2" !in closed).shouldBeTrue()
                closed.stdOut shouldBeSameInstanceAs out
            }
        }

        test("find-by-term/finds-the-channel-by-its-stream-term") {
            with(Fixture()) {
                store.findByTerm(err.streamTerm).toList() shouldBe listOf(err)
            }
        }
    })
