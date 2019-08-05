package it.unibo.tuprolog.libraries

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.theory.ClauseDatabase

/** A class representing an agglomerate of libraries with an alias */
class Libraries(libraries: Sequence<LibraryAliased>) : LibraryGroup<LibraryAliased>,
        Map<String, LibraryAliased> by (libraries.map { it.alias to it }.toMap()) {

    constructor(vararg library: LibraryAliased) : this(library.asSequence())
    constructor(libraries: Iterable<LibraryAliased>) : this(libraries.asSequence())

    /** All library aliases of libraries included in this library group */
    val libraryAliases: Set<String> by lazy { keys }

    override val libraries: Collection<LibraryAliased> by lazy { values }

    override val operators: OperatorSet by lazy {
        OperatorSet(libraries.flatMap { it.operators.asSequence() })
    }

    override val theory: ClauseDatabase by lazy {
        ClauseDatabase.of(libraries.flatMap { it.theory.clauses.asSequence() })
    }

    override val primitives: Map<Signature, Primitive> by lazy {
        libraries.flatMap { lib ->
            lib.primitives.entries.asSequence().flatMap {
                sequenceOf(it.toPair(), it.key.copy(name = lib.alias + LibraryAliased.ALIAS_SEPARATOR + it.key.name) to it.value)
            }
        }.toMap()
    }

    override fun plus(library: LibraryAliased): LibraryGroup<LibraryAliased> {
        if (library.alias in libraryAliases) {
            throw AlreadyLoadedLibraryException("A library aliased as `${library.alias}` has already been loaded")
        }

        return Libraries(libraries.asSequence() + sequenceOf(library))
    }

    override fun update(library: LibraryAliased): LibraryGroup<LibraryAliased> {
        if (library.alias !in libraryAliases) {
            throw IllegalArgumentException("A library aliased as `${library.alias}` has never been loaded")
        }

        return Libraries(libraries.asSequence() + sequenceOf(library))
    }
}