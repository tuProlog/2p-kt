package it.unibo.tuprolog.solve.prolog

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.parsing.parse
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolutionStream
import it.unibo.tuprolog.solve.exception.ResolutionCancelledException
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.primitive.Alternatives
import it.unibo.tuprolog.solve.primitive.Primitive
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.theory.Theory

/**
 * `solve`'s [SolutionStream] (#1010): cancelling stops a resolution, even inside a loop or a nested resolution, with a `Halt`;
 * closing ends it silently; and a primitive's alternatives that hold resources are released when their choice point
 * goes (cut, exception, close).
 */
class SolutionStreamSpec :
    FunSpec({
        test("cancel-inside-a-loop-halts") {
            val fixture = Fixture()
            val stream = fixture.stream("cancel_me, loop")
            val solutions = stream.iterator()

            solutions
                .next()
                .shouldBeInstanceOf<Solution.Halt>()
                .exception
                .shouldBeInstanceOf<ResolutionCancelledException>()
            solutions.hasNext().shouldBeFalse()
            stream.isCancelled.shouldBeTrue()
        }

        test("cancel-between-answers-halts") {
            val fixture = Fixture()
            val stream = fixture.stream("member(X, [1, 2, 3])")
            val solutions = stream.iterator()

            solutions.next().shouldBeInstanceOf<Solution.Yes>()
            stream.cancel()
            solutions
                .next()
                .shouldBeInstanceOf<Solution.Halt>()
                .exception
                .shouldBeInstanceOf<ResolutionCancelledException>()
            solutions.hasNext().shouldBeFalse()
        }

        test("nested-resolutions-share-cancellation") {
            val fixture = Fixture()
            val stream = fixture.stream("nested")
            val solutions = stream.iterator()

            solutions
                .next()
                .shouldBeInstanceOf<Solution.Halt>()
                .exception
                .shouldBeInstanceOf<ResolutionCancelledException>()
            fixture.nested
                .shouldBeInstanceOf<Solution.Halt>()
                .exception
                .shouldBeInstanceOf<ResolutionCancelledException>()
        }

        test("close-ends-silently-and-releases-producers") {
            val fixture = Fixture()
            val stream = fixture.stream("gen(X)")
            val solutions = stream.iterator()

            solutions.next().shouldBeInstanceOf<Solution.Yes>()
            fixture.released shouldBe 0
            stream.close()
            fixture.released shouldBe 1
            solutions.hasNext().shouldBeFalse()
        }

        test("cut-releases-producers") {
            val fixture = Fixture()
            val solutions = fixture.stream("gen(X), !").toList()

            solutions
                .single()
                .shouldBeInstanceOf<Solution.Yes>()
                .substitution.values
                .single() shouldBe Integer.of(1)
            fixture.released shouldBe 1
        }

        test("single-answer-releases-producers") {
            val fixture = Fixture()
            val solutions = fixture.stream("gen_once(X)").toList()

            solutions.single().shouldBeInstanceOf<Solution.Yes>()
            fixture.released shouldBe 1
        }

        test("solve-once-releases-producers") {
            val fixture = Fixture()

            fixture.solver.solveOnce(Struct.parse("gen(X)")).shouldBeInstanceOf<Solution.Yes>()
            fixture.released shouldBe 1
        }

        test("use-releases-producers") {
            val fixture = Fixture()
            val first = fixture.stream("gen(X)").use { it.first() }

            first.shouldBeInstanceOf<Solution.Yes>()
            fixture.released shouldBe 1
        }

        test("exception-releases-producers") {
            val fixture = Fixture()
            val solutions = fixture.stream("catch((gen(X), throw(e)), _, true)").toList()

            solutions.single().shouldBeInstanceOf<Solution.Yes>()
            fixture.released shouldBe 1
        }
    })

private class Fixture {
    lateinit var current: SolutionStream
    var released = 0
    var nested: Solution? = null

    // gen(X): X = 1, 2, 3, ... on demand, from an iterator that counts how often it is closed
    private val gen = counting(Alternatives.ON_DEMAND)

    // the same, but answering once
    private val genOnce = counting(Alternatives.AT_MOST_ONE)

    private fun counting(alternatives: Alternatives) =
        Primitive.of(alternatives) { request ->
            val counter =
                object : Iterator<Solve.Response>, AutoCloseable {
                    var n = 0

                    override fun hasNext() = true

                    override fun next() =
                        request.replySuccess(Substitution.of(request.arguments[0] as Var, Integer.of(++n)))

                    override fun close() {
                        released++
                    }
                }
            Sequence { counter }
        }

    private val cancelMe =
        Primitive.of(Alternatives.AT_MOST_ONE) { request ->
            current.cancel()
            sequenceOf(request.replySuccess())
        }

    // cancels, then runs a never-ending nested resolution, which must stop too
    private val nestedLoop =
        Primitive.of(Alternatives.AT_MOST_ONE) { request ->
            current.cancel()
            nested = request.subSolver().solve(Atom.of("loop")).first()
            sequenceOf(request.replySuccess())
        }

    private val library =
        Library.of(
            "stream.test",
            primitives =
                mapOf(
                    Signature("gen", 1) to gen,
                    Signature("gen_once", 1) to genOnce,
                    Signature("cancel_me", 0) to cancelMe,
                    Signature("nested", 0) to nestedLoop,
                ),
        )

    val solver by lazy {
        PrologSolverFactory.solverWithDefaultBuiltins(
            otherLibraries = Runtime.of(library),
            staticKb = Theory.of(Rule.of(Atom.of("loop"), Atom.of("loop"))),
        )
    }

    fun stream(query: String): SolutionStream = solver.solve(Struct.parse(query)).also { current = it }
}
