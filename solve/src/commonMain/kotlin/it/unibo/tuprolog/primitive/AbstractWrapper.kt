package it.unibo.tuprolog.primitive

/**
 * Signature to [Wrapped] type, abstract wrapper class
 *
 * @param signature the supported input signature
 *
 * @author Enrico
 */
abstract class AbstractWrapper<out Wrapped>(val signature: Signature) {

    constructor(name: String, arity: Int, vararg: Boolean = false) : this(Signature(name, arity, vararg))

    /** A shorthand to get the signature functor name */
    val functor: String by lazy { signature.name }

    /** The wrapped implementation */
    abstract val wrappedImplementation: Wrapped

    /** Gets this wrapped primitive description Pair formed by [signature] and wrapped primitive type */
    val descriptionPair: Pair<Signature, Wrapped> by lazy { signature to wrappedImplementation }
}
