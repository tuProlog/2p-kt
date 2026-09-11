package it.unibo.tuprolog.ui.gui.solver

enum class ResolutionSchedulingPolicy {
    /** Different pages may compute independently. */
    PER_PAGE_CONCURRENT,

    /** Starting a resolution cancels computations in every other page. */
    SINGLE_ACTIVE_RESOLUTION,
}
