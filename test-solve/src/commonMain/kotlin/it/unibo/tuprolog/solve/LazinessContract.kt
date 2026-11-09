package it.unibo.tuprolog.solve

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.parsing.parseAsClause
import it.unibo.tuprolog.core.parsing.parseAsStruct
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.primitive.Primitive
import it.unibo.tuprolog.theory.Theory

/**
 * Alternatives are produced on demand (#1010, step 0). `gen/1` is a primitive yielding 1, 2, 3 that logs each
 * alternative as it is pulled, `note/1` logs its argument, and `boom/1` yields 1 and fails the test if its second
 * alternative is ever pulled. The log shows exactly which user code the solver ran, and when.
 */
fun lazinessContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    scenario("laziness/alternatives-interleave-with-continuation") {
        val probe = LazinessProbe(solverFactory)
        probe.answers("gen(X), note(X)") shouldBe listOf("yes: X = 1", "yes: X = 2", "yes: X = 3")
        probe.log shouldBe listOf("gen(1)", "note(1)", "gen(2)", "note(2)", "gen(3)", "note(3)")
    }

    scenario("laziness/first-answer-pulls-one-alternative") {
        val probe = LazinessProbe(solverFactory)
        probe.solver
            .solve("gen(X)".parseAsStruct())
            .first()
            .shouldBeInstanceOf<Solution.Yes>()
        probe.log shouldBe listOf("gen(1)")
    }

    scenario("laziness/repeated-has-next-does-not-advance") {
        val probe = LazinessProbe(solverFactory)
        val solutions = probe.solver.solve("gen(X), note(X)".parseAsStruct()).iterator()
        solutions.hasNext() shouldBe true
        val afterFirstCheck = probe.log.toList()
        solutions.hasNext() shouldBe true
        solutions.hasNext() shouldBe true
        probe.log shouldBe afterFirstCheck
        solutions.next().shouldBeInstanceOf<Solution.Yes>()
        solutions.hasNext()
        solutions.hasNext()
        probe.log shouldBe listOf("gen(1)", "note(1)")
    }

    scenario("laziness/cut-prunes-without-pulling") {
        val probe = LazinessProbe(solverFactory)
        probe.answers("gen(X), !") shouldBe listOf("yes: X = 1")
        probe.log shouldBe listOf("gen(1)")
    }

    scenario("laziness/once-prunes-without-pulling") {
        val probe = LazinessProbe(solverFactory)
        probe.answers("once(gen(X))") shouldBe listOf("yes: X = 1")
        probe.log shouldBe listOf("gen(1)")
    }

    scenario("laziness/unpulled-alternative-never-runs") {
        LazinessProbe(solverFactory).answers("boom(X), !") shouldBe listOf("yes: X = 1")
    }

    scenario("laziness/unused-clauses-never-run") {
        val probe = LazinessProbe(solverFactory, "c(1).", "c(2) :- note(2).")
        probe.answers("c(X), !") shouldBe listOf("yes: X = 1")
        probe.log shouldBe emptyList()
    }

    scenario("laziness/effect-order-across-backtracking") {
        val probe = LazinessProbe(solverFactory, "c(X) :- gen(X), note(X).")
        probe.answers("c(X), X >= 2, note(done)") shouldBe listOf("yes: X = 2", "yes: X = 3")
        probe.log shouldBe
            listOf("gen(1)", "note(1)", "gen(2)", "note(2)", "note(done)", "gen(3)", "note(3)", "note(done)")
    }
}

private class LazinessProbe(
    solverFactory: SolverFactory,
    vararg clauses: String,
) {
    val log = mutableListOf<String>()

    private fun generator(
        name: String,
        count: Int,
        pullable: Int = count,
    ) = Primitive { request ->
        sequence {
            for (i in 1..count) {
                check(i <= pullable) { "alternative $i of $name/1 was pulled" }
                log += "$name($i)"
                yield(request.replyWith(request.unificator.mgu(request.arguments[0], Integer.of(i))))
            }
        }
    }

    private val library =
        Library.of(
            "test.laziness",
            primitives =
                mapOf(
                    Signature("gen", 1) to generator("gen", 3),
                    Signature("boom", 1) to generator("boom", 2, pullable = 1),
                    Signature("note", 1) to
                        Primitive { request ->
                            log += "note(${request.arguments[0]})"
                            sequenceOf(request.replySuccess())
                        },
                ),
        )

    val solver =
        solverFactory.solverWithDefaultBuiltins(
            otherLibraries = Runtime.of(library),
            staticKb = Theory.of(clauses.map { it.parseAsClause() }),
        )

    fun answers(query: String): List<String> = solver.answers(query)
}
