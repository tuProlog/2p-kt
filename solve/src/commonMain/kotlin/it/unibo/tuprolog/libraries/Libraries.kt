package it.unibo.tuprolog.libraries

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.theory.ClauseDatabase

class Libraries(libraries: Sequence<LibraryAliased>) : Library,
        Map<String, LibraryAliased> by (libraries.map { it.alias to it }.toMap()) {

    constructor(vararg library: LibraryAliased) : this(library.asSequence())

    constructor(libraries: Iterable<LibraryAliased>) : this(libraries.asSequence())

    val libraries: Collection<LibraryAliased>
        get() = values

    val libraryAliases: Set<String>
        get() = keys

    override val operators: OperatorSet by lazy {
        OperatorSet(libraries.flatMap { it.operators.asSequence() })
    }

    override val theory: ClauseDatabase by lazy {
        ClauseDatabase.of(libraries.flatMap { it.theory.clauses.asSequence() })
    }

    override val primitives: Map<Signature, Primitive> by lazy {
        libraries.flatMap { lib ->
            lib.primitives.entries.asSequence().flatMap {
                sequenceOf(it.toPair(), it.key.copy(name = lib.alias + "." + it.key.name) to it.value)
            }
        }.toMap()
    }

    fun add(library: LibraryAliased): Libraries {
        if (library.alias in libraryAliases) {
            throw AlreadyLoadedLibraryException("A library aliased as `${library.alias}` has already been loaded")
        }

        return Libraries(libraries.asSequence() + sequenceOf(library))
    }

    fun update(library: LibraryAliased): Libraries {
        if (library.alias !in libraryAliases) {
            throw IllegalArgumentException("A library aliased as `${library.alias}` has never been loaded")
        }

        return Libraries(libraries.asSequence() + sequenceOf(library))
    }
}