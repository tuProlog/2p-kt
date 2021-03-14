package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.VarImpl
import kotlin.js.JsName
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

interface Var : Term {

    override val isVariable: Boolean
        get() = true

    override val variables: Sequence<Var>
        get() = sequenceOf(this)

    @JsName("isAnonymous")
    val isAnonymous: Boolean
        get() = Terms.ANONYMOUS_VAR_NAME == name

    @JsName("name")
    val name: String

    @JsName("id")
    val id: String

    @JsName("completeName")
    val completeName: String

    override fun freshCopy(): Var

    override fun freshCopy(scope: Scope): Var

    @JsName("isNameWellFormed")
    val isNameWellFormed: Boolean

    override fun asVar(): Var = this

    @Suppress("MayBeConstant")
    companion object {

        @JvmField
        val ANONYMOUS_NAME = Terms.ANONYMOUS_VAR_NAME

        @JvmField
        val NAME_PATTERN = Terms.VAR_NAME_PATTERN

        @JvmStatic
        @JsName("of")
        fun of(name: String): Var = VarImpl(name)

        @JvmStatic
        @JsName("anonymous")
        fun anonymous(): Var = VarImpl(Terms.ANONYMOUS_VAR_NAME)

        @JvmStatic
        @JsName("escapeName")
        fun escapeName(string: String): String = "`$string`"

        @JvmStatic
        @JsName("escapeNameIfNecessary")
        fun escapeNameIfNecessary(string: String): String =
            if (Terms.VAR_NAME_PATTERN.matches(string)) {
                string
            } else {
                escapeName(string)
            }
    }
}
