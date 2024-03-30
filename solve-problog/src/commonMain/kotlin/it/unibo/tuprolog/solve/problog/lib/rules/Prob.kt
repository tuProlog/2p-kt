package it.unibo.tuprolog.solve.problog.lib.rules

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.classic.ClassicExecutionContext
import it.unibo.tuprolog.solve.problog.lib.ProblogLib
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbExplanation
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbExplanationTerm
import it.unibo.tuprolog.solve.problog.lib.primitive.ProbHelper
import it.unibo.tuprolog.solve.rule.RuleWrapper

/**
 * This rule represents a single goal in the probabilistic resolution. The first argument is a term representing
 * the [ProbExplanation] explanations of a goal's solutions, and the second argument represents the probabilistic
 * goal itself. The rule returns a sequence containing all the substitutions for the goal, which are
 * all its solutions and their corresponding explanation in the form of [ProbExplanationTerm].
 *
 * The computation uses regular Prolog SLD resolution semantics. Solutions are not grouped and the resolution
 * uses a depth-first approach.
 *
 * This rule also handles backwards compatibility with Prolog.
 *
 * It is worth mentioning that no probability is calculated by this primitive. Instead, this just bundles the logic
 * for finding probabilistic goal solutions and their explanation.
 *
 * Under the hood, this used [ProbHelper] to compute some imperative business logic and map the goal into a
 * more appropriate form, and then calls it.
 *
 * @author Jason Dellaluce
 */
internal object Prob : RuleWrapper<ClassicExecutionContext>(ProblogLib.PREDICATE_PREFIX, 2) {
    override val Scope.head: List<Term>
        get() = listOf(varOf("EXPL"), varOf("GOAL"))

    override val Scope.body: Term
        get() {
            val expl = varOf("EXPL")
            val goal = varOf("GOAL")
            val newGoal = varOf("NEW_GOAL")
            return tupleOf(
                atomOf("!"),
                structOf("ensure_executable", goal),
                structOf(ProbHelper.functor, expl, goal, newGoal),
                newGoal,
            )
        }
}
