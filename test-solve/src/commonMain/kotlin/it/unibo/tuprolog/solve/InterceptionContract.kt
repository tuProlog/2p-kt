package it.unibo.tuprolog.solve

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.core.parsing.parseAsClause
import it.unibo.tuprolog.solve.intercept.Decision
import it.unibo.tuprolog.solve.intercept.Port
import it.unibo.tuprolog.solve.intercept.Transition
import it.unibo.tuprolog.solve.intercept.TransitionInterceptor
import it.unibo.tuprolog.theory.Theory

/**
 * The ports a [TransitionInterceptor] sees, and the effect of its decisions (#1010, section 6). Traces list each port
 * as `port goal`, with the goal's unbound variables shown as `_`.
 */
fun interceptionContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    val pq = arrayOf("p(X) :- q(X).", "q(1).", "q(2).", "q(3).")

    scenario("interception/deterministic-call") {
        solverFactory.traced("p(X)", "p(X) :- r(X).", "r(1).").trace shouldBe
            listOf("call p(_)", "call r(_)", "exit r(1)", "exit p(1)")
    }

    scenario("interception/backtracking-redoes-the-exited-call") {
        val run = solverFactory.traced("q(X), X > 1", "q(1).", "q(2).")
        run.answers shouldBe listOf("yes: X = 2")
        run.trace shouldBe
            listOf(
                "call q(_)",
                "exit q(1)",
                "call 1 > 1",
                "fail 1 > 1",
                "redo q(_)",
                "exit q(2)",
                "call 2 > 1",
                "exit 2 > 1",
            )
    }

    scenario("interception/exhaustion-fails-every-open-call") {
        solverFactory.traced("q(X), X > 5", "q(1).", "q(2).").trace shouldBe
            listOf(
                "call q(_)",
                "exit q(1)",
                "call 1 > 5",
                "fail 1 > 5",
                "redo q(_)",
                "exit q(2)",
                "call 2 > 5",
                "fail 2 > 5",
                "fail q(_)",
            )
    }

    scenario("interception/control-constructs-have-no-ports") {
        solverFactory.traced("call((r(X), \\+ fail))", "r(1).").trace shouldBe
            listOf("call r(_)", "exit r(1)", "call \\+ fail", "exit \\+ fail")
    }

    scenario("interception/caught-exception-leaves-the-calls-inside-catch") {
        val run = solverFactory.traced("catch(r, _, true), s", "r :- t.", "t :- throw(oops).", "s.")
        run.answers shouldBe listOf("yes")
        run.trace shouldBe listOf("call r", "call t", "exception t", "exception r", "call s", "exit s")
    }

    scenario("interception/uncaught-exception-leaves-every-open-call") {
        solverFactory.traced("r", "r :- t.", "t :- throw(oops).").trace shouldBe
            listOf("call r", "call t", "exception t", "exception r")
    }

    scenario("interception/ports-of-a-call-share-its-invocation-and-depth") {
        val seen = mutableListOf<Transition>()
        val ids = mutableListOf<Triple<Port, Long, Int>>()
        solverFactory.traced("p(X)", *pq) {
            seen += it
            Decision.Continue
        }
        for (transition in seen) ids += Triple(transition.port, transition.invocation, transition.depth)
        val (p, q) = ids[0].second to ids[1].second
        ids.take(4) shouldBe
            listOf(Triple(Port.CALL, p, 1), Triple(Port.CALL, q, 2), Triple(Port.EXIT, q, 2), Triple(Port.EXIT, p, 1))
    }

    scenario("interception/fail-at-call") {
        val run = solverFactory.traced("q(X)", *pq) { if (it.port == Port.CALL) Decision.Fail else Decision.Continue }
        run.answers shouldBe listOf("no")
        run.trace shouldBe listOf("call q(_)", "fail q(_)")
    }

    scenario("interception/fail-at-exit-rejects-the-answer") {
        val run =
            solverFactory.traced("q(X)", *pq) {
                if (it.port == Port.EXIT && it.goal[0] == Integer.of(1)) Decision.Fail else Decision.Continue
            }
        run.answers shouldBe listOf("yes: X = 2", "yes: X = 3")
    }

    scenario("interception/fail-at-redo-stops-the-call") {
        val run = solverFactory.traced("q(X)", *pq) { if (it.port == Port.REDO) Decision.Fail else Decision.Continue }
        run.answers shouldBe listOf("yes: X = 1", "no")
        run.trace shouldBe listOf("call q(_)", "exit q(1)", "redo q(_)", "fail q(_)")
    }

    scenario("interception/redirect-runs-another-goal") {
        val run =
            solverFactory.traced("q(X)", "q(1).", "r(9).") {
                if (it.port == Port.CALL && it.goal.functor == "q") {
                    Decision.Redirect(Struct.of("r", it.goal[0]))
                } else {
                    Decision.Continue
                }
            }
        run.answers shouldBe listOf("yes: X = 9")
        run.trace shouldBe listOf("call q(_)", "call r(_)", "exit r(9)")
    }

    scenario("interception/halt-ends-the-resolution") {
        val run = solverFactory.traced("q(X)", *pq) { if (it.port == Port.EXIT) Decision.Halt() else Decision.Continue }
        run.answers.map { it.substringBefore(':') } shouldBe listOf("halt")
    }

    scenario("interception/decision-not-allowed-at-port") {
        shouldThrow<IllegalArgumentException> {
            solverFactory.traced("q(X)", *pq) {
                if (it.port == Port.EXIT) Decision.Redirect(Struct.of("q", it.goal[0])) else Decision.Continue
            }
        }
    }
}

/** The answers and the trace of [query], on a fresh solver whose static KB holds [clauses], with [decide] deciding. */
internal fun SolverFactory.traced(
    query: String,
    vararg clauses: String,
    options: SolveOptions = SolveOptions.allLazily(),
    decide: (Transition) -> Decision = { Decision.Continue },
): TracedRun {
    val solver = solverWithDefaultBuiltins(staticKb = Theory.of(clauses.map { it.parseAsClause() }))
    val formatter = TermFormatter.readable(solver.operators)
    val trace = mutableListOf<String>()
    val interceptor =
        object : TransitionInterceptor {
            override fun before(transition: Transition): Decision {
                trace +=
                    "${transition.port.name.lowercase()} ${formatter.format(
                        transition.goal.withBlanks(),
                    ).replace("'_'", "_")}"
                return decide(transition)
            }
        }
    val answers = solver.answers(query, options.setInterceptor(interceptor))
    return TracedRun(answers, trace)
}

/** What [traced] returns. */
internal data class TracedRun(
    val answers: List<String>,
    val trace: List<String>,
)

private fun Struct.withBlanks(): Struct =
    this[
        Substitution.of(
            variables.distinct().associateWith {
                Atom.of("_")
            },
        ),
    ].castToStruct()
