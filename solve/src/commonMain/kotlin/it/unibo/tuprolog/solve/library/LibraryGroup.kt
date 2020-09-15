package it.unibo.tuprolog.solve.library

import kotlin.js.JsName

/** Represents a group of [Library] objects */
interface LibraryGroup<L : Library> : Library {

    /** All libraries composing this library group */
    @JsName("libraries")
    val libraries: Collection<L>

    /** Adds a library to this library group */
    @JsName("plus")
    operator fun plus(library: L): LibraryGroup<L>

    /** Adds all libraries in provided libraryGroup to this libraryGroup */
    @JsName("plusGroup")
    operator fun plus(libraryGroup: LibraryGroup<L>): LibraryGroup<L>

    /** Removes the library from this library group */
    @JsName("minus")
    operator fun minus(library: L): LibraryGroup<L>

    /** Updates an already contained library, with given library */
    @JsName("update")
    fun update(library: L): LibraryGroup<L>
}
