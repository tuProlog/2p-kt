package it.unibo.tuprolog.solve.library.impl

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.function.LogicFunction
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.library.exception.AlreadyLoadedLibraryException
import it.unibo.tuprolog.solve.library.exception.NoSuchALibraryException
import it.unibo.tuprolog.solve.primitive.Primitive
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator
import it.unibo.tuprolog.utils.Cache

/** A class representing an agglomerate of libraries with an alias */
internal class RuntimeImpl(private val delegate: Map<String, Library>) :
    Runtime,
    AbstractPluggable(),
    Map<String, Library> by delegate {

    constructor(libraries: Sequence<Library>) : this(libraries.map { it.alias to it }.toMap())

    /** All aliases of all libraries included in this library group */
    override val aliases: Set<String>
        get() = keys

    override val libraries: Set<Library>
        get() = values.toSet()

    private val theoryCache: Cache<Unificator, Theory> = Cache.simpleLru(1)

    override fun asTheory(unificator: Unificator): Theory =
        theoryCache.getOrSet(unificator) {
            Theory.indexedOf(unificator, clauses)
        }

    override val operators: OperatorSet by lazy {
        OperatorSet(libraries.flatMap { it.operators.asSequence() })
    }

    override val clauses: List<Clause>
        get() = libraries.flatMap { it.clauses }

    override val primitives: Map<Signature, Primitive> by lazy {
        libraries.flatMap { lib ->
            lib.primitives.entries.asSequence().flatMap {
                sequenceOf(
                    it.toPair(),
                    it.key.copy(name = lib.alias + Library.ALIAS_SEPARATOR + it.key.name) to it.value
                )
            }
        }.toMap()
    }

    override val functions: Map<Signature, LogicFunction> by lazy {
        libraries.flatMap { lib ->
            lib.functions.entries.asSequence().flatMap {
                sequenceOf(
                    it.toPair(),
                    it.key.copy(name = lib.alias + Library.ALIAS_SEPARATOR + it.key.name) to it.value
                )
            }
        }.toMap()
    }

    override fun plus(other: Library): Runtime {
        return aliases.find { other.alias in aliases }
            ?.let { alreadyLoadedError(other) }
            ?: RuntimeImpl(libraries.asSequence() + sequenceOf(other))
    }

    override fun plus(runtime: Runtime): RuntimeImpl =
        runtime.libraries.find { it.alias in aliases }
            ?.let { alreadyLoadedError(it) }
            ?: RuntimeImpl(libraries.asSequence() + runtime.libraries.asSequence())

    override fun minus(library: Library): RuntimeImpl {
        if (library.alias in aliases) {
            noSuchALibraryError(library)
        }
        return RuntimeImpl(libraries.asSequence().filter { it.alias != library.alias })
    }

    override operator fun minus(alias: String): RuntimeImpl {
        if (alias in aliases) {
            noSuchALibraryError(alias)
        }
        return RuntimeImpl(libraries.asSequence().filter { it.alias != alias })
    }

    override operator fun minus(aliases: Iterable<String>): RuntimeImpl {
        val toBeRemoved = aliases.map {
            if (it in this.aliases) {
                noSuchALibraryError(it)
            }
            it
        }.toSet()
        return RuntimeImpl(libraries.asSequence().filterNot { it.alias in toBeRemoved })
    }

    override fun update(library: Library): RuntimeImpl =
        aliases.find { library.alias in aliases }
            ?.let { RuntimeImpl(libraries.asSequence() + sequenceOf(library)) }
            ?: throw IllegalArgumentException("A library aliased as `${library.alias}` has never been loaded")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as RuntimeImpl

        if (delegate != other.delegate) return false

        return true
    }

    override fun hashCode(): Int = libraries.hashCode()

    override fun toString(): String = "RuntimeImpl($libraries)"

    @Suppress("NOTHING_TO_INLINE")
    companion object {
        /** Utility function to handle already loaded error */
        private inline fun alreadyLoadedError(library: Library): Nothing =
            throw AlreadyLoadedLibraryException("A library aliased as `${library.alias}` has already been loaded")

        private inline fun noSuchALibraryError(library: Library): Nothing =
            noSuchALibraryError(library.alias)

        private inline fun noSuchALibraryError(alias: String): Nothing =
            throw NoSuchALibraryException("No library with alias `$alias` has been loaded")
    }
}
