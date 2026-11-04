package it.unibo.tuprolog.solve.tags

import it.unibo.tuprolog.core.Term

/** Whether a tag-aware unificator requires the terms it compares to carry the same tags. */
internal enum class TagComparisonVerse {
    EQUALS,
    IGNORE,
    ;

    fun accepts(
        lhs: Term,
        rhs: Term,
    ): Boolean =
        when (this) {
            EQUALS -> lhs.tags == rhs.tags
            IGNORE -> true
        }
}
