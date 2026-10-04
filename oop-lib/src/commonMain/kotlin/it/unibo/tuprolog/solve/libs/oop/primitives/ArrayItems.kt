package it.unibo.tuprolog.solve.libs.oop.primitives

/**
 * `array_items(?ArrayRef, ?Items)`: converts between a Prolog list `Items` and an
 * [it.unibo.tuprolog.solve.libs.oop.ObjectRef] `ArrayRef` wrapping a JVM `Object[]` array holding
 * the same (converted) elements, in either direction -- see [AbstractIterableItems].
 *
 * Example: `array_items(A, [1, 2, 3])` binds `A` to an [it.unibo.tuprolog.solve.libs.oop.ObjectRef]
 * wrapping `new Object[]{1, 2, 3}`.
 */
object ArrayItems : AbstractIterableItems<Array<*>>("array", Array::class) {
    override val help: String =
        """
        `array_items(?ArrayRef, ?Items)`
        
        Converts between a Prolog list `Items` and an object reference `ArrayRef` wrapping a JVM/Kotlin array (`Object[]`) with the same elements. If `Items` is a list, each item is converted to an object (respecting `as` casts and `${'$'}Alias` references) and `ArrayRef` is unified with a reference to a fresh array; otherwise, if `ArrayRef` references an array, `Items` is unified with the list of its elements converted back to terms: strings become atoms, numbers become Prolog numbers, and other objects stay object references. Deterministic. Raises an instantiation error if both arguments are unbound, and a type error if `ArrayRef` is neither unbound nor an object reference; fails if it references something that is not an array.

        **Examples**

        ```prolog
        ?- array_items(A, [a, b, c]), array_items(A, L).
        L = [a, b, c].

        ?- array_items(A, [1, 2.5]), array_items(A, L).
        L = [1, 2.5].

        ?- new_object('java.util.ArrayList', [], L), array_items(L, Items).
        no.

        ?- array_items(A, L).
        throws error(instantiation_error, _).
        ```
        """.trimIndent()

    override fun Sequence<Any?>.toIterable(): Array<*> = toList().toTypedArray()

    override val Any?.isIterable: Boolean
        get() = this is Array<*>

    override val Array<*>.items: Sequence<Any?>
        get() = asSequence()
}
