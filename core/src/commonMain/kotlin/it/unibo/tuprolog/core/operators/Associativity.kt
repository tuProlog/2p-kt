package it.unibo.tuprolog.core.operators

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term

enum class Associativity {
    XF, YF, FX, FY, XFX, XFY, YFX;

    val isPrefix: Boolean
        get() = PREFIX.contains(this)

    val isInfix: Boolean
        get() = INFIX.contains(this)

    val isPostfix: Boolean
        get() = POSTFIX.contains(this)

    fun toTerm(): Atom = Atom.of(name.toLowerCase())

    companion object {
        val PREFIX: Set<Associativity> = setOf(FX, FY)
        val POSTFIX: Set<Associativity> = setOf(YF, XF)
        val INFIX: Set<Associativity> = setOf(XFX, YFX, XFY)

        val NON_PREFIX: Set<Associativity> = POSTFIX + INFIX

        fun fromTerm(atom: Atom): Associativity = valueOf(atom.value.toUpperCase())

        fun fromTerm(term: Term): Associativity =
                when(term) {
                    is Atom -> fromTerm(term)
                    else -> throw IllegalArgumentException("Argument `term` must be an atom")
                }
    }
}