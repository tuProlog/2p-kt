package it.unibo.tuprolog.solve

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Atom
import kotlin.streams.toList

/** On the JVM, a [SolutionStream] is also a `java.util.stream.Stream` that closes it (#1010). */
class PlatformSolutionStreamSpec :
    FunSpec({
        val query = Atom.of("q")

        test("stream-yields-the-solutions-and-closes-the-solution-stream") {
            val solutions = IteratorSolutionStream(sequenceOf(Solution.yes(query), Solution.no(query)).iterator())

            solutions.stream().use { it.toList() } shouldBe listOf(Solution.yes(query), Solution.no(query))
            solutions.isCancelled.shouldBeTrue()
        }
    })
