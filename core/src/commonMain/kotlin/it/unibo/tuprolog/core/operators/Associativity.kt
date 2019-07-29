package it.unibo.tuprolog.core.operators

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term

/**
 * Enumeration containing all type of associativity that can be given to Operators.
 *
 * The conventions for defining the order (prefix, infix and postfix) and associativity (left, right, none) of operators are as follows,
 * where f stands for the position of the operator and x and y for its arguments:
 *
 * | Prefix | Postfix | Infix | Associativity |
 * | ------ | ------- | ----- | ------------- |
 * | fx     | xf      | xfx   | none          |
 * |        | yf      | yfx   | left          |
 * | fy     |         | xfy   | right         |
 *
 */
enum class Associativity {
    /** Postfix and no-associative */
    XF,
    /** Postfix and left-associative */
    YF,
    /** Prefix and no-associative */
    FX,
    /** Prefix and right-associative */
    FY,
    /** Infix and no-associative */
    XFX,
    /** Infix and right-associative */
    XFY,
    /** Infix and left-associative */
    YFX;

    /** Whether this associativity is a prefix one */
    val isPrefix: Boolean
        get() = PREFIX.contains(this)

    /** Whether this associativity is an infix one */
    val isInfix: Boolean
        get() = INFIX.contains(this)

    /** Whether this associativity is a postfix one */
    val isPostfix: Boolean
        get() = POSTFIX.contains(this)

    /** Creates an atom containing the associativity symbolic name */
    fun toTerm(): Atom = Atom.of(name.toLowerCase())

    companion object {
        /** Set of prefix associativity */
        val PREFIX: Set<Associativity> = setOf(FX, FY)
        /** Set of postfix associativity */
        val POSTFIX: Set<Associativity> = setOf(YF, XF)
        /** Set of infix associativity */
        val INFIX: Set<Associativity> = setOf(XFX, YFX, XFY)
        /** Set of non-prefix associativity */
        val NON_PREFIX: Set<Associativity> = POSTFIX + INFIX

        /** Retrieves the associativity from an Atom value, throwing exception if not found */
        fun fromTerm(atom: Atom): Associativity = valueOf(atom.value.toUpperCase())

        /** Retrieves the associativity from an Atom value, throwing exception if not found */
        fun fromTerm(term: Term): Associativity =
                when (term) {
                    is Atom -> fromTerm(term)
                    else -> throw IllegalArgumentException("Argument `term` must be an atom")
                }
    }
}