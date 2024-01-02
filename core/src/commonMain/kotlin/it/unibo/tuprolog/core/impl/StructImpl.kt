package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term

internal class StructImpl(
    override val functor: String,
    override val args: List<Term>,
    tags: Map<String, Any> = emptyMap(),
) : AbstractStruct(functor, args, tags) {
    constructor(functor: String, args: Array<Term>, tags: Map<String, Any> = emptyMap()) :
        this(functor, listOf(*args), tags)

    override val isGround: Boolean by lazy { checkGroundness() }

    override fun copyWithTags(tags: Map<String, Any>): Struct = StructImpl(functor, args, tags)
}
