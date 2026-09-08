package it.unibo.tuprolog.serialize

/**
 * Platform-specific helpers for working with the plain object trees produced/consumed by
 * [Objectifier]/[Deobjectifier] implementations (e.g. `Map`/`List` structures on the JVM, or
 * `dynamic` objects on Kotlin/JS). Mainly intended for testing serializers/deserializers without
 * depending on a specific textual rendering (e.g. key order) of a given format.
 */
expect object ObjectsUtils {
    /**
     * Parses [string], assumed to be encoded in [mimeType], into a plain object tree — the same
     * kind of tree a [Deobjectifier] would receive as input.
     */
    fun parseAsObject(
        string: String,
        mimeType: MimeType,
    ): Any

    /**
     * Checks whether two plain object trees ([obj1] and [obj2]) are structurally equal, i.e.
     * equal maps/lists of equal elements and numbers compared by their textual value (so that,
     * e.g., `1` and `1.0` need not compare equal, but a `Long` and an `Int` holding the same
     * value do). Useful to compare objects obtained from differently-formatted (but equivalent)
     * input, irrespective of key/element order or numeric type.
     */
    fun deeplyEqual(
        obj1: Any?,
        obj2: Any?,
    ): Boolean
}
