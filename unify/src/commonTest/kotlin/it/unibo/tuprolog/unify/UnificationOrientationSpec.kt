@file:Suppress("DEPRECATION")

package it.unibo.tuprolog.unify

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var

private const val SIDE = "side"
private const val LEFT = "left"
private const val RIGHT = "right"

/** Rebuilds this term with every node (variables included) tagged as coming from [side]. */
private fun Term.from(side: String): Term =
    when (this) {
        is Struct -> Struct.of(functor, args.map { it.from(side) }).replaceTags(mapOf(SIDE to side))
        else -> replaceTags(mapOf(SIDE to side))
    }

private fun Term.side(): Any? = tags[SIDE]

/** An observable [Unificator], recording every pair of terms it compares, left one first. */
private interface Recorder : Unificator {
    val pairs: MutableList<Pair<Term, Term>>
}

private class WorklistRecorder :
    AbstractWorklistUnificator(),
    Recorder {
    override val pairs = mutableListOf<Pair<Term, Term>>()

    override fun handleComparison(step: UnificationStep) {
        pairs += step.lhs to step.rhs
    }
}

private class EquationalRecorder :
    AbstractUnificator(),
    Recorder {
    override val pairs = mutableListOf<Pair<Term, Term>>()

    override fun checkTermsEquality(
        first: Term,
        second: Term,
    ): Boolean = first == second

    override fun handleEquation(
        request: Request,
        equation: Equation,
    ): Equation = equation.also { pairs += it.lhs to it.rhs }
}

private class RecorderCase(
    val id: String,
    val recorder: () -> Recorder,
)

private val engines =
    listOf(RecorderCase("worklist", ::WorklistRecorder), RecorderCase("equational", ::EquationalRecorder))

/** Two terms with no variable in common nor repeated, so no binding can carry a term across sides. */
private class LinearCase(
    val id: String,
    val build: Scope.() -> Pair<Term, Term>,
)

private val linearCases =
    listOf(
        LinearCase("nested-structs") {
            structOf("f", varOf("X"), structOf("g", varOf("Y"), atomOf("b"))) to
                structOf("f", atomOf("a"), structOf("g", structOf("h", varOf("Z")), varOf("W")))
        },
        LinearCase("partial-list-vs-list") {
            consOf(varOf("H"), varOf("T")) to logicListOf(atomOf("a"), atomOf("b"), atomOf("c"))
        },
        LinearCase("lists-of-variables") {
            logicListOf(varOf("A"), atomOf("b"), varOf("C")) to logicListOf(atomOf("a"), varOf("B"), varOf("D"))
        },
        LinearCase("tuples") { tupleOf(varOf("A"), atomOf("b")) to tupleOf(intOf(1), varOf("B")) },
        LinearCase("failing") { structOf("f", varOf("X"), atomOf("a")) to structOf("f", atomOf("b"), atomOf("c")) },
    )

private val cases = engines.flatMap { engine -> linearCases.map { engine to it } }

/**
 * Left operands' subterms are always compared on the left, right operands' ones on the right: custom unificators can
 * rely on it to treat the two sides differently (e.g. through their tags).
 */
class UnificationOrientationSpec :
    FunSpec({
        withTests(nameFn = { (engine, case) -> "mgu/${engine.id}/${case.id}" }, cases) { (engine, case) ->
            val (left, right) = Scope.empty().(case.build)()
            val recorder = engine.recorder()
            recorder.mgu(left.from(LEFT), right.from(RIGHT))
            recorder.pairs.shouldNotBeEmpty()
            withClue(recorder.pairs) {
                recorder.pairs.filter { (lhs, rhs) -> lhs.side() != LEFT || rhs.side() != RIGHT }.shouldBeEmpty()
            }
        }

        withTests(nameFn = { (engine, case) -> "merge/${engine.id}/${case.id}" }, cases) { (engine, case) ->
            val (left, right) = Scope.empty().(case.build)()
            val variable = Var.of("Shared")
            val recorder = engine.recorder()
            recorder.merge(Substitution.of(variable, left.from(LEFT)), Substitution.of(variable, right.from(RIGHT)))
            recorder.pairs.shouldNotBeEmpty()
            withClue(recorder.pairs) {
                // bindings themselves may be reported as `Variable = Value`, whichever substitution they come from
                recorder.pairs
                    .filterNot { (lhs, _) -> lhs.isVar }
                    .filter { (lhs, rhs) -> lhs.side() != LEFT || rhs.side() != RIGHT }
                    .shouldBeEmpty()
            }
        }

        withTests(nameFn = { "repeated-variable-carries-right-term-to-the-left/${it.id}" }, engines) { engine ->
            val x = Var.of("X")
            val recorder = engine.recorder()
            val left = Struct.of("f", x, x).from(LEFT)
            val right = Struct.of("f", Scope.empty().atomOf("a"), Scope.empty().atomOf("b")).from(RIGHT)
            recorder.mgu(left, right) shouldBe Substitution.failed()
            recorder.pairs.filter { (lhs, rhs) -> lhs.side() == RIGHT && rhs.side() == RIGHT }.shouldNotBeEmpty()
        }
    })
