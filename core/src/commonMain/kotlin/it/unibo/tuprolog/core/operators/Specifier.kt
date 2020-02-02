package it.unibo.tuprolog.core.operators

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.ToTermConvertible

/**
 * Enumeration containing all type of specifiers that can be given to Operators.
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
enum class Specifier : ToTermConvertible {
    /** Postfix and no-associative */
    XF,
    /** Postfix and left-associative */
    YF,
    /** Prefix and non-associative */
    FX,
    /** Prefix and right-associative */
    FY,
    /** Infix and non-associative */
    XFX,
    /** Infix and right-associative */
    XFY,
    /** Infix and left-associative */
    YFX;

    /** Whether this specifier is a prefix one */
    val isPrefix: Boolean
        get() = PREFIX.contains(this)

    /** Whether this specifier is an infix one */
    val isInfix: Boolean
        get() = INFIX.contains(this)

    /** Whether this specifier is a postfix one */
    val isPostfix: Boolean
        get() = POSTFIX.contains(this)

    /** Creates an atom containing the specifier symbolic name */
    override fun toTerm(): Atom = atomRepresentation

    private val atomRepresentation
        get() = Atom.of(name.toLowerCase())

    companion object {
        /** Set of prefix specifiers */
        val PREFIX: Set<Specifier> = setOf(FX, FY)
        /** Set of postfix specifiers */
        val POSTFIX: Set<Specifier> = setOf(YF, XF)
        /** Set of infix specifiers */
        val INFIX: Set<Specifier> = setOf(XFX, YFX, XFY)
        /** Set of non-prefix specifiers */
        val NON_PREFIX: Set<Specifier> = POSTFIX + INFIX

        /**
         * Retrieves the specifier from an Atom value
         *
         * @throws IllegalArgumentException if provided [Atom] value "upperCased" is not present in this enum
         */
        fun fromTerm(atom: Atom): Specifier = valueOf(atom.value.toUpperCase())

        /**
         * Retrieves the specifier from an Atom value, throwing exception if not found
         *
         * @throws IllegalArgumentException if provided term is not an [Atom] or [Atom] value "upperCased" is not present in this enum
         */
        fun fromTerm(term: Term): Specifier =
            when (term) {
                is Atom -> fromTerm(term)
                else -> throw IllegalArgumentException("Argument `$term` must be an atom")
            }
    }
}