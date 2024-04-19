package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.EmptyBlock
import it.unibo.tuprolog.core.EmptyList
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.SubstitutionFactory
import it.unibo.tuprolog.core.Terms
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.utils.synchronizedOnSelf

internal class ScopeImpl(
    private val _variables: MutableMap<String, Var>,
) : Scope, AbstractTermFactory(), SubstitutionFactory by DefaultSubstitutionFactory {
    override fun contains(variable: Var): Boolean = _variables.containsKey(variable.name)

    override fun contains(variable: String): Boolean = _variables.containsKey(variable)

    override fun get(variable: String): Var? = _variables[variable]

    override val variables: Map<String, Var>
        get() = _variables.toMap()

    override val fail: Truth
        get() = fail()

    override val emptyLogicList: EmptyList
        get() = emptyLogicList()

    override val emptyBlock: EmptyBlock
        get() = emptyBlock()

    override fun anonymousVar(): Var = super.varOf(Terms.ANONYMOUS_VAR_NAME)

    override fun anonymous(): Var = anonymousVar()

    override val `_`: Var
        get() = anonymousVar()

    override fun whatever(): Var = anonymousVar()

    override fun varOf(name: String): Var =
        synchronizedOnSelf {
            if (!_variables.containsKey(name)) {
                _variables[name] = super.varOf(name)
            }
            _variables[name]!!
        }

    override fun where(lambda: Scope.() -> Unit): Scope = this.also(lambda)

    override fun <R> with(lambda: Scope.() -> R): R = with(this, lambda)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ScopeImpl

        if (_variables != other._variables) return false

        return true
    }

    override fun hashCode(): Int = _variables.hashCode()

    override fun toString(): String = variables.toString()
}
