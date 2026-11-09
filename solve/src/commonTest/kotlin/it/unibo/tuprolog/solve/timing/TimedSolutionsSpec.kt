package it.unibo.tuprolog.solve.timing

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.solve.Dummy
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.exception.HaltException
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TestTimeSource

class TimedSolutionsSpec :
    FunSpec({
        val query = Atom.of("q")
        val yes = Solution.yes(query)

        // each solution takes its duration of solver time, and the end takes `tail` more
        fun solutions(
            clock: TestTimeSource,
            vararg costs: Pair<Duration, Solution>,
            tail: Duration = Duration.ZERO,
        ) = sequence {
            for ((cost, solution) in costs) {
                clock += cost
                yield(solution)
            }
            clock += tail
        }

        test("timing/exhausted-with-consumer-pauses") {
            val clock = TestTimeSource()
            val timed =
                solutions(
                    clock,
                    5.milliseconds to yes,
                    7.milliseconds to yes,
                    tail = 3.milliseconds,
                ).timed(clock)
            for (solution in timed) clock += 100.milliseconds // the consumer's pause
            timed.timing shouldBe
                ResolutionTiming(
                    firstAnswer = 5.milliseconds,
                    advances = listOf(5.milliseconds, 7.milliseconds),
                    active = 15.milliseconds,
                    elapsed = 215.milliseconds,
                    answers = 2,
                    outcome = Outcome.EXHAUSTED,
                )
        }

        test("timing/abandoned-after-first-answer") {
            val clock = TestTimeSource()
            val timed = solutions(clock, 5.milliseconds to yes, 7.milliseconds to yes).timed(clock)
            timed.first()
            timed.timing.outcome shouldBe Outcome.ABANDONED
            timed.timing.advances shouldBe listOf(5.milliseconds)
            timed.timing.active shouldBe 5.milliseconds
        }

        test("timing/halted") {
            val clock = TestTimeSource()
            val halt = Solution.halt(query, HaltException(context = Dummy.Context))
            val timed = solutions(clock, 2.milliseconds to yes, 4.milliseconds to halt).timed(clock)
            timed.toList()
            timed.timing.outcome shouldBe Outcome.HALTED
            timed.timing.answers shouldBe 2
        }

        test("timing/no-solutions-yet") {
            val timed = emptySequence<Solution>().timed(TestTimeSource())
            timed.timing shouldBe
                ResolutionTiming(null, emptyList(), Duration.ZERO, Duration.ZERO, 0, Outcome.ABANDONED)
        }

        test("timing/has-next-stays-false-after-exhaustion") {
            val clock = TestTimeSource()
            val iterator = solutions(clock, 5.milliseconds to yes).timed(clock).iterator()
            iterator.hasNext() shouldBe true
            iterator.hasNext() shouldBe true
            iterator.next()
            iterator.hasNext() shouldBe false
            iterator.hasNext() shouldBe false
        }

        test("timing/iterated-once") {
            val timed = sequenceOf(yes).timed(TestTimeSource())
            timed.toList()
            shouldThrow<IllegalStateException> { timed.toList() }
        }
    })
