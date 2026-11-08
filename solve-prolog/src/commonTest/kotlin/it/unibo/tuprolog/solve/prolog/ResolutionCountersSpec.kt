package it.unibo.tuprolog.solve.prolog

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.parsing.parseAsClause
import it.unibo.tuprolog.core.parsing.parseAsStruct
import it.unibo.tuprolog.solve.ResolutionCounters
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.theory.Theory

/** The logical counts of [ResolutionCounters] on small programs, worked out by hand (#1010). */
class ResolutionCountersSpec :
    FunSpec({
        val factory = PrologSolverFactory

        data class CountsCase(
            val id: String,
            val query: String,
            val clauses: List<String>,
            val inferences: Long,
            val alternatives: Long,
            val choicePoints: Long,
        )

        val facts = listOf("p(1).", "p(2).", "p(3).")
        val cases =
            listOf(
                // one call to p/1, all three clauses tried, one choice point left after the first
                CountsCase("all-facts", "p(X)", facts, inferences = 1, alternatives = 3, choicePoints = 1),
                // p/1 as above, then one call to >/2 per answer of p/1
                CountsCase(
                    "facts-then-test",
                    "p(X), X > 1",
                    facts,
                    inferences = 4,
                    alternatives = 6,
                    choicePoints = 1,
                ),
                // q/1 calls p/1: two calls, one alternative of q/1 and three of p/1
                CountsCase("rule-calls-facts", "q(X)", facts + "q(X) :- p(X).", 2, 4, 1),
                // control constructs are not counted, the goals inside them are: the condition pulls one answer of
                // p(X), call/1 all three of p(Y)
                CountsCase("control-constructs", "(p(X) -> true ; fail), call(p(Y))", facts, 2, 4, 2),
                // the cut prunes p/1's remaining alternatives before they are tried
                CountsCase("cut", "p(X), !", facts, inferences = 1, alternatives = 1, choicePoints = 1),
                // findall/3 is one deterministic primitive call, and its nested resolution counts p/1 as above
                CountsCase("nested-resolution", "findall(X, p(X), L)", facts, 2, 4, 1),
                // a deterministic call creates no choice point
                CountsCase(
                    "deterministic",
                    "r(X)",
                    listOf("r(a)."),
                    inferences = 1,
                    alternatives = 1,
                    choicePoints = 0,
                ),
            )

        withTests(nameFn = { "counts/${it.id}" }, cases) { case ->
            val counters = ResolutionCounters()
            factory
                .solverWithDefaultBuiltins(staticKb = Theory.of(case.clauses.map { it.parseAsClause() }))
                .solve(case.query.parseAsStruct(), SolveOptions.allLazily().setCounters(counters))
                .toList()
            Triple(counters.inferences, counters.alternatives, counters.choicePoints) shouldBe
                Triple(case.inferences, case.alternatives, case.choicePoints)
        }

        test("counts/reset") {
            val counters = ResolutionCounters()
            factory
                .solverWithDefaultBuiltins()
                .solve("member(X, [a, b])".parseAsStruct(), SolveOptions.allLazily().setCounters(counters))
                .toList()
            counters.reset()
            counters.toString() shouldBe ResolutionCounters().toString()
        }
    })
