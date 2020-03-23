package it.unibo.tuprolog.solve.libraries

/** Represents a [Library] that can have an alias */
interface AliasedLibrary : Library {

    /** The library alias */
    val alias: String

    companion object {

        /** The character used to separate library alias from original name */
        const val ALIAS_SEPARATOR = "."
    }
}

/** Creates a library group from this library aliased and the "added" one */
operator fun <L : AliasedLibrary> L.plus(library: L): LibraryGroup<AliasedLibrary> =
    Libraries(this, library)
