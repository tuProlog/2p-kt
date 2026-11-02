package it.unibo.tuprolog.ui.gui.prolog

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.ui.gui.presentation.SolutionPresentation
import it.unibo.tuprolog.ui.gui.solver.ResolutionStep

class SolutionConversionsSpec :
    FunSpec({
        test("a variable mentioned twice in the query is only bound once") {
            val x = Var.of("X")
            val query = Struct.of("holds", x, x)
            val solution = Solution.yes(query, Substitution.of(mapOf(x to Integer.of(1))))

            val step = solution.toStep("holds(X, X).", emptyList(), emptyMap(), OperatorSet.DEFAULT)

            val yes =
                (step.shouldBeInstanceOf<ResolutionStep.Yield>().solution)
                    .shouldBeInstanceOf<SolutionPresentation.Yes>()
            yes.bindings.size shouldBe 1
            yes.bindings.single().variable shouldBe "X"
            yes.bindings.single().value shouldBe "1"
        }

        test("the solved query and bindings are formatted as pretty expressions using the given operator set") {
            val x = Var.of("X")
            val sum = Struct.of("+", Integer.of(1), Integer.of(2))
            val query = Struct.of("compute", x)
            val solution = Solution.yes(query, Substitution.of(mapOf(x to sum)))

            val step = solution.toStep("compute(X).", emptyList(), emptyMap(), OperatorSet.DEFAULT)

            val yes =
                (step.shouldBeInstanceOf<ResolutionStep.Yield>().solution)
                    .shouldBeInstanceOf<SolutionPresentation.Yes>()
            yes.bindings.single().value shouldBe "1 + 2"
            yes.solvedQuery shouldBe "compute(1 + 2)"
        }

        test("groundQueriesHaveBooleanSolution nulls out solvedQuery for a ground query") {
            val query = Struct.of("is", Integer.of(2), Struct.of("+", Integer.of(1), Integer.of(1)))
            val solution = Solution.yes(query)

            val step =
                solution.toStep(
                    "2 is 1 + 1.",
                    emptyList(),
                    emptyMap(),
                    OperatorSet.DEFAULT,
                    groundQueriesHaveBooleanSolution = true,
                )

            val yes =
                (step.shouldBeInstanceOf<ResolutionStep.Yield>().solution)
                    .shouldBeInstanceOf<SolutionPresentation.Yes>()
            yes.solvedQuery shouldBe null
        }

        test("groundQueriesHaveBooleanSolution leaves a non-ground query's solvedQuery untouched") {
            val x = Var.of("X")
            val query = Struct.of("is", x, Struct.of("+", Integer.of(1), Integer.of(1)))
            val solution = Solution.yes(query, Substitution.of(mapOf(x to Integer.of(2))))

            val step =
                solution.toStep(
                    "X is 1 + 1.",
                    emptyList(),
                    emptyMap(),
                    OperatorSet.DEFAULT,
                    groundQueriesHaveBooleanSolution = true,
                )

            val yes =
                (step.shouldBeInstanceOf<ResolutionStep.Yield>().solution)
                    .shouldBeInstanceOf<SolutionPresentation.Yes>()
            yes.solvedQuery shouldBe "2 is 1 + 1"
        }

        test("groundQueriesHaveBooleanSolution defaults to false, keeping a ground query's solvedQuery") {
            val query = Struct.of("is", Integer.of(2), Struct.of("+", Integer.of(1), Integer.of(1)))
            val solution = Solution.yes(query)

            val step = solution.toStep("2 is 1 + 1.", emptyList(), emptyMap(), OperatorSet.DEFAULT)

            val yes =
                (step.shouldBeInstanceOf<ResolutionStep.Yield>().solution)
                    .shouldBeInstanceOf<SolutionPresentation.Yes>()
            yes.solvedQuery shouldBe "2 is 1 + 1"
        }
    })
