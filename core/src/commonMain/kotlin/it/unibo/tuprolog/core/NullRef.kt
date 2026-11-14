package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.NullRefImpl
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * The [Ref] wrapping the host `null` value. All [NullRef]s are equal, regardless of their tags: like [EmptyList] and
 * [EmptyBlock], [instance] is the canonical untagged instance, while tagged copies are distinct, equal, instances.
 * A [NullRef] is not an [ObjectRef], nor an [Atom].
 */
interface NullRef : Ref {
    override val isNullRef: Boolean
        get() = true

    override fun asNullRef(): NullRef = this

    override val value: Nothing?
        get() = null

    override fun freshCopy(): NullRef

    override fun freshCopy(scope: Scope): NullRef

    companion object {
        /** Creates a [NullRef], equal to [instance]. */
        @JvmStatic
        @JsName("invoke")
        operator fun invoke(): NullRef = NullRefImpl()

        /** The canonical, untagged, [NullRef] instance. */
        @JvmStatic
        @JsName("instance")
        val instance: NullRef = NullRefImpl()
    }
}
