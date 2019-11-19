package it.unibo.tuprolog.core

/**
 * An interface to be implemented by types convertible to Prolog [Term]s
 *
 * @author Enrico
 */
interface ToTermConvertible {

    /** Converts this instance to a Prolog [Term] */
    fun toTerm(): Term
}
