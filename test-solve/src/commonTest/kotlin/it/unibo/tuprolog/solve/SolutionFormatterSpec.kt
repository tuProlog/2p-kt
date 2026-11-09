package it.unibo.tuprolog.solve

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.solve.timing.timed
import kotlin.time.TestTimeSource

/** Test class for [SolutionFormatter], focused on the `GroundQueriesHaveBooleanSolution` presentation flag. */
class SolutionFormatterSpec :
    FunSpec({
        val scope = Scope.empty()

        val groundQuery =
            with(scope) { Struct.of("is", Integer.of(2), Struct.of("+", Integer.of(1), Integer.of(1))) }

        val nonGroundQuery =
            with(scope) { Struct.of("is", varOf("X"), Struct.of("+", Integer.of(1), Integer.of(1))) }

        test("by default a ground query's yes solution still shows the solved query") {
            val formatter = SolutionFormatter.of()
            val solution = Solution.yes(groundQuery)

            formatter.format(solution) shouldBe "yes: 2 is 1 + 1"
        }

        test("groundQueriesHaveBooleanSolution renders a ground query's yes solution as just yes") {
            val formatter = SolutionFormatter.of(groundQueriesHaveBooleanSolution = true)
            val solution = Solution.yes(groundQuery)

            formatter.format(solution) shouldBe "yes."
        }

        test("groundQueriesHaveBooleanSolution leaves a non-ground query's yes solution unaffected") {
            val formatter = SolutionFormatter.of(groundQueriesHaveBooleanSolution = true)
            val solution =
                with(scope) {
                    Solution.yes(nonGroundQuery, Substitution.of(mapOf(varOf("X") to Integer.of(2))))
                }

            formatter.format(solution) shouldBe "yes: 2 is 1 + 1\n    X = 2"
        }

        test("groundQueriesHaveBooleanSolution does not affect no or halt solutions") {
            val formatter = SolutionFormatter.of(groundQueriesHaveBooleanSolution = true)

            formatter.format(Solution.no(groundQuery)) shouldBe "no."
        }

        test("a timed solution shows its time on a trailing line") {
            val solution = sequenceOf(Solution.no(groundQuery)).timed(TestTimeSource()).single()

            SolutionFormatter.of().format(solution) shouldBe "no.\n    % time: 0s"
        }
    })
