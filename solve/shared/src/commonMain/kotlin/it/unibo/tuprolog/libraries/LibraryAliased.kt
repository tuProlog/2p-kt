package it.unibo.tuprolog.libraries

/** Represents a [Library] that can have an alias */
interface LibraryAliased : Library {

    /** The library alias */
    val alias: String

    companion object {

        /** The character used to separate library alias from original name */
        const val ALIAS_SEPARATOR = "."
    }
}

/** Creates a library group from this library aliased and the "added" one */
operator fun <L : LibraryAliased> L.plus(library: L): LibraryGroup<LibraryAliased> =
        Libraries(this, library)
