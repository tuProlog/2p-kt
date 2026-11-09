package it.unibo.tuprolog.solve.primitive

/**
 * How a solver may pull the alternatives (answers) of a [Primitive] (#1010). A solver that needs to know whether a
 * primitive has more answers, e.g. to avoid keeping a choice point after its last one, may only look ahead when the
 * primitive allows it: otherwise, looking ahead would run the primitive's code for an answer nobody asked for yet.
 */
enum class Alternatives {
    /** At most one answer: no choice point is ever needed. */
    AT_MOST_ONE,

    /**
     * More answers may follow, but computing the next one early is cheap, terminates, and has no effects (effects
     * described by a [Solve.Response]'s side effects only happen when the solver uses that response): solvers may look
     * ahead.
     */
    LOOKAHEAD_SAFE,

    /**
     * More answers may follow, and each is computed only when the search backtracks into the primitive. When the last
     * answer of a query comes from such a primitive, finding out that there are no more takes one more step, so the
     * query may end with a further `no`. The default.
     */
    ON_DEMAND,
}
