package it.unibo.tuprolog.solve.problogclassic.knowledge

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.exception.TuPrologException
import it.unibo.tuprolog.solve.problogclassic.stdlib.DefaultBuiltins
import it.unibo.tuprolog.solve.problogclassic.stdlib.rule.Comma
import it.unibo.tuprolog.solve.problogclassic.stdlib.rule.NegationAsFailure
import it.unibo.tuprolog.solve.stdlib.rule.Semicolon
import kotlin.collections.List

/**
 * This is used to parse and generate Problog clauses from a raw Prolog theory.
 * By principle, a single logic clause can be mapped to 0 or more Problog clauses in order to
 * implement probabilistic logic specific features such as Annotated Disjunctions or Evidence.
 *
 * @author Jason Dellaluce
 * */
internal abstract class ProblogClauseMapper {
    private var clauseIndex = 1L

    protected fun mapClause(clause: Clause): List<Clause> {
        return when (clause) {
            is Rule -> mapRule(clause)
            else -> listOf(clause)
        }
    }

    private fun mapRule(rule: Rule): List<Clause> {
        if (rule.head.functor == Semicolon.FUNCTOR) {
            return mapDisjointAnnotationRule(rule)
        }

        if (rule.head.functor == DefaultBuiltins.PROB_FUNCTOR) {
            if (rule.head.arity != 2) {
                throw TuPrologException("Invalid probabilistic fact: ${rule.head}")
            }

            /* Extract probability */
            var probability = when(val probTerm = rule.head[0]) {
                is Numeric -> probTerm.decimalValue.toDouble()
                else -> parseArithmeticDivisionTerm(probTerm)
            }

            /* Extract predicate */
            val predicateTerm = rule.head[1]
            val predicate = if (predicateTerm is Struct) {
                predicateTerm
            } else {
                Struct.of(predicateTerm.toString())
            }

            /* This is a negated probabilistic fact */
            if (rule.body is Truth) {
                /* Map "negated" facts */
                if (!rule.body.isTrue) {
                    probability = 1.0 - probability
                }
                return listOf(ProblogFact(clauseIndex++, probability, predicate))
            }

            /* This is a probabilistic clause */
            return listOf(ProblogRule(clauseIndex++, probability, predicate, rule.body))
        }

        return listOf(rule)
    }

    /** We split the disjoint heads in many sub-rules with a single head. This is not optimal
     * in regards of goal resolution (that will require a deeper exploration ), however this
     * avoids the need of handling Multi-Valued Decision Diagrams (MDDs). This function applies
     * the "binary-split" logic to decompose a disjoint head annotation in its single-head
     * equivalents. */
    private fun mapDisjointAnnotationRule(rule: Rule): List<Clause> {
        val body = rule.body
        val disjointHeads = mutableListOf<Struct>()
        collectDisjointAnnotationsHeads(rule.head, disjointHeads)

        val prevHeadList = mutableListOf<Term>()
        var probSum = 0.0
        return disjointHeads.map { cur ->
            val curProb = (cur[0] as Numeric).decimalValue.toDouble()
            var curRuleBody = body
            prevHeadList.forEach { curRuleBody = Struct.of(Comma.functor, curRuleBody, Struct.of(NegationAsFailure.FUNCTOR, it)) }
            val curRuleHead = if (cur[1] is Struct) cur[1] as Struct else Struct.of(cur[1].toString())
            prevHeadList.add(curRuleHead)
            probSum += curProb
            ProblogRule(clauseIndex++, curProb / (1.0 - (probSum - curProb)), curRuleHead, curRuleBody)
        }.toList()
    }

    private fun collectDisjointAnnotationsHeads(head: Term, accumulator: MutableList<Struct>) {
        if (head is Struct && head.arity == 2) {
            if (head.functor != Semicolon.FUNCTOR && head.functor != DefaultBuiltins.PROB_FUNCTOR) {
                throw TuPrologException("Badly formatted disjoint annotation: $head")
            }
            if (head.functor == Semicolon.FUNCTOR) {
                collectDisjointAnnotationsHeads(head[1], accumulator)
            }
        }

        var curHeadProb = 1.0
        var curHead = head
        if (head is Struct) {
            curHead = if (head.functor == Semicolon.FUNCTOR) head[0] else head
            if (curHead is Struct && curHead.functor == DefaultBuiltins.PROB_FUNCTOR) {
                val curHeadProbTerm = curHead[0]
                curHeadProb = when(curHeadProbTerm) {
                    is Numeric -> curHeadProbTerm.decimalValue.toDouble()
                    else -> parseArithmeticDivisionTerm(curHeadProbTerm)
                }
                curHead = curHead[1]
            }
        }
        accumulator.add(Struct.of(DefaultBuiltins.PROB_FUNCTOR, Numeric.of(curHeadProb), curHead))
    }

    private fun parseArithmeticDivisionTerm(term: Term): Double {
        val termStr = term.toString().replace("\\s".toRegex(), "")
        val regexMatch = Regex("([0-9.]+)(/)([0-9.]+).*").find(termStr)
                ?: throw TuPrologException(
                        "Unsupported probability notation in annotated disjunction: $termStr")
        val (dividendStr, _, divisorStr) = regexMatch.destructured
        val dividend = dividendStr.toDoubleOrNull()
        val divisor = divisorStr.toDoubleOrNull()
        if (dividend == null || divisor == null) {
            throw TuPrologException(
                    "Unable to parse arithmetic division expression: $termStr")
        }
        return dividend / divisor
    }
}
