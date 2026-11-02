package it.unibo.tuprolog.ui.gui.plp

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import it.unibo.tuprolog.bdd.BinaryDecisionDiagram
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.setBinaryDecisionDiagram
import it.unibo.tuprolog.solve.setProbability
import it.unibo.tuprolog.ui.gui.model.FeatureValue

class SolutionConversionsSpec :
    FunSpec({
        test("a probabilistic yes solution populates both probability and BDD feature state") {
            val solution =
                Solution
                    .yes(Struct.of("rain"))
                    .setProbability(0.25)
                    .setBinaryDecisionDiagram(BinaryDecisionDiagram.variableOf(Atom.of("rain")))

            val features = solution.plpFeatureState()

            (
                features
                    .getValue(
                        PlpGuiIds.SOLUTION_DETAILS,
                    ).getValue(PlpFeatureKeys.PROBABILITY)
                    .shouldBeInstanceOf<FeatureValue.Number>()
                    .value
            ) shouldBe
                0.25
            features
                .getValue(
                    PlpGuiIds.BDD_INSPECTOR,
                ).getValue(PlpFeatureKeys.BDD_DOT)
                .shouldBeInstanceOf<FeatureValue.Text>()
        }

        test("a plain yes solution without tags defaults to certain and reports no BDD") {
            val features = Solution.yes(Struct.of("rain")).plpFeatureState()

            (
                features
                    .getValue(
                        PlpGuiIds.SOLUTION_DETAILS,
                    ).getValue(PlpFeatureKeys.PROBABILITY)
                    .shouldBeInstanceOf<FeatureValue.Number>()
                    .value
            ) shouldBe
                1.0
            (
                features
                    .getValue(
                        PlpGuiIds.BDD_INSPECTOR,
                    ).getValue(PlpFeatureKeys.BDD_AVAILABLE)
                    .shouldBeInstanceOf<FeatureValue.BooleanValue>()
                    .value
            ) shouldBe
                false
        }

        test("a no solution reports no probability") {
            val features = Solution.no(Struct.of("rain")).plpFeatureState()

            features.getValue(PlpGuiIds.SOLUTION_DETAILS)[PlpFeatureKeys.PROBABILITY].shouldBeNull()
        }
    })
