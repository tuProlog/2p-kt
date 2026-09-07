package it.unibo.tuprolog.solve.problog.lib

import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.solve.classic.stdlib.rule.Call
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.impl.ExtensionLibrary
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper
import it.unibo.tuprolog.solve.problog.PROBLOG_SPECIFIC_OPERATORS
import it.unibo.tuprolog.solve.problog.lib.primitive.ProbExplAnd
import it.unibo.tuprolog.solve.problog.lib.primitive.ProbHelper
import it.unibo.tuprolog.solve.problog.lib.primitive.ProbNegationAsFailure
import it.unibo.tuprolog.solve.problog.lib.primitive.ProbQuery
import it.unibo.tuprolog.solve.problog.lib.primitive.ProbSetConfig
import it.unibo.tuprolog.solve.problog.lib.primitive.ProbSolve
import it.unibo.tuprolog.solve.problog.lib.primitive.ProbSolveEvidence
import it.unibo.tuprolog.solve.problog.lib.primitive.ProbSolveWithEvidence
import it.unibo.tuprolog.solve.problog.lib.rules.Prob
import it.unibo.tuprolog.solve.rule.RuleWrapper
import it.unibo.tuprolog.solve.stdlib.primitive.EnsureExecutable
import it.unibo.tuprolog.solve.classic.stdlib.DefaultBuiltins as ClassicDefaultBuiltins

/**
 * The [Library] contributing ProbLog's probabilistic logic programming vocabulary on top of plain Prolog: the
 * `::` annotation [Operator] (see [PROBLOG_SPECIFIC_OPERATORS]) plus the internal primitives and rules
 * (`prob_*`/`prob/2`) that rewrite an annotated theory into a Prolog-compliant one carrying "explanation" terms,
 * and that drive probabilistic query resolution over them (see [it.unibo.tuprolog.solve.problog.ProblogSolver]).
 *
 * This is registered under the [Library.alias] `"problog.lang"`. Client code normally does not load this
 * library directly: [it.unibo.tuprolog.solve.problog.ProblogSolverFactory] takes care of loading either
 * [DefaultBuiltins] or [MinimalBuiltins] (depending on whether classic Prolog builtins are already present)
 * when building a solver.
 *
 * @author Jason Dellaluce
 */
object ProblogLib : ExtensionLibrary(Library.of("problog.lang")) {
    /** Name used for the internal variable holding a goal's probabilistic "explanation" term while a clause or
     * query is being rewritten (see [it.unibo.tuprolog.solve.problog.lib.knowledge.ProbExplanation]). */
    const val EXPLANATION_VAR_NAME = "EXPL"

    /** Functor of the `evidence/1`/`evidence/2` predicate, used in a ProbLog theory to assert facts known for
     * certain (e.g. `evidence(alarm, true).`), which condition the probability computed for other queries. */
    const val EVIDENCE_PREDICATE = "evidence"

    /** Common prefix shared by all the internal `prob_*` primitives and by the `prob/2` rule contributed by
     * this library, kept here to avoid repeating the literal across declarations. */
    const val PREDICATE_PREFIX = "prob"

    /** Adds [ANNOTATION_OPERATOR] (the `::` probability-annotation operator) on top of the operators already
     * known to the [Library] this is mixed into. */
    override val additionalOperators: Iterable<Operator>
        get() = PROBLOG_SPECIFIC_OPERATORS

    /** Adds the [Prob] rule, i.e. the `prob/2` entry point used internally to resolve a single probabilistic
     * goal and collect its explanation. */
    override val additionalRules: Iterable<RuleWrapper<*>>
        get() =
            listOf(
                Prob,
            )

    /** Adds the internal primitives implementing probabilistic goal resolution, evidence handling and
     * explanation composition (see each primitive's own documentation for its role). */
    override val additionalPrimitives: Iterable<PrimitiveWrapper<*>>
        get() =
            listOf(
                ProbHelper,
                ProbSolve,
                ProbExplAnd,
                ProbQuery,
                ProbSolveEvidence,
                ProbSolveWithEvidence,
                ProbNegationAsFailure,
                ProbSetConfig,
            )

    internal object DefaultBuiltins : ExtensionLibrary(ClassicDefaultBuiltins) {
        override val alias: String
            get() = ProblogLib.alias

        override val additionalOperators: Iterable<Operator>
            get() = ProblogLib.additionalOperators

        override val additionalPrimitives: Iterable<PrimitiveWrapper<*>>
            get() = ProblogLib.additionalPrimitives

        override val additionalRules: Iterable<RuleWrapper<*>>
            get() = ProblogLib.additionalRules
    }

    internal object MinimalBuiltins : ExtensionLibrary(ProblogLib) {
        override val additionalPrimitives: Iterable<PrimitiveWrapper<*>>
            get() =
                listOf(
                    EnsureExecutable,
                )

        override val additionalRules: Iterable<RuleWrapper<*>>
            get() =
                listOf(
                    Call,
                )
    }
}
