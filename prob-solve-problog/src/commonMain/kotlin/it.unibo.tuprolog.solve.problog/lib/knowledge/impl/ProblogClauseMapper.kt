package it.unibo.tuprolog.solve.problog.lib.knowledge.impl

import it.unibo.tuprolog.bdd.BinaryDecisionDiagram
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.exception.TuPrologException
import it.unibo.tuprolog.solve.problog.lib.ProblogLib
import it.unibo.tuprolog.solve.problog.lib.ProblogLib.EVIDENCE_PREDICATE
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbTerm
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProblogObjectRef
import it.unibo.tuprolog.solve.problog.lib.primitive.ProbBuildAnd
import it.unibo.tuprolog.solve.problog.lib.rule.Prob
import it.unibo.tuprolog.solve.stdlib.rule.Semicolon

object ProblogClauseMapper {
    private val TRUE_TERMINAL = BinaryDecisionDiagram.Terminal<ProbTerm>(true)
    private var clauseIndex: Long = 1

    fun apply(clause: Clause): List<Clause> {
        return when (clause) {
            is Rule -> apply(clause)
            else -> listOf(clause)
        }
    }

    private fun apply(rule: Rule): List<Clause> {
        /* Disjoint probabilistic annotations */
        if (rule.head.functor == Semicolon.FUNCTOR) {
            return mapProbabilisticDisjointAnnotationRule(rule)
        }

        /* Probabilistic clauses and facts */
        if (rule.head.functor == ProblogLib.PROB_FUNCTOR) {
            return mapProbabilisticRule(rule)
        }

        /* Probabilistic clauses and facts */
        if (rule.head.functor == EVIDENCE_PREDICATE) {
            return mapEvidenceRule(rule)
        }

        /* Regular Prolog facts */
        if (rule.body is Truth) {
            return mapPrologFact(rule)
        }

        /* Regular Prolog rules */
        return mapPrologRule(rule)
    }

    private fun mapPrologFact(rule: Rule): List<Clause> {
        return listOf(
            Rule.of(
                Struct.of(Prob.FUNCTOR, bddToTerm(TRUE_TERMINAL), rule.head),
                rule.body
            )
        )
    }

    private fun mapPrologRule(rule: Rule): List<Clause> {
        val bddVar = Var.of(ProblogLib.SOLUTION_VAR_NAME)
        return listOf(
            Rule.of(
                Struct.of(Prob.FUNCTOR, bddVar, rule.head),
                Struct.of(Prob.FUNCTOR, bddVar, rule.body),
            )
        )
    }

    private fun mapEvidenceRule(rule: Rule): List<Clause> {
        if (rule.head.arity > 2) {
            throw TuPrologException("Invalid evidence predicate: ${rule.head}")
        } else if (rule.head.arity == 2 && rule.head[1] !is Truth) {
            throw TuPrologException("Invalid evidence predicate: ${rule.head}")
        }

        /* Adjust implicit evidence predicates */
        var headTerm = rule.head
        if (headTerm.arity == 1) {
            headTerm = Struct.of(headTerm.functor, headTerm[0], Truth.TRUE)
        }

        val bddBodyVar = Var.anonymous()
        return listOf(
            Rule.of(
                headTerm,
                Struct.of(Prob.FUNCTOR, bddBodyVar, rule.body),
            )
        )
    }

    private fun mapProbabilisticRule(rule: Rule): List<Clause> {
        if (rule.head.arity != 2) {
            throw TuPrologException("Invalid probabilistic rule: ${rule.head}")
        }

        /* Extract probability value */
        var probability = when (val probTerm = rule.head[0]) {
            is Numeric -> probTerm.decimalValue.toDouble()
            else -> parseArithmeticDivisionTerm(probTerm)
        }

        /* Extract head predicate */
        var headTerm = termToStruct(rule.head[1])
        if (headTerm.functor == EVIDENCE_PREDICATE) {
            throw TuPrologException("Probability notation can't be expressed on evidence: " + rule.head)
        }

        /* This is a negated probabilistic fact (A fact expressed as negated logic
        * i.e. providing the probability of that predicate being false).
        * We want to map these cases to positive logic facts, so we make them become
        * regular facts and negate the original probability value.
        * Clearly, this is optimized to make false facts with 0.0 stay as they
        * are, in order to lead the resolution engine to a failure. */
        var bodyTerm = rule.body
        if (rule.body is Truth) {
            if (!rule.body.isTrue && probability < 1.0) {
                probability = 1.0 - probability
                bodyTerm = Truth.of(true)
            }
        }

        /* All variables must be taken in consideration when grounding
           probabilistic clauses, so we must also remove anonymous
           variables. */
        val anonVarSubstitution = Substitution.of(
            headTerm.variables.toSet()
                .plus(bodyTerm.variables.toSet())
                .filter {
                    it.isAnonymous
                }
                .mapIndexed { index, it ->
                    Pair(it, Var.of("ANON_$index"))
                }
        )
        headTerm = termToStruct(headTerm.apply(anonVarSubstitution))
        bodyTerm = termToStruct(bodyTerm.apply(anonVarSubstitution))

        /* Make head non-ground inside the BDD, if it is not already.
        * Namely, we want BDD variables to contain all the variables present
        * in the rule, so we add the ones that are present in the body but
        * not in the head. */
        var bddHeadTerm = headTerm
        if (!Rule.of(headTerm, bodyTerm).isGround) {
            val headVars = headTerm.variables.toSet()
            val bodyVars = bodyTerm.variables.toSet()
            val diffVars = bodyVars.subtract(headVars)
            bddHeadTerm = Struct.of(headTerm.functor, *(headTerm.args + diffVars.toTypedArray()))
        }

        /* Create ObjectRef with BDD */
        val headBdd = BinaryDecisionDiagram.Var(
            ProbTerm(clauseIndex++, probability, bddHeadTerm)
        )

        /* Problog probabilistic facts */
        if (bodyTerm is Truth) {
            return listOf(
                Rule.of(
                    Struct.of(Prob.FUNCTOR, bddToTerm(headBdd), headTerm),
                    bodyTerm
                )
            )
        }

        /* Problog probabilistic rules */
        val bddVar = Var.of(ProblogLib.SOLUTION_VAR_NAME)
        val bddBodyVar = Var.of("${ProblogLib.SOLUTION_VAR_NAME}_Body")
        return listOf(
            Rule.of(
                Struct.of(Prob.FUNCTOR, bddVar, headTerm),
                Struct.of(
                    ",",
                    Struct.of(Prob.FUNCTOR, bddBodyVar, bodyTerm),
                    Struct.of(ProbBuildAnd.functor, bddVar, bddBodyVar, bddToTerm(headBdd)),
                ),
            )
        )
    }

    /** We split the disjoint heads in many sub-rules with a single head. This is not optimal
     * in regards of goal resolution (that will require a deeper exploration ), however this
     * avoids the need of handling Multi-Valued Decision Diagrams (MDDs). This function applies
     * the "binary-split" logic to decompose a disjoint head annotation in its single-head
     * equivalents. */
    private fun mapProbabilisticDisjointAnnotationRule(rule: Rule): List<Clause> {
        val body = rule.body
        val disjointHeads = mutableListOf<Struct>()
        collectDisjointAnnotationsHeads(rule.head, disjointHeads)

        val prevHeadList = mutableListOf<Term>()
        var probSum = 0.0
        return disjointHeads.flatMap { cur ->
            val curProb = (cur[0] as Numeric).decimalValue.toDouble()
            val curRuleHead = if (cur[1] is Struct) cur[1] as Struct else Struct.of(cur[1].toString())
            var curRuleBody = body

            probSum += curProb
            prevHeadList.forEach {
                curRuleBody = Struct.of(",", curRuleBody, Struct.of(Prob.Disjunction.FUNCTOR, it))
            }
            prevHeadList.add(curRuleHead)
            mapProbabilisticRule(
                Rule.of(
                    Struct.of(
                        ProblogLib.PROB_FUNCTOR,
                        Numeric.of(curProb / (1.0 - (probSum - curProb))),
                        curRuleHead
                    ),
                    curRuleBody
                )
            )
        }.toList()
    }

    private fun collectDisjointAnnotationsHeads(head: Term, accumulator: MutableList<Struct>) {
        if (head is Struct && head.arity == 2) {
            if (head.functor != Semicolon.FUNCTOR && head.functor != ProblogLib.PROB_FUNCTOR) {
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
            if (curHead is Struct && curHead.functor == ProblogLib.PROB_FUNCTOR) {
                val curHeadProbTerm = curHead[0]
                curHeadProb = when (curHeadProbTerm) {
                    is Numeric -> curHeadProbTerm.decimalValue.toDouble()
                    else -> parseArithmeticDivisionTerm(curHeadProbTerm)
                }
                curHead = curHead[1]
            }
        }
        accumulator.add(Struct.of(ProblogLib.PROB_FUNCTOR, Numeric.of(curHeadProb), curHead))
    }

    private fun parseArithmeticDivisionTerm(term: Term): Double {
        val termStr = term.toString().replace("\\s".toRegex(), "")
        val regexMatch = Regex("([0-9.]+)(/)([0-9.]+).*").find(termStr)
            ?: throw TuPrologException(
                "Unsupported probability notation in annotated disjunction: $termStr"
            )
        val (dividendStr, _, divisorStr) = regexMatch.destructured
        val dividend = dividendStr.toDoubleOrNull()
        val divisor = divisorStr.toDoubleOrNull()
        if (dividend == null || divisor == null) {
            throw TuPrologException(
                "Unable to parse arithmetic division expression: $termStr"
            )
        }
        return dividend / divisor
    }

    private fun termToStruct(term: Term): Struct {
        return if (term is Struct) {
            term
        } else {
            Struct.of(term.toString())
        }
    }

    private fun bddToTerm(bdd: BinaryDecisionDiagram<ProbTerm>): Term {
        return ProblogObjectRef(bdd)
    }
}
