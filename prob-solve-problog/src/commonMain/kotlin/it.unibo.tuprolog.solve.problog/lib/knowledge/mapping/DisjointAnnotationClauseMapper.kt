package it.unibo.tuprolog.solve.problog.lib.knowledge.mapping

import it.unibo.tuprolog.bdd.BinaryDecisionDiagram
import it.unibo.tuprolog.bdd.and
import it.unibo.tuprolog.bdd.not
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.exception.TuPrologException
import it.unibo.tuprolog.solve.problog.lib.ProblogLib
import it.unibo.tuprolog.solve.problog.lib.exception.ClauseMappingException
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbTerm
import it.unibo.tuprolog.solve.stdlib.rule.Semicolon

internal object DisjointAnnotationClauseMapper : ClauseMapper {
    override fun isCompatible(clause: Clause): Boolean {
        return clause is Rule && clause.head.functor == Semicolon.FUNCTOR
    }

    /** We split the disjoint heads in many sub-rules with a single head. This is not optimal
     * in regards of goal resolution (that will require a deeper exploration), however this
     * avoids the need of handling Multi-Valued Decision Diagrams (MDDs). This function applies
     * the "binary-split" logic to decompose a disjoint head annotation in its single-head
     * equivalents. Disjunction exclusion is achieved by pre-computing a BDD with all the required
     * negations needed to apply the logical exclusions of the multi-value binary split. */
    override fun apply(clause: Clause): List<Clause> {
        if (clause !is Rule) {
            throw ClauseMappingException("Clause is not an instance of Rule: $clause")
        }
        val body = clause.body
        val disjointHeads = mutableListOf<Struct>()
        collectAllHeads(clause.head, disjointHeads)

        var probSum = 0.0
        val mappedRules = disjointHeads.map { cur ->
            val curProb = (cur[0] as Numeric).decimalValue.toDouble()
            val curRuleHead = if (cur[1] is Struct) cur[1] as Struct else Struct.of(cur[1].toString())

            probSum += curProb
            ProbabilisticClauseMapper.mapRuleInternal(
                Rule.of(
                    Struct.of(
                        ProblogLib.PROB_FUNCTOR,
                        Numeric.of(curProb / (1.0 - (probSum - curProb))),
                        curRuleHead
                    ),
                    body
                )
            )
        }.toList()

        var dd: BinaryDecisionDiagram<ProbTerm> = BinaryDecisionDiagram.Terminal(true)
        return mappedRules.map {
            val thisBdd = BinaryDecisionDiagram.Var(it.second)
            val resBdd = thisBdd and dd
            dd = dd and thisBdd.not()
            ProbabilisticClauseMapper.mapRuleWithDecisionDiagram(it.first, resBdd)
        }
    }

    private fun collectAllHeads(head: Term, accumulator: MutableList<Struct>) {
        if (head is Struct && head.arity == 2) {
            if (head.functor != Semicolon.FUNCTOR && head.functor != ProblogLib.PROB_FUNCTOR) {
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
            if (curHead is Struct && curHead.functor == ProblogLib.PROB_FUNCTOR) {
                val curHeadProbTerm = curHead[0]
                curHeadProb = when (curHeadProbTerm) {
                    is Numeric -> curHeadProbTerm.decimalValue.toDouble()
                    else -> curHeadProbTerm.solveArithmeticExpression()
                }
                curHead = curHead[1]
            }
        }
        accumulator.add(Struct.of(ProblogLib.PROB_FUNCTOR, Numeric.of(curHeadProb), curHead))
    }
}
