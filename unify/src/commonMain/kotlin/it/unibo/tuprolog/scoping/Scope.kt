package it.unibo.tuprolog.scoping

import it.unibo.tuprolog.core.Var

interface Scope {

    val variables: Map<String, Var>

    fun varOf(name: String): Var

    fun where (lambda: Scope.() -> Unit)

    companion object {
        fun of(vararg vars: Var): Scope {
            val variables: MutableMap<String, Var> = mutableMapOf()
            for (v in vars) {
                variables[v.name] = v
            }
            return ScopeImpl(variables)
        }

        fun of(vararg vars: Var, lambda: Scope.() -> Unit): Scope {
            val scope = of(*vars)
            scope.where(lambda)
            return scope
        }

        fun of(vararg vars: String): Scope {
            val variables: MutableMap<String, Var> = mutableMapOf()
            for (v in vars) {
                variables[v] = Var.of(v)
            }
            return ScopeImpl(variables)
        }

        fun of(vararg vars: String, lambda: Scope.() -> Unit): Scope {
            val scope = of(*vars)
            scope.where(lambda)
            return scope
        }
    }
}