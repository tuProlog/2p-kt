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