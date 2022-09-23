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

object ProblogLib : ExtensionLibrary(Library.of("problog.lang")) {
    const val EXPLANATION_VAR_NAME = "EXPL"
    const val EVIDENCE_PREDICATE = "evidence"
    const val PREDICATE_PREFIX = "prob"

    override val additionalOperators: Iterable<Operator>
        get() = PROBLOG_SPECIFIC_OPERATORS

    override val additionalRules: Iterable<RuleWrapper<*>>
        get() = listOf(
            Prob
        )

    override val additionalPrimitives: Iterable<PrimitiveWrapper<*>>
        get() = listOf(
            ProbHelper,
            ProbSolve,
            ProbExplAnd,
            ProbQuery,
            ProbSolveEvidence,
            ProbSolveWithEvidence,
            ProbNegationAsFailure,
            ProbSetConfig
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
            get() = listOf(
                EnsureExecutable
            )

        override val additionalRules: Iterable<RuleWrapper<*>>
            get() = listOf(
                Call
            )
    }
}
