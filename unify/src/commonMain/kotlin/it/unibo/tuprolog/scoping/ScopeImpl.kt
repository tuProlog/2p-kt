package it.unibo.tuprolog.scoping

import it.unibo.tuprolog.core.Var

internal class ScopeImpl(private val _variables: MutableMap<String, Var>) : Scope {

    override val variables: Map<String, Var>
        get() = _variables

    override fun varOf(name: String): Var {
        if (name !in _variables) {
            _variables[name] = Var.of(name)
        }
        return _variables[name]!!
    }

    override fun where(lambda: Scope.() -> Unit) {
        this.lambda()
    }
}