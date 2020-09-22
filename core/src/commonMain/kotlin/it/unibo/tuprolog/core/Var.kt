package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.VarImpl
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface Var : Term {

    override val isVariable: Boolean
        get() = true

    override val variables: Sequence<Var>
        get() = sequenceOf(this)

    @JsName("isAnonymous")
    val isAnonymous: Boolean
        get() = ANONYMOUS_VAR_NAME == name

    @JsName("name")
    val name: String

    @JsName("completeName")
    val completeName: String

    override fun freshCopy(): Var = super.freshCopy() as Var

    override fun freshCopy(scope: Scope): Var =
        when {
            isAnonymous -> scope.anonymous()
            else -> scope.varOf(name)
        }

    @JsName("isNameWellFormed")
    val isNameWellFormed: Boolean

    companion object {

        const val ANONYMOUS_VAR_NAME = "_"

        val VAR_REGEX_PATTERN = "[A-Z_][A-Za-z_0-9]*".toRegex()

        @JvmStatic
        @JsName("of")
        fun of(name: String): Var = VarImpl(name)

        @JvmStatic
        @JsName("anonymous")
        fun anonymous(): Var = VarImpl(ANONYMOUS_VAR_NAME)

        @JvmStatic
        @JsName("escapeName")
        fun escapeName(string: String): String =
            "`$string`"

        @JvmStatic
        @JsName("escapeNameIfNecessary")
        fun escapeNameIfNecessary(string: String): String =
            if (VAR_REGEX_PATTERN.matches(string)) {
                string
            } else {
                escapeName(string)
            }
    }
}
