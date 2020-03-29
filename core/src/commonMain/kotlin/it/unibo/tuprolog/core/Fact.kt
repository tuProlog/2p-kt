package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.FactImpl
import kotlin.jvm.JvmStatic

interface Fact : Rule {

    override val body: Term
        get() = Truth.TRUE

    override val isFact: Boolean
        get() = true

    override fun freshCopy(): Fact = super.freshCopy() as Fact

    override fun freshCopy(scope: Scope): Fact = super.freshCopy(scope) as Fact

    companion object {
        @JvmStatic
        fun of(head: Struct): Fact = FactImpl(head)
    }
}
