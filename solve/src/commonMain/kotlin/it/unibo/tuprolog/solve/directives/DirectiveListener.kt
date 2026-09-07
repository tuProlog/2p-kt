package it.unibo.tuprolog.solve.directives

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Substitution.Unifier
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName

/**
 * A [ClauseListener] refinement that further dispatches [Directive]s (`:- Goal` clauses) depending on which of
 * [patterns] their body unifies with, rather than treating every directive alike via [ClauseListener.onDirective].
 *
 * @see DirectiveSelector for the concrete set of ISO/implementation-defined directive patterns 2P-Kt recognizes.
 */
interface DirectiveListener : ClauseListener {
    /** The [Unificator] used to match a directive's body against [patterns]. */
    @JsName("unificator")
    val unificator: Unificator

    /** The directive-body patterns recognized by this listener, tried in order. */
    @JsName("patterns")
    val patterns: List<Term>

    /** Invoked when a directive's body unifies with one of [patterns], via [unifier]. */
    @JsName("onDirectiveMatchingPattern")
    fun onDirectiveMatchingPattern(
        directive: Directive,
        pattern: Term,
        unifier: Unifier,
    )

    /**
     * Tries to unify [directive]'s body against each of [patterns], in order; on the first match, delegates to
     * [listenDirectiveMatchingPattern], otherwise falls back to [ClauseListener.onDirective].
     */
    @JsName("listenDirective")
    fun listenDirective(directive: Directive) {
        patterns
            .asSequence()
            .map { it to (unificator.mgu(directive.body, it)) }
            .filter { (_, substitution) -> substitution.isSuccess }
            .firstOrNull()
            ?.let { (pattern, substitution) ->
                listenDirectiveMatchingPattern(directive, pattern, substitution as Unifier)
            } ?: onDirective(directive)
    }

    /** Invoked by [listenDirective] on a match; by default just forwards to [onDirectiveMatchingPattern]. */
    @JsName("listenDirectiveMatchingPattern")
    fun listenDirectiveMatchingPattern(
        directive: Directive,
        pattern: Term,
        unifier: Unifier,
    ) = onDirectiveMatchingPattern(directive, pattern, unifier)

    override fun listen(clause: Clause) {
        when (clause) {
            is Directive -> listenDirective(clause)
            else -> super.listen(clause)
        }
    }
}
