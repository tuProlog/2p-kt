package it.unibo.tuprolog.solve.intercept

/**
 * The ports of Byrd's box model (#1010): the logical events of a predicate call, as debuggers and tracers show them.
 *
 * A call has one [CALL], then any number of [EXIT]s (one per answer), each but the first preceded by a [REDO], and ends
 * with one [FAIL] (no more answers) or one [EXCEPTION], unless the resolution stops first. Control constructs (`,`/2,
 * `;`/2, `->`/2, `call/N`, `catch/3`, `throw/1`), `true`, `fail`/`false` and `!` have no ports of their own: the goals
 * inside them have.
 */
enum class Port {
    /** The call is about to start. */
    CALL,

    /** The call has succeeded, with the bindings of [Transition.goal]. */
    EXIT,

    /** The call is about to be retried for another answer, after an [EXIT]. */
    REDO,

    /** The call has no more answers. */
    FAIL,

    /** The call has raised [Transition.error], which leaves it. */
    EXCEPTION,
}
