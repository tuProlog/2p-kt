package it.unibo.tuprolog.solve.directives

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Substitution.Unifier
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName

interface DirectiveListener : ClauseListener {

    @JsName("unificator")
    val unificator: Unificator

    @JsName("patterns")
    val patterns: List<Term>

    @JsName("onDirectiveMatchingPattern")
    fun onDirectiveMatchingPattern(directive: Directive, pattern: Term, unifier: Unifier)

    @JsName("listenDirective")
    fun listenDirective(directive: Directive) {
        patterns.asSequence()
            .map { it to (unificator.mgu(directive.body, it)) }
            .filter { (_, substitution) -> substitution.isSuccess }
            .firstOrNull()
            ?.let { (pattern, substitution) ->
                listenDirectiveMatchingPattern(directive, pattern, substitution as Unifier)
            } ?: onDirective(directive)
    }

    @JsName("listenDirectiveMatchingPattern")
    fun listenDirectiveMatchingPattern(directive: Directive, pattern: Term, unifier: Unifier) =
        onDirectiveMatchingPattern(directive, pattern, unifier)

    override fun listen(clause: Clause) {
        when (clause) {
            is Directive -> listenDirective(clause)
            else -> super.listen(clause)
        }
    }
}
