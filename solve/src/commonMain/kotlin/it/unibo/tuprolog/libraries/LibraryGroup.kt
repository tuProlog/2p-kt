package it.unibo.tuprolog.libraries

/** Represents a group of [Library] objects */
interface LibraryGroup<L : Library> : Library {

    /** All libraries composing this library group */
    val libraries: Collection<L>

    /** Adds a library to this library group */
    operator fun plus(library: L): LibraryGroup<L>

    /** Updates an already contained library, with given library */
    fun update(library: L): LibraryGroup<L>

}
