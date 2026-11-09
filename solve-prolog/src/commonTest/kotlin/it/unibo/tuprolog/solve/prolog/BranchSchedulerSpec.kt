package it.unibo.tuprolog.solve.prolog

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import it.unibo.tuprolog.core.parsing.parseAsClause
import it.unibo.tuprolog.core.parsing.parseAsStruct
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.answers
import it.unibo.tuprolog.solve.exception.error.SystemError
import it.unibo.tuprolog.solve.intercept.TransitionInterceptor
import it.unibo.tuprolog.solve.search.BranchScheduler
import it.unibo.tuprolog.theory.Theory
import kotlin.time.Duration.Companion.milliseconds

/**
 * Non-depth-first search on the stateful machine (#1010, section 7.4, items 3 to 5): a [BranchScheduler] other than
 * depth-first advances real suspended branches, each with its own bindings, one resolution step at a time.
 */
class BranchSchedulerSpec :
    FunSpec({
        fun solver(vararg clauses: String): Solver =
            MachinePrologSolverFactory.solverWithDefaultBuiltins(
                staticKb = Theory.of(clauses.map { it.parseAsClause() }),
            )

        val breadthFirst = SolveOptions.allLazily().setBranchScheduler(BranchScheduler.BREADTH_FIRST)
        val shallowAndDeep = arrayOf("p(X) :- q(X).", "p(shallow).", "q(X) :- r(X).", "r(deep).")

        test("breadth-first-returns-the-shallow-answer-first") {
            solver(*shallowAndDeep).answers("p(X)", breadthFirst) shouldBe listOf("yes: X = shallow", "yes: X = deep")
        }

        test("depth-first-is-standard-prolog") {
            val depthFirst = SolveOptions.allLazily().setBranchScheduler(BranchScheduler.DEPTH_FIRST)
            solver(*shallowAndDeep).answers("p(X)", depthFirst) shouldBe listOf("yes: X = deep", "yes: X = shallow")
            solver(*shallowAndDeep).answers("p(X)") shouldBe listOf("yes: X = deep", "yes: X = shallow")
        }

        test("breadth-first-escapes-a-left-recursive-branch") {
            val leftRecursive = solver("p :- p.", "p.")
            val query = "p".parseAsStruct()
            // depth-first never leaves the first clause
            leftRecursive
                .solve(query, SolveOptions.allLazily().setTimeout(200.milliseconds))
                .first()
                .shouldBeInstanceOf<Solution.Halt>()
            leftRecursive.solve(query, breadthFirst).first().shouldBeInstanceOf<Solution.Yes>()
        }

        test("interleaved-branches-keep-their-own-bindings") {
            val solver = solver("c(X, Y) :- X = a, d(Y).", "c(X, Y) :- X = b, d(Y).", "d(1).", "d(2).")
            val answers = solver.answers("c(X, Y)", breadthFirst)
            answers shouldContainExactlyInAnyOrder
                listOf("yes: X = a, Y = 1", "yes: X = a, Y = 2", "yes: X = b, Y = 1", "yes: X = b, Y = 2")
            // the two branches of c/2 advance in turns, rather than one after the other as in depth-first search
            answers.take(2) shouldBe listOf("yes: X = a, Y = 1", "yes: X = b, Y = 1")
        }

        data class Rejected(
            val id: String,
            val query: String,
            val clauses: List<String>,
            val reason: String,
        )

        withTests(
            nameFn = { "rejects/${it.id}" },
            listOf(
                Rejected("cut", "p", listOf("p :- !."), "cut"),
                Rejected("disjunction", "p", listOf("p :- (true ; true)."), "';'/2"),
                Rejected("database-update", "p", listOf("p :- assertz(q)."), "assertz/1"),
            ),
        ) { case ->
            val solution = solver(*case.clauses.toTypedArray()).solve(case.query.parseAsStruct(), breadthFirst).first()
            val error = solution.shouldBeInstanceOf<Solution.Halt>().exception.shouldBeInstanceOf<SystemError>()
            error.message shouldContain case.reason
        }

        test("rejects-an-interceptor-with-a-scheduler") {
            val options = breadthFirst.setInterceptor(object : TransitionInterceptor {})
            shouldThrow<IllegalArgumentException> { solver("p.").solve("p".parseAsStruct(), options).toList() }
        }
    })
