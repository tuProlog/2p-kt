package it.unibo.tuprolog.solve.library

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.function.PrologFunction
import it.unibo.tuprolog.solve.library.exception.AlreadyLoadedLibraryException
import it.unibo.tuprolog.solve.library.exception.NoSuchALibraryException
import it.unibo.tuprolog.solve.primitive.Primitive
import it.unibo.tuprolog.theory.Theory
import kotlin.js.JsName

/** A class representing an agglomerate of libraries with an alias */
class Libraries(libraries: Sequence<AliasedLibrary>) : LibraryGroup<AliasedLibrary>,
    Map<String, AliasedLibrary> by (libraries.map { it.alias to it }.toMap()) {

    constructor(vararg library: AliasedLibrary) : this(library.asSequence())
    constructor(libraries: Iterable<AliasedLibrary>) : this(libraries.asSequence())

    /** All library aliases of libraries included in this library group */
    @JsName("libraryAliases")
    val libraryAliases: Set<String>
        inline get() = keys

    override val libraries: Collection<AliasedLibrary>
        get() = values.toList()

    override val operators: OperatorSet by lazy {
        OperatorSet(libraries.flatMap { it.operators.asSequence() })
    }

    override val theory: Theory by lazy {
        Theory.indexedOf(libraries.flatMap { it.theory.clauses.asSequence() })
    }

    override val primitives: Map<Signature, Primitive> by lazy {
        libraries.flatMap { lib ->
            lib.primitives.entries.asSequence().flatMap {
                sequenceOf(
                    it.toPair(),
                    it.key.copy(name = lib.alias + AliasedLibrary.ALIAS_SEPARATOR + it.key.name) to it.value
                )
            }
        }.toMap()
    }

    override val functions: Map<Signature, PrologFunction> by lazy {
        libraries.flatMap { lib ->
            lib.functions.entries.asSequence().flatMap {
                sequenceOf(
                    it.toPair(),
                    it.key.copy(name = lib.alias + AliasedLibrary.ALIAS_SEPARATOR + it.key.name) to it.value
                )
            }
        }.toMap()
    }

    override fun plus(library: AliasedLibrary): Libraries =
        libraryAliases.find { library.alias in libraryAliases }
            ?.let { alreadyLoadedError(library) }
            ?: Libraries(libraries.asSequence() + sequenceOf(library))

    override fun plus(libraryGroup: LibraryGroup<AliasedLibrary>): Libraries =
        libraryGroup.libraries.find { it.alias in libraryAliases }
            ?.let { alreadyLoadedError(it) }
            ?: Libraries(libraries.asSequence() + libraryGroup.libraries.asSequence())

    override fun minus(library: AliasedLibrary): Libraries {
        if (library.alias in libraryAliases) {
            noSuchALibraryError(library)
        }
        return Libraries(libraries.asSequence().filter { it.alias != library.alias })
    }

    @JsName("minusAlias")
    operator fun minus(alias: String): Libraries {
        if (alias in libraryAliases) {
            noSuchALibraryError(alias)
        }
        return Libraries(libraries.asSequence().filter { it.alias != alias })
    }

    @JsName("minusAliases")
    operator fun minus(aliases: Iterable<String>): Libraries {
        val toBeRemoved = aliases.map {
            if (it in libraryAliases) {
                noSuchALibraryError(it)
            }
            it
        }.toSet()
        return Libraries(libraries.asSequence().filterNot { it.alias in toBeRemoved })
    }

    override fun update(library: AliasedLibrary): Libraries =
        libraryAliases.find { library.alias in libraryAliases }
            ?.let { Libraries(libraries.asSequence() + sequenceOf(library)) }
            ?: throw IllegalArgumentException("A library aliased as `${library.alias}` has never been loaded")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Libraries

        if (libraries != other.libraries) return false

        return true
    }

    override fun hashCode(): Int = libraries.hashCode()

    override fun toString(): String = "Libraries($libraries)"

    companion object {
        /** Utility function to handle already loaded error */
        private fun alreadyLoadedError(library: AliasedLibrary): Nothing =
            throw AlreadyLoadedLibraryException("A library aliased as `${library.alias}` has already been loaded")

        private fun noSuchALibraryError(library: AliasedLibrary): Nothing =
            noSuchALibraryError(library.alias)

        private fun noSuchALibraryError(alias: String): Nothing =
            throw NoSuchALibraryException("No library with alias `$alias` has been loaded")
    }
}