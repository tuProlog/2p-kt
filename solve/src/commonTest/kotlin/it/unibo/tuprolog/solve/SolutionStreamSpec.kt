package it.unibo.tuprolog.solve

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.solve.exception.ResolutionCancelledException

/**
 * The default [SolutionStream] (#1010): cancellation between solutions, with or without a closing `Halt`; a single-pass,
 * lazy [Sequence].
 */
class SolutionStreamSpec :
    FunSpec({
        val query = Atom.of("q")

        test("cancel-ends-silently-without-a-halt") {
            val stream = IteratorSolutionStream(generateSequence { Solution.yes(query) }.iterator())

            val solutions = stream.iterator()
            solutions.next().shouldBeInstanceOf<Solution.Yes>()
            stream.cancel()
            solutions.hasNext().shouldBeFalse()
        }

        test("cancel-answers-the-halt-then-ends") {
            val halt = Solution.halt(query, ResolutionCancelledException(context = Dummy.Context))
            val stream = IteratorSolutionStream(generateSequence { Solution.yes(query) }.iterator()) { halt }

            val solutions = stream.iterator()
            solutions.next().shouldBeInstanceOf<Solution.Yes>()
            stream.cancel()
            solutions.next() shouldBeSameInstanceAs halt
            solutions.hasNext().shouldBeFalse()
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
            stream.iterator().hasNext().shouldBeFalse()
        }

        test("single-pass") {
            val stream = IteratorSolutionStream(sequenceOf(Solution.yes(query)).iterator())

            stream.toList() shouldBe listOf(Solution.yes(query))
            shouldThrow<IllegalStateException> { stream.iterator() }
        }

        test("lazy-operations") {
            var produced = 0
            val stream = IteratorSolutionStream(generateSequence { Solution.yes(query).also { produced++ } }.iterator())

            stream.map { it.query }.take(2).toList() shouldBe listOf(query, query)
            produced shouldBe 2
        }

        test("to-list-closes") {
            var closed = 0
            val solutions =
                object : Iterator<Solution>, AutoCloseable {
                    var left = 2

                    override fun hasNext() = left > 0

                    override fun next() = Solution.yes(query).also { left-- }

                    override fun close() {
                        closed++
                    }
                }

            IteratorSolutionStream(solutions).toList() shouldBe listOf(Solution.yes(query), Solution.yes(query))
            closed shouldBe 1
        }

        test("as-iterable") {
            val stream = IteratorSolutionStream(sequenceOf(Solution.yes(query), Solution.no(query)).iterator())

            stream.asIterable().map { it.isYes } shouldBe listOf(true, false)
        }
    })
