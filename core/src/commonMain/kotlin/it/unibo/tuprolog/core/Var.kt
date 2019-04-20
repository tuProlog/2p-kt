package it.unibo.tuprolog.core

interface Var : Term {

    override val isVar: Boolean
        get() = true

    override val isGround: Boolean
        get() = false

    val isAnonymous: Boolean
        get() = ANONYMOUS_VAR_NAME == name

    val name: String

    val completeName: String

    override fun clone(): Var {
        return Var.of(name)
    }

    val isNameWellFormed: Boolean

    companion object {

        const val ANONYMOUS_VAR_NAME = "_"

        val WELL_FORMED_NAME_PATTERN = Regex("""[A-Z_][A-Za-z_0-9]*""")

        fun of(name: String): Var {
            return VarImpl(name)
        }

        fun anonymous(): Var {
            return VarImpl(ANONYMOUS_VAR_NAME)
        }
    }
}
