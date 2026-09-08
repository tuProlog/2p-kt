package it.unibo.tuprolog.solve.flags

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import kotlin.jvm.JvmField

/**
 * The ISO Prolog `unknown` flag, controlling what happens when a goal's predicate does not exist in the current
 * knowledge base/libraries at all: raise an [it.unibo.tuprolog.solve.exception.error.ExistenceError] ([ERROR],
 * the default), just report a [it.unibo.tuprolog.solve.exception.warning.MissingPredicate] warning ([WARNING]), or
 * silently fail the goal ([FAIL]).
 */
@Suppress("MemberVisibilityCanBePrivate")
object Unknown : NotableFlag {
    /** Raise an [it.unibo.tuprolog.solve.exception.error.ExistenceError] for missing predicates. */
    @JvmField
    val ERROR = Atom.of("error")

    /** Warn (via [it.unibo.tuprolog.solve.exception.warning.MissingPredicate]) about missing predicates instead of raising an error. */
    @JvmField
    val WARNING = Atom.of("warning")

    /** Silently fail the goal for missing predicates. */
    @JvmField
    val FAIL = Atom.of("fail")

    override val name: String = "unknown"

    override val defaultValue: Term
        get() = WARNING

    override val admissibleValues: Sequence<Term> = sequenceOf(ERROR, WARNING, FAIL)
}
