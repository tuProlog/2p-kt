package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.core.Atom

/**
 * The three modes a stream can be opened with via [it.unibo.tuprolog.solve.libs.io.primitives.Open3] or
 * [it.unibo.tuprolog.solve.libs.io.primitives.Open4], mirroring the
 * `mode` argument of ISO's `open/3,4`: [READ] opens an existing source for input, [WRITE] truncates (or creates) a
 * sink for output, and [APPEND] opens (or creates) a sink for output, positioned at its end.
 *
 * Each constant corresponds to the lowercase Prolog [Atom] with the same name (e.g. [READ] to `read`), as collected
 * in [atomValues].
 */
enum class IOMode {
    READ,
    WRITE,
    APPEND,
    ;

    companion object {
        /** The Prolog atoms (`read`, `write`, `append`) accepted as the `mode` argument of `open/3,4`. */
        val atomValues =
            entries
                .asSequence()
                .map { it.name.lowercase() }
                .map(Atom.Companion::of)
                .toSet()
    }
}
