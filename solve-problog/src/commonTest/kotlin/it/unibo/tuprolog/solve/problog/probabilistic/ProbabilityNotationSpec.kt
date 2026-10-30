package it.unibo.tuprolog.solve.problog.probabilistic

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.exception.TuPrologException
import it.unibo.tuprolog.core.parsing.parseAsStruct
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.probability
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import it.unibo.tuprolog.solve.setProbabilistic
import it.unibo.tuprolog.theory.parsing.parseAsTheory
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

private val operators = ProblogSolverFactory.defaultBuiltins.operators

/** The probability of [query]'s single solution, on a solver loaded with [theory]. */
private fun probability(
    theory: String,
    query: String,
): Double {
    val solver = ProblogSolverFactory.mutableSolverWithDefaultBuiltins()
    solver.loadStaticKb(theory.parseAsTheory(operators))
    return solver
        .solve(
            query.parseAsStruct(operators),
            SolveOptions.DEFAULT.setProbabilistic(true),
        ).single()
        .probability
}

private class NotationCase(
    val id: String,
    val theory: String,
    val query: String,
    val probability: Double,
)

private val notations =
    listOf(
        NotationCase("number", "0.25::a.", "a", 0.25),
        NotationCase("division", "1/4::a.", "a", 0.25),
        NotationCase("product", "0.5*0.5::a.", "a", 0.25),
        NotationCase("sum-of-divisions", "1/4+1/4::a.", "a", 0.5),
        NotationCase("difference", "1-0.75::a.", "a", 0.25),
        NotationCase("product-then-division", "2*1/4::a.", "a", 0.5),
        NotationCase("annotated-disjunction-with-divisions", "1/3::a ; 2/3::b.", "b", 2.0 / 3),
        NotationCase("false-body-negates-the-probability", "0.3::a :- false.", "a", 0.7),
        NotationCase("negation-as-failure", "0.3::a. b :- \\+ a.", "b", 0.7),
        NotationCase("noisy-or", "0.3::a. 0.4::a.", "a", 0.58),
        NotationCase("negative-evidence", "0.5::a. 0.5::b. c :- a. c :- b. evidence(a, false).", "c", 0.5),
        NotationCase("dynamic-directive", ":- dynamic(p/1).", "(assertz(p(1)), p(1))", 1.0),
    )

class ProbabilityNotationSpec :
    FunSpec({
        withTests(nameFn = { it.id }, notations) { case ->
            val actual = probability(case.theory, case.query)
            assertTrue(
                TestUtils.assertEqualsDouble(actual, case.probability),
                "expected ${case.probability}, got $actual",
            )
        }

        test("non-arithmetic-probability-is-rejected") {
            assertFailsWith<TuPrologException> { probability("p::a.", "a") }
        }
    })
