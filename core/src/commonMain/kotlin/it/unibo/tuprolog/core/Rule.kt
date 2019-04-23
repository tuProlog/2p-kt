package it.unibo.tuprolog.core

internal interface Rule : Clause {

    override val head: Struct

    override val isRule: Boolean
        get() = true

    override val isFact: Boolean
        get() = body.isTrue

    override val isDirective: Boolean
        get() = false

    companion object {
        const val FUNCTOR = ":-"

        fun of(head: Struct, vararg body: Term): Rule {
            return if (body.isEmpty() || (body.size == 1 && body[0].isTrue)) {
                Fact.of(head)
            } else {
                TODO()
            }
        }
    }
}