package it.unibo.tuprolog.ui.gui.model

/** How up-to-date a page's solver session is with respect to its source/configuration. */
enum class SolverSessionLifecycle {
    /** No session has ever been built for this page. */
    ABSENT,

    /** A new session is being built. */
    BUILDING,

    /** The session reflects the page's current source/configuration. */
    FRESH,

    /** The session was built from source/configuration that has since changed; it must be rebuilt before use. */
    STALE,

    /** Building the session failed. */
    FAILED,
}
