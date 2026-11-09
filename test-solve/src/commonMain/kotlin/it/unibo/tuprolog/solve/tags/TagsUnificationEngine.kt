package it.unibo.tuprolog.solve.tags

import it.unibo.tuprolog.unify.Unificator

/** A unification engine whose custom unificators can see terms' tags, and which of them it lets them see. */
internal enum class TagsUnificationEngine(
    val tagAware: (TagComparisonVerse) -> Unificator,
    val tagFlow: (TagFlow) -> Unificator,
    /** Whether tags on matching compound terms are visible, besides the ones on variables and constants. */
    val compoundTagsVisible: Boolean,
) {
    /** Compound terms are compared, hence visible, before being decomposed. */
    WORKLIST(::TagAwareUnificator, ::TagFlowUnificator, compoundTagsVisible = true),

    /** The worklist engine, opted into unification in the solver's bindings (#1010). */
    INCREMENTAL(::incrementalTagAwareUnificator, ::incrementalTagFlowUnificator, compoundTagsVisible = true),

    /** Matching compound terms are decomposed straight into equations of their arguments, so they are invisible. */
    EQUATIONAL(::EquationalTagAwareUnificator, ::EquationalTagFlowUnificator, compoundTagsVisible = false),
}
