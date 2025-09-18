package it.unibo.tuprolog.solve.problog.lib.knowledge

import it.unibo.tuprolog.bdd.bddOf
import it.unibo.tuprolog.bdd.bddTerminalOf
import it.unibo.tuprolog.solve.problog.lib.knowledge.impl.BinaryDecisionDiagramExplanation
import kotlin.js.JsName

/**
 * This interface represents an explanation of a probabilistic logic query over a given probabilistic theory.
 * By "explanation" we refer to the collection of terms used to compute a logic solution for a query.
 * This can be also seen as a boolean formula in which each boolean variable is represented by Prolog terms,
 * and as such fundamental logic operators are supported (not, and, or).
 *
 * Terms are represented by the [ProbTerm] class.
 *
 * This also acts as an abstraction for the underlying knowledge compilation system. In fact, state of the art
 * probabilistic logic programming research attempts involve the compilation of logic explanations in specific data
 * structures suitable for optimized Weighted Model Checking, that is used to compute the probability over
 * queries' solutions. Most popular proposals include Binary Decision Diagrams (BDD),
 * Decomposable Negation Normal Form circuit (d-DNNF), and Sentential Decision Diagrams (SDD).
 *
 * Instances of this interface must be immutable.
 *
 * @author Jason Dellaluce
 */
internal interface ProbExplanation {
    override fun toString(): String

    /**
     * Performs the "Not" unary boolean operation over this explanation.
     */
    @JsName("not")
    fun not(): ProbExplanation

    /**
     * Performs the "And" unary boolean operation over two [ProbExplanation]s.
     */
    @JsName("and")
    infix fun and(that: ProbExplanation): ProbExplanation

    /**
     * Performs the "Or" unary boolean operation over two [ProbExplanation]s.
     */
    @JsName("or")
    infix fun or(that: ProbExplanation): ProbExplanation

    /**
     * Computes the probability of this [ProbExplanation]s, if possible.
     * Returns the probability as [Double] on success, or throws an exception
     * if the computation is infeasible or an error is encountered.
     */
    @JsName("probability")
    val probability: Double

    /**
     * Computes the probability of this [ProbExplanation]s, if possible.
     * Returns the probability as [Double] on success, or null if the computation is
     * infeasible or an error is encountered.
     */
    @JsName("probabilityOrNull")
    val probabilityOrNull: Double?
        get() {
            return try {
                probability
            } catch (e: Throwable) {
                null
            }
        }

    /**
     * Returns true if at least one term contained in this explanation is not ground. This can happen
     * during explanation construction where a solution and all substitutions are yet to be discovered.
     */
    @JsName("containsNonGroundTerm")
    val containsAnyNotGroundTerm: Boolean

    /**
     * This applies the [transformation] to all the terms contained in this explanation and returns
     * a new [ProbExplanation] containing the results.
     */
    @JsName("applyTransformation")
    fun apply(transformation: (ProbTerm) -> ProbTerm): ProbExplanation

    companion object {
        /** A [ProbExplanation] representing a logic falsity. */
        val FALSE: ProbExplanation =
            BinaryDecisionDiagramExplanation(
                bddTerminalOf(false),
                BinaryDecisionDiagramExplanation.FALSE_COMPUTED_VALUE,
            )

        /** A [ProbExplanation] representing a logic truth. */
        val TRUE: ProbExplanation =
            BinaryDecisionDiagramExplanation(
                bddTerminalOf(true),
                BinaryDecisionDiagramExplanation.TRUE_COMPUTED_VALUE,
            )

        /** Creates a new [ProbExplanation] representing the single probabilistic logic term [term]. */
        fun of(term: ProbTerm): ProbExplanation =
            BinaryDecisionDiagramExplanation(
                bddOf(term),
                BinaryDecisionDiagramExplanation.ComputedValue(
                    term.probability,
                ),
            )
    }
}
