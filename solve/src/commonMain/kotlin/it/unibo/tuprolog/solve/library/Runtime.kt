package it.unibo.tuprolog.solve.library

import it.unibo.tuprolog.solve.library.impl.RuntimeImpl
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * Represents a group of [Library] objects constituting the runtime a logic solver may leverage upon, keyed by
 * [Library.alias] (as the underlying `Map<String, Library>` reflects).
 *
 * A [it.unibo.tuprolog.solve.Solver] loads exactly one [Runtime] (see [it.unibo.tuprolog.solve.SolverFactory.defaultRuntime]);
 * `Runtime` is what lets several independently-authored [Library] instances (e.g. `:io-lib`, `:oop-lib`, a
 * standard-library `Library`) coexist within a single solver, aliasing being the mechanism that resolves clashing
 * predicate indicators without either library needing to know about the other.
 *
 * Construct one via the companion's [empty]/[of] factories, or combine [Library]/[Runtime] instances with [plus].
 *
 * @see Library
 * @see it.unibo.tuprolog.solve.MutableSolver.setRuntime
 */
interface Runtime :
    Pluggable,
    Map<String, Library> {
    /** The [Library.alias] of every library in this runtime; same as this map's [keys]. */
    @JsName("aliases")
    val aliases: Set<String>

    /** All libraries composing this library group */
    @JsName("libraries")
    val libraries: Set<Library>

    /** Merges every library's [Pluggable.clauses] into a single indexed [Theory], using [unificator]. */
    @JsName("asTheory")
    fun asTheory(unificator: Unificator): Theory

    /**
     * Returns a new [Runtime] with [other] added to [libraries].
     * @throws it.unibo.tuprolog.solve.library.exception.AlreadyLoadedLibraryException if a library aliased as [other]'s [Library.alias] is already loaded.
     */
    @JsName("plusLibrary")
    operator fun plus(other: Library): Runtime

    /**
     * Adds all libraries in provided libraryGroup to this libraryGroup.
     * @throws it.unibo.tuprolog.solve.library.exception.AlreadyLoadedLibraryException if any library in [runtime] shares its [Library.alias] with one already loaded in this runtime.
     */
    @JsName("plusRuntime")
    operator fun plus(runtime: Runtime): Runtime

    /**
     * Removes the library from this library group.
     *
     * __Note__: as currently implemented, this throws [it.unibo.tuprolog.solve.library.exception.NoSuchALibraryException]
     * when [library]'s alias *is* loaded, and silently returns an unchanged [Runtime] when it is *not* -- the
     * opposite of what the exception's name suggests; verify against `RuntimeImpl.minus` before relying on this.
     */
    @JsName("minus")
    operator fun minus(library: Library): Runtime

    /**
     * Updates an already contained library, with given library.
     * @throws IllegalArgumentException if no library aliased as [library]'s [Library.alias] is currently loaded.
     */
    @JsName("update")
    fun update(library: Library): Runtime

    /**
     * Removes the library aliased [alias] from this library group.
     *
     * __Note__: see [minus]'s caveat -- as currently implemented this throws
     * [it.unibo.tuprolog.solve.library.exception.NoSuchALibraryException] when [alias] *is* loaded, and is a no-op
     * when it is *not*.
     */
    @JsName("minusAlias")
    operator fun minus(alias: String): Runtime

    /** Same as [minus] for a single alias, but removes every library aliased by any of [aliases]. */
    @JsName("minusAliases")
    operator fun minus(aliases: Iterable<String>): Runtime

    companion object {
        /** An empty [Runtime], with no loaded libraries. */
        @JsName("empty")
        @JvmStatic
        fun empty(): Runtime = RuntimeImpl(emptySequence())

        /**
         * Creates a [Runtime] out of the given [library] instances. Note that, unlike [plus], this does *not*
         * reject clashing aliases: if two of them share the same [Library.alias], the later one silently wins.
         */
        @JsName("of")
        @JvmStatic
        fun of(vararg library: Library): Runtime = RuntimeImpl(sequenceOf(*library))

        /** Same as [of], for an [Iterable] of libraries. */
        @JsName("ofIterable")
        @JvmStatic
        fun of(libraries: Iterable<Library>): Runtime = RuntimeImpl(libraries.asSequence())

        /** Same as [of], for a [Sequence] of libraries. */
        @JsName("ofSequence")
        @JvmStatic
        fun of(libraries: Sequence<Library>): Runtime = RuntimeImpl(libraries)
    }
}
