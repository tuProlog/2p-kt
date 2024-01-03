package it.unibo.tuprolog.solve.directives

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Rule
import kotlin.js.JsName

interface ClauseListener {
    @JsName("onDirective")
    fun onDirective(directive: Directive)

    @JsName("onRule")
    fun onRule(rule: Rule)

    @JsName("onFact")
    fun onFact(fact: Fact)

    @JsName("onClause")
    fun onClause(clause: Clause)

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
