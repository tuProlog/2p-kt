package it.unibo.tuprolog.solve

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.solve.exception.ResolutionCancelledException

/** The default [SolutionStream] (#1010): cancellation between solutions, with or without a closing `Halt`. */
class SolutionStreamSpec :
    FunSpec({
        val query = Atom.of("q")

        test("cancel-ends-silently-without-a-halt") {
            val stream = IteratorSolutionStream(generateSequence { Solution.yes(query) }.iterator())

            stream.next().shouldBeInstanceOf<Solution.Yes>()
            stream.cancel()
            stream.hasNext().shouldBeFalse()
        }

        test("cancel-answers-the-halt-then-ends") {
            val halt = Solution.halt(query, ResolutionCancelledException(context = Dummy.Context))
            val stream = IteratorSolutionStream(generateSequence { Solution.yes(query) }.iterator()) { halt }

            stream.next().shouldBeInstanceOf<Solution.Yes>()
            stream.cancel()
            stream.next() shouldBeSameInstanceAs halt
            stream.hasNext().shouldBeFalse()
        }

        test("close-closes-the-solutions") {
            var closed = 0
            val solutions =
                object : Iterator<Solution>, AutoCloseable {
                    override fun hasNext() = true

                    override fun next() = Solution.yes(query)

                    override fun close() {
                        closed++
                    }
                }
            val stream = IteratorSolutionStream(solutions)

            stream.close()
            closed shouldBe 1
            stream.hasNext().shouldBeFalse()
        }
    })
