package it.unibo.tuprolog.solve

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Atom

private val countAll: (SolutionStream) -> Int =
    js("(function (s) { var n = 0; for (const x of s) { n++; } return n; })")

private val takeFirst: (SolutionStream) -> Int =
    js("(function (s) { var n = 0; for (const x of s) { n++; break; } return n; })")

private val closeByName: (SolutionStream) -> Unit = js("(function (s) { s.close(); })")

/** On JavaScript, a [SolutionStream] works in `for...of`, and leaving the loop early closes it (#1010). */
class PlatformSolutionStreamSpec :
    FunSpec({
        val query = Atom.of("q")

        test("for-of-yields-every-solution") {
            val solutions = IteratorSolutionStream(sequenceOf(Solution.yes(query), Solution.no(query)).iterator())

            countAll(solutions) shouldBe 2
        }

        test("for-of-break-closes-the-stream") {
            val solutions = IteratorSolutionStream(generateSequence { Solution.yes(query) }.iterator())

            solutions.isCancelled.shouldBeFalse()
            takeFirst(solutions) shouldBe 1
            solutions.isCancelled.shouldBeTrue()
        }

        test("close-is-callable-by-name") {
            val solutions = IteratorSolutionStream(generateSequence { Solution.yes(query) }.iterator())

            closeByName(solutions)
            solutions.isCancelled.shouldBeTrue()
        }
    })
