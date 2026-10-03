package it.unibo.tuprolog.solve.flags

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import kotlin.jvm.JvmField

/**
 * The ISO Prolog `unknown` flag, controlling what happens when a goal's predicate does not exist in the current
 * knowledge base/libraries at all: raise an [it.unibo.tuprolog.solve.exception.error.ExistenceError] ([ERROR]),
 * report an [it.unibo.tuprolog.solve.exception.warning.MissingPredicate] warning ([WARNING], the current default),
 * or silently fail the goal ([FAIL]).
 */
@Suppress("MemberVisibilityCanBePrivate")
object Unknown : NotableFlag {
    override val help: String =
        """
        `flag(unknown)`
        
        Controls what happens when a goal refers to a predicate that does not exist in the current knowledge bases or loaded libraries.
        
        - `error`: raise an existence error.
        - `warning`: emit a missing-predicate warning instead of raising the error.
        - `fail`: silently fail the goal.
        - Default in the current implementation: `warning`
        - Editable: yes

        **Examples**

        ```prolog
        ?- current_prolog_flag(unknown, V).
        V = warning.

        ?- undefined_pred.
        no.

        ?- set_prolog_flag(unknown, error), undefined_pred.
        throws error(existence_error(procedure, undefined_pred/0), _).

        ?- set_prolog_flag(unknown, fail), undefined_pred.
        no.
        ```
        """.trimIndent()

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

    override val admissibleValues = FlagDomain.SetOfTerms(ERROR, WARNING, FAIL)
}
