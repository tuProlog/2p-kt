package it.unibo.tuprolog.solve.problog.lib.knowledge.impl

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.exception.TuPrologException
import it.unibo.tuprolog.solve.problog.ANNOTATION_FUNCTOR
import it.unibo.tuprolog.solve.problog.lib.exception.ClauseMappingException
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbExplanation
import it.unibo.tuprolog.solve.stdlib.rule.Semicolon

/**
 * Implements a [ClauseMapper] that handles the case of Probabilistic Annotated Disjunctions.
 * Those are a specific kind of [Clause]s in which the head is composed by a set of disjoint terms.
 * During probabilistic query resolution a clause of this kind can only be selected with only one
 * of its heads, so that it can't happen to have two different heads from the same annotate disjoint clause
 * present in the same solution and having the same substitution.
 *
 * Explanations including this kind of clauses usually involve knowledge compilation into
 * Multi-Value Decision Diagrams (MDDs) to support them natively. However, this usually requires a secondary
 * knowledge compilation process from MDDs to another data structure with more package/module support.
 * In our solution, we avoid the usage of MDDs by pre-computing a [ProbExplanation] with all the binary splits and
 * negations necessary to support Annotated Disjoint clauses.
 *
 * @author Jason Dellaluce
 */
internal object AnnotatedDisjunctionClauseMapper : ClauseMapper {
    override fun isCompatible(clause: Clause): Boolean = clause is Rule && clause.head.functor == Semicolon.FUNCTOR

    /** We split the disjoint heads in many sub-rules with a single head. This is not optimal
     * in regards of goal resolution (that will require a deeper exploration), however this
     * avoids the need of handling Multi-Valued Decision Diagrams (MDDs). This function applies
     * the "binary-split" logic to decompose a disjoint head annotation in its single-head
     * equivalents. Disjunction exclusion is achieved by pre-computing a [ProbExplanation] with all
     * the required negations needed to apply the logical exclusions of the multi-value binary split. */
    override fun apply(clause: Clause): List<Clause> {
        if (clause !is Rule) {
            throw ClauseMappingException("Clause is not an instance of Rule: $clause")
        }
        val body = clause.body
        val disjointHeads = mutableListOf<Struct>()
        collectAllHeads(clause.head, disjointHeads)

        var probSum = 0.0
        val mappedRules =
            disjointHeads
                .map { cur ->
                    val curProb = (cur[0] as Numeric).decimalValue.toDouble()
                    val curRuleHead = if (cur[1] is Struct) cur[1] as Struct else Struct.of(cur[1].toString())

                    probSum += curProb
                    ProbabilisticClauseMapper.mapRuleInternal(
                        Rule.of(
                            Struct.of(
                                ANNOTATION_FUNCTOR,
                                Numeric.of(curProb / (1.0 - (probSum - curProb))),
                                curRuleHead,
                            ),
                            body,
                        ),
                    )
                }.toList()

        var explanation: ProbExplanation = ProbExplanation.TRUE
        return mappedRules.map {
            val thisExplanation = ProbExplanation.of(it.second)
            val resultExplanation = thisExplanation and explanation
            explanation = explanation and thisExplanation.not()
            ProbabilisticClauseMapper.mapRuleWithExplanation(it.first, resultExplanation)
        }
    }

    private fun collectAllHeads(
        head: Term,
        accumulator: MutableList<Struct>,
    ) {
        if (head is Struct && head.arity == 2) {
            if (head.functor != Semicolon.FUNCTOR && head.functor != ANNOTATION_FUNCTOR) {
                throw TuPrologException("Badly formatted disjoint annotation: $head")
            }
            if (head.functor == Semicolon.FUNCTOR) {
                collectAllHeads(head[1], accumulator)
            }
        }

        var curHeadProb = 1.0
        var curHead = head
        if (head is Struct) {
            curHead = if (head.functor == Semicolon.FUNCTOR) head[0] else head
            if (curHead is Struct && curHead.functor == ANNOTATION_FUNCTOR) {
                val curHeadProbTerm = curHead[0]
                curHeadProb =
                    when (curHeadProbTerm) {
                        is Numeric -> curHeadProbTerm.decimalValue.toDouble()
                        else -> curHeadProbTerm.solveArithmeticExpression()
                    }
                curHead = curHead[1]
            }
        }
        accumulator.add(Struct.of(ANNOTATION_FUNCTOR, Numeric.of(curHeadProb), curHead))
    }
}
