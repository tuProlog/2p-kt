package it.unibo.tuprolog.solve.channel

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.solve.channel.OutputStore.Companion.STDERR
import it.unibo.tuprolog.solve.channel.OutputStore.Companion.STDOUT
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

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
                assertSame(out, store.current)
                assertEquals(listOf(STDOUT), store.currentAliases.toList())
            }
        }

        test("current/can-be-set-by-alias-or-channel") {
            with(Fixture()) {
                assertSame(err, store.setCurrent(STDERR).current)
                assertSame(err, store.setCurrent(err).current)
                assertSame(out, store.setCurrent("no-such-alias").current)
            }
        }

        withTests(nameFn = { "plus/${it.id}" }, plusCases) { case ->
            with(Fixture()) {
                val extended = case.plus(this)
                assertSame(log, extended["log"])
                assertSame(trace, extended["trace"])
                assertSame(out, extended.stdOut)
                assertEquals(listOf("log"), extended.aliasesOf(log).toList())
            }
        }

        withTests(nameFn = { "minus/${it.id}" }, minusCases) { case ->
            with(Fixture()) {
                val extended = store.plus("log" to log, "trace" to trace)
                assertEquals(store, case.minus(extended))
            }
        }

        test("close/closes-the-channel-and-drops-its-aliases") {
            with(Fixture()) {
                val extended = store.plus("log" to log, "log2" to log)
                val closed = extended.close(log)
                assertTrue(log.isClosed)
                assertTrue("log" !in closed && "log2" !in closed)
                assertSame(out, closed.stdOut)
            }
        }

        test("find-by-term/finds-the-channel-by-its-stream-term") {
            with(Fixture()) {
                assertEquals(listOf(err), store.findByTerm(err.streamTerm).toList())
            }
        }
    })
