package it.unibo.tuprolog.solve.library

/** Represents a group of [Library] objects */
interface LibraryGroup<L : Library> : Library {

    /** All libraries composing this library group */
    val libraries: Collection<L>

    /** Adds a library to this library group */
    operator fun plus(library: L): LibraryGroup<L>

    /** Adds all libraries in provided libraryGroup to this libraryGroup */
    operator fun plus(libraryGroup: LibraryGroup<L>): LibraryGroup<L>

    /** Updates an already contained library, with given library */
    fun update(library: L): LibraryGroup<L>

}
