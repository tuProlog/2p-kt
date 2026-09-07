package it.unibo.tuprolog.solve.directives

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Rule
import kotlin.js.JsName

/**
 * A visitor over [Clause]s, dispatching to a specific callback depending on the clause's concrete kind
 * ([onDirective]/[onFact]/[onRule]/[onClause] for anything else), via [listen].
 *
 * @see DirectiveListener
 * @see ClausePartitioner
 */
interface ClauseListener {
    /** Invoked when [listen]ing a [Directive] (a `:- Goal` clause). */
    @JsName("onDirective")
    fun onDirective(directive: Directive)

    /** Invoked when [listen]ing a [Rule] (a clause with a non-empty body, other than a [Directive]). */
    @JsName("onRule")
    fun onRule(rule: Rule)

    /** Invoked when [listen]ing a [Fact] (a clause with an empty body). */
    @JsName("onFact")
    fun onFact(fact: Fact)

    /** Invoked when [listen]ing any [Clause] that is neither a [Directive], [Fact], nor [Rule]. */
    @JsName("onClause")
    fun onClause(clause: Clause)

    /** Dispatches [clause] to [onDirective]/[onFact]/[onRule]/[onClause], depending on its concrete kind. */
    @JsName("listen")
    fun listen(clause: Clause) {
        when (clause) {
            is Directive -> onDirective(clause)
            is Fact -> onFact(clause)
            is Rule -> onRule(clause)
            else -> onClause(clause)
        }
    }
}
