package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.VarImpl
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

interface Var : Term {

    override val isVariable: Boolean
        get() = true

    override val variables: Sequence<Var>
        get() = sequenceOf(this)

    val isAnonymous: Boolean
        get() = ANONYMOUS_VAR_NAME == name

    val name: String

    val completeName: String

    override fun freshCopy(): Var = super.freshCopy() as Var

    override fun freshCopy(scope: Scope): Var =
        when {
            isAnonymous -> scope.anonymous()
            else -> scope.varOf(name)
        }

    val isNameWellFormed: Boolean

    companion object {

        const val ANONYMOUS_VAR_NAME = "_"

        val VAR_REGEX_PATTERN = "[A-Z_][A-Za-z_0-9]*".toRegex()

        @JvmStatic
        fun of(name: String): Var = VarImpl(name)

        @JvmStatic
        fun anonymous(): Var = VarImpl(ANONYMOUS_VAR_NAME)

        @JvmStatic
        fun escapeName(string: String): String =
            "`$string`"

        @JvmStatic
        fun escapeNameIfNecessary(string: String): String =
            if (VAR_REGEX_PATTERN.matches(string)) {
                string
            } else {
                escapeName(string)
            }
    }
}
