package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.VarImpl
import it.unibo.tuprolog.scoping.Scope

interface Var : Term {

    override val isVariable: Boolean
        get() = true

    override val isGround: Boolean
        get() = false

    val isAnonymous: Boolean
        get() = ANONYMOUS_VAR_NAME == name

    val name: String

    val completeName: String

    override fun freshCopy(): Var {
        return super.freshCopy() as Var
    }

    override fun freshCopy(scope: Scope): Var {
        return if (isAnonymous) {
            scope.anonymous()
        } else {
            scope.varOf(name)
        }
    }

    val isNameWellFormed: Boolean

    companion object {

        const val ANONYMOUS_VAR_NAME = "_"

        val VAR_REGEX_PATTERN = "[A-Z_][A-Za-z_0-9]*".toRegex()

        fun of(name: String): Var = VarImpl(name)

        fun anonymous(): Var = VarImpl(ANONYMOUS_VAR_NAME)
    }
}
