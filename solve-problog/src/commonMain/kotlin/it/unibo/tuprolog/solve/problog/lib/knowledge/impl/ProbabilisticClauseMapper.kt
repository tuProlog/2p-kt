package it.unibo.tuprolog.solve.problog.lib.knowledge.impl

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.exception.TuPrologException
import it.unibo.tuprolog.solve.problog.ANNOTATION_FUNCTOR
import it.unibo.tuprolog.solve.problog.lib.ProblogLib
import it.unibo.tuprolog.solve.problog.lib.exception.ClauseMappingException
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbExplanation
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbTerm
import it.unibo.tuprolog.solve.problog.lib.primitive.ProbExplAnd

/**
 * A [ClauseMapper] that handles the case of probabilistic clauses and facts.
 * In rule bodies, associative predicates such as ',', ';', and '->' are unrolled and mapped too in order
 * to reduce the work done during query resolution.
 *
 * @author Jason Dellaluce
 */
internal object ProbabilisticClauseMapper : ClauseMapper {
    override fun isCompatible(clause: Clause): Boolean = clause is Rule && clause.head.functor == ANNOTATION_FUNCTOR

    override fun apply(clause: Clause): List<Clause> {
        if (clause !is Rule) {
            throw ClauseMappingException("Clause is not an instance of Rule: $clause")
        }
        val mapped = mapRuleInternal(clause)
        return listOf(mapRuleWithExplanation(mapped.first, ProbExplanation.of(mapped.second)))
    }

    fun mapRuleInternal(rule: Rule): Pair<Rule, ProbTerm> {
        if (rule.head.arity != 2) {
            throw TuPrologException("Invalid probabilistic rule: ${rule.head}")
        }

        // Extract probability value
        var probability =
            when (val probTerm = rule.head[0]) {
                is Numeric -> probTerm.decimalValue.toDouble()
                else -> probTerm.solveArithmeticExpression()
            }

        // Extract head predicate
        var headTerm = rule.head[1].safeToStruct()
        if (headTerm.functor == ProblogLib.EVIDENCE_PREDICATE) {
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

        // Remove anonymous variables
        val anonVariablesFix = Rule.of(headTerm, bodyTerm).withoutAnonymousVars()
        headTerm = anonVariablesFix.head
        bodyTerm = anonVariablesFix.body

        // Probabilistic term of this rule
        val extraVariables = Rule.of(headTerm, bodyTerm).extraVariables
        val probTerm = ProbTerm(ClauseMappingUtils.newClauseId(), probability, headTerm, extraVariables)

        return Pair(Rule.of(headTerm, bodyTerm), probTerm)
    }

    fun mapRuleWithExplanation(
        rule: Rule,
        explanation: ProbExplanation,
    ): Rule {
        // Problog probabilistic facts
        if (rule.body is Truth) {
            return Rule.of(
                rule.head.withExplanation(explanation.toTerm()),
                rule.body,
            )
        }

        // Problog probabilistic rules
        val explanationVar = Var.of("${ProblogLib.EXPLANATION_VAR_NAME}_Res")
        val explanationBodyVar = Var.of("${ProblogLib.EXPLANATION_VAR_NAME}_Body")
        val newBody = rule.body.withBodyExplanation(explanationBodyVar)
        return Rule.of(
            rule.head.withExplanation(explanationVar),
            Struct.of(
                ",",
                newBody,
                Struct.of(ProbExplAnd.functor, explanationVar, explanationBodyVar, explanation.toTerm()),
            ),
        )
    }
}
