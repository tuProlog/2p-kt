package it.unibo.tuprolog.libraries

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.libraries.exception.AlreadyLoadedLibraryException
import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.primitive.function.PrologFunction
import it.unibo.tuprolog.theory.ClauseDatabase

/** A class representing an agglomerate of libraries with an alias */
class Libraries(libraries: Sequence<LibraryAliased>) : LibraryGroup<LibraryAliased>,
    Map<String, LibraryAliased> by (libraries.map { it.alias to it }.toMap()) {

    constructor(vararg library: LibraryAliased) : this(library.asSequence())
    constructor(libraries: Iterable<LibraryAliased>) : this(libraries.asSequence())

    /** All library aliases of libraries included in this library group */
    val libraryAliases: Set<String> by lazy { keys }

    override val libraries: Collection<LibraryAliased> by lazy { values.toList() }

    override val operators: OperatorSet by lazy {
        OperatorSet(libraries.flatMap { it.operators.asSequence() })
    }

    override val theory: ClauseDatabase by lazy {
        ClauseDatabase.of(libraries.flatMap { it.theory.clauses.asSequence() })
    }

    override val primitives: Map<Signature, Primitive> by lazy {
        libraries.flatMap { lib ->
            lib.primitives.entries.asSequence().flatMap {
                sequenceOf(
                    it.toPair(),
                    it.key.copy(name = lib.alias + LibraryAliased.ALIAS_SEPARATOR + it.key.name) to it.value
                )
            }
        }.toMap()
    }

    override val functions: Map<Signature, PrologFunction> by lazy {
        libraries.flatMap { lib ->
            lib.functions.entries.asSequence().flatMap {
                sequenceOf(
                    it.toPair(),
                    it.key.copy(name = lib.alias + LibraryAliased.ALIAS_SEPARATOR + it.key.name) to it.value
                )
            }
        }.toMap()
    }

    override fun plus(library: LibraryAliased): LibraryGroup<LibraryAliased> =
        libraryAliases.find { library.alias in libraryAliases }
            ?.let { alreadyLoadedError(library) }
            ?: Libraries(libraries.asSequence() + sequenceOf(library))

    override fun plus(libraryGroup: LibraryGroup<LibraryAliased>): LibraryGroup<LibraryAliased> =
        libraryGroup.libraries.find { it.alias in libraryAliases }
            ?.let { alreadyLoadedError(it) }
            ?: Libraries(libraries.asSequence() + libraryGroup.libraries.asSequence())

    override fun update(library: LibraryAliased): LibraryGroup<LibraryAliased> =
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

    /** Utility function to handle already loaded error */
    private fun alreadyLoadedError(library: LibraryAliased): Nothing =
        throw AlreadyLoadedLibraryException("A library aliased as `${library.alias}` has already been loaded")
}