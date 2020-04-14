package it.unibo.tuprolog.solve

import kotlin.js.JsName

/**
 * Signature to [Wrapped] type, abstract wrapper class
 *
 * @param signature the supported input signature
 *
 * @author Enrico
 */
abstract class AbstractWrapper<out Wrapped>(@JsName("signature") val signature: Signature) {

    constructor(name: String, arity: Int, vararg: Boolean = false) : this(
        Signature(
            name,
            arity,
            vararg
        )
    )

    /** A shorthand to get the signature functor name */
    @JsName("functor")
    val functor: String
        inline get() = signature.name

    /** The wrapped implementation */
    @JsName("wrappedImplementation")
    abstract val wrappedImplementation: Wrapped

    /** Gets this wrapped primitive description Pair formed by [signature] and wrapped primitive type */
    @JsName("descriptionPair")
    val descriptionPair: Pair<Signature, Wrapped>
        inline get() = signature to wrappedImplementation
}
