package it.unibo.tuprolog.solve.library

import it.unibo.tuprolog.solve.library.impl.RuntimeImpl
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/** Represents a group of [Library] objects constituting the runtime a logic solver may leverage upon */
interface Runtime : Pluggable, Map<String, Library> {

    @JsName("aliases")
    val aliases: Set<String>

    /** All libraries composing this library group */
    @JsName("libraries")
    val libraries: Set<Library>

    @JsName("asTheory")
    fun asTheory(unificator: Unificator): Theory

    @JsName("plusLibrary")
    operator fun plus(other: Library): Runtime

    /** Adds all libraries in provided libraryGroup to this libraryGroup */
    @JsName("plusRuntime")
    operator fun plus(runtime: Runtime): Runtime

    /** Removes the library from this library group */
    @JsName("minus")
    operator fun minus(library: Library): Runtime

    /** Updates an already contained library, with given library */
    @JsName("update")
    fun update(library: Library): Runtime

    @JsName("minusAlias")
    operator fun minus(alias: String): Runtime

    @JsName("minusAliases")
    operator fun minus(aliases: Iterable<String>): Runtime

    companion object {
        @JsName("empty")
        @JvmStatic
        fun empty(): Runtime = RuntimeImpl(emptySequence())

        @JsName("of")
        @JvmStatic
        fun of(vararg library: Library): Runtime = RuntimeImpl(sequenceOf(*library))

        @JsName("ofIterable")
        @JvmStatic
        fun of(libraries: Iterable<Library>): Runtime = RuntimeImpl(libraries.asSequence())

        @JsName("ofSequence")
        @JvmStatic
        fun of(libraries: Sequence<Library>): Runtime = RuntimeImpl(libraries)
    }
}
