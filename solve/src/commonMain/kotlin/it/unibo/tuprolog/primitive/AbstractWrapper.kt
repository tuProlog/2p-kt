package it.unibo.tuprolog.primitive

/**
 * Abstract primitive wrapper class
 *
 * @param signature Supported input signature
 *
 * @author Enrico
 */
abstract class AbstractWrapper<out Wrapped>(val signature: Signature) {

    constructor(name: String, arity: Int, vararg: Boolean = false) : this(Signature(name, arity, vararg))

    /** A shorthand to get the signature functor name */
    val functor: String by lazy { signature.name }

    /** Gets this wrapped primitive description Pair formed by [signature] and wrapped primitive type */
    abstract val descriptionPair: Pair<Signature, Wrapped>
}
