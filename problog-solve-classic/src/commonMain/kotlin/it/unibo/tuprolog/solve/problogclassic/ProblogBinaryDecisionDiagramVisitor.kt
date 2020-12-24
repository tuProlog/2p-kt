package it.unibo.tuprolog.solve.problogclassic

import it.unibo.tuprolog.bdd.BinaryDecisionDiagram
import it.unibo.tuprolog.bdd.BinaryDecisionDiagramVisitor
import it.unibo.tuprolog.bdd.applyAnd
import it.unibo.tuprolog.bdd.applyNot
import it.unibo.tuprolog.bdd.applyOr
import it.unibo.tuprolog.solve.problogclassic.knowledge.ProblogSolutionTerm
import kotlin.js.JsName

/**
 * A [BinaryDecisionDiagramVisitor] that calculates the probability over a BBD representing a probabilistic
 * logic query solution, in which every variable is a [ProblogSolutionTerm].
 *
 * TODO: Implement tabling optimization here to avoid computation repetitions
 *
 * @author Jason Dellaluce
 */
internal class ProblogBinaryDecisionDiagramVisitor : BinaryDecisionDiagramVisitor<ProblogSolutionTerm> {
    var prob = 0.0

    override fun visit(node: BinaryDecisionDiagram.Terminal<ProblogSolutionTerm>) {
        prob = if (node.value) 1.0 else 0.0
    }

    override fun visit(node: BinaryDecisionDiagram.Var<ProblogSolutionTerm>) {
        prob = node.value.probability * node.high.probability() + (1.0 - node.value.probability) * node.low.probability()
    }
}

/**
 * Computes the probability of a [BinaryDecisionDiagram] representing a probabilistic logic query solution using]
 * the Shannon expansion to recursively explore the diagram. It is worth mentioning that no optimization is
 * performed during the exploration, so BDDs must be reduced before invoking this procedure in order to reduce
 * computation time and complexity.
 *
 * @author Jason Dellaluce
 */
@JsName("probability")
internal fun BinaryDecisionDiagram<ProblogSolutionTerm>.probability(): Double {
    val visitor = ProblogBinaryDecisionDiagramVisitor()
    this.accept(visitor)
    return visitor.prob
}

/**
 * Encapsulates the logic used to build BDDs. In this way it is easier to change BDD construction algorithm
 * and to apply use-case-specific optimizations.
 *
 * @author Jason Dellaluce
 */
@JsName("problogNot")
internal fun BinaryDecisionDiagram<ProblogSolutionTerm>.problogNot(): BinaryDecisionDiagram<ProblogSolutionTerm> {
    return this.applyNot()
}

/**
 * Encapsulates the logic used to build BDDs. In this way it is easier to change BDD construction algorithm
 * and to apply use-case-specific optimizations.
 *
 * @author Jason Dellaluce
 */
@JsName("problogAnd")
internal infix fun BinaryDecisionDiagram<ProblogSolutionTerm>.problogAnd(that: BinaryDecisionDiagram<ProblogSolutionTerm>):
    BinaryDecisionDiagram<ProblogSolutionTerm> {
        return this.applyAnd(that)
    }

/**
 * Encapsulates the logic used to build BDDs. In this way it is easier to change BDD construction algorithm
 * and to apply use-case-specific optimizations.
 *
 * @author Jason Dellaluce
 */
@JsName("problogOr")
internal infix fun BinaryDecisionDiagram<ProblogSolutionTerm>.problogOr(that: BinaryDecisionDiagram<ProblogSolutionTerm>):
    BinaryDecisionDiagram<ProblogSolutionTerm> {
        return this.applyOr(that)
    }
