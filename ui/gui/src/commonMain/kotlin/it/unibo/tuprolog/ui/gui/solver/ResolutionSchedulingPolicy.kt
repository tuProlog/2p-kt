package it.unibo.tuprolog.ui.gui.solver

/** Controls whether resolutions on different pages may run at the same time, or whether starting one
 * pre-empts every other page's. */
enum class ResolutionSchedulingPolicy {
    /** Different pages may compute independently. */
    PER_PAGE_CONCURRENT,

    /** Starting a resolution cancels computations in every other page. */
    SINGLE_ACTIVE_RESOLUTION,
}
