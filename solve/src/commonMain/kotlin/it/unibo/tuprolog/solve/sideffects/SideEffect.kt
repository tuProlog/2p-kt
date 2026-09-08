package it.unibo.tuprolog.solve.sideffects

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.toOperatorSet
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.Theory

/**
 * A state change to be applied to an [ExecutionContext] after a
 * [it.unibo.tuprolog.solve.primitive.Primitive] has run, rather than mutated directly by the primitive itself.
 * Primitives attach zero or more [SideEffect]s to their [it.unibo.tuprolog.solve.primitive.Solve.Response]
 * (see [it.unibo.tuprolog.solve.primitive.Solve.Request.replyWith]), and the resolution strategy folds them, in
 * order, over the context via [ExecutionContext.apply] -- each [applyTo] call producing a new, immutable
 * [ExecutionContext] rather than mutating the one it receives.
 *
 * Every concrete [SideEffect] subtype nested here covers one specific piece of mutable state a solver carries
 * (clauses in either knowledge base, flags, libraries, operators, I/O channels, custom data), typically in both a
 * "reset to" and "alter by adding/removing" flavour. [SideEffectFactory]/[SideEffectsBuilder] are the ergonomic way
 * to construct these without naming each subtype explicitly.
 *
 * @see it.unibo.tuprolog.solve.primitive.Solve.Response.sideEffects
 */
abstract class SideEffect {
    /** Produces the [ExecutionContext] resulting from applying this side effect to [context]. */
    abstract fun applyTo(context: ExecutionContext): ExecutionContext

    /** Base class for side effects computing a [Theory] (or [MutableTheory]) out of [clauses], for either knowledge base. */
    abstract class SetClausesOfKb(
        open val clauses: Iterable<Clause>,
    ) : SideEffect() {
        /** [clauses] as a [Theory], reusing it as-is if it already is one. */
        fun theory(context: ExecutionContext): Theory =
            clauses.let {
                if (it is Theory) {
                    it
                } else {
                    Theory.indexedOf(context.unificator, it)
                }
            }

        /** [clauses] as a [MutableTheory], converting it if necessary. */
        fun mutableTheory(context: ExecutionContext): Theory =
            clauses.let {
                when (it) {
                    is MutableTheory -> it
                    is Theory -> it.toMutableTheory()
                    else -> MutableTheory.indexedOf(context.unificator, it)
                }
            }
    }

    /** Base class for side effects adding [clauses] to a knowledge base, either at the front ([onTop]) or the back. */
    abstract class AddClausesToKb(
        clauses: Iterable<Clause>,
        open val onTop: Boolean,
    ) : SetClausesOfKb(clauses)

    /** Base class for side effects removing [clauses] from a knowledge base. */
    abstract class RemoveClausesFromKb(
        clauses: Iterable<Clause>,
    ) : SetClausesOfKb(clauses)

    /** Replaces the static knowledge base with a [Theory] built out of [clauses]. */
    data class ResetStaticKb(
        override val clauses: Iterable<Clause>,
    ) : SetClausesOfKb(clauses) {
        constructor(vararg clauses: Clause) : this(listOf(*clauses))

        constructor(clauses: Sequence<Clause>) : this(clauses.asIterable())

        override fun applyTo(context: ExecutionContext): ExecutionContext = context.update(staticKb = theory(context))
    }

    /** Adds [clauses] to the static knowledge base, at the front if [onTop], at the back otherwise. */
    data class AddStaticClauses(
        override val clauses: Iterable<Clause>,
        override val onTop: Boolean = false,
    ) : AddClausesToKb(clauses, onTop) {
        constructor(vararg clauses: Clause, onTop: Boolean = false) : this(listOf(*clauses), onTop)

        constructor(clauses: Sequence<Clause>, onTop: Boolean = false) : this(clauses.asIterable(), onTop)

        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(staticKb = context.staticKb.let { if (onTop) it.assertA(clauses) else it.assertZ(clauses) })
    }

    /** Removes [clauses] from the static knowledge base. */
    data class RemoveStaticClauses(
        override val clauses: Iterable<Clause>,
    ) : RemoveClausesFromKb(clauses) {
        constructor(vararg clauses: Clause) : this(listOf(*clauses))

        constructor(clauses: Sequence<Clause>) : this(clauses.asIterable())

        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(staticKb = context.staticKb.retract(clauses).theory)
    }

    /** Replaces the dynamic knowledge base with a [MutableTheory] built out of [clauses]. */
    data class ResetDynamicKb(
        override val clauses: Iterable<Clause>,
    ) : SetClausesOfKb(clauses) {
        constructor(vararg clauses: Clause) : this(listOf(*clauses))

        constructor(clauses: Sequence<Clause>) : this(clauses.asIterable())

        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(
                dynamicKb = mutableTheory(context),
            )
    }

    /** Adds [clauses] to the dynamic knowledge base, at the front if [onTop], at the back otherwise. */
    data class AddDynamicClauses(
        override val clauses: Iterable<Clause>,
        override val onTop: Boolean = false,
    ) : AddClausesToKb(clauses, onTop) {
        constructor(vararg clauses: Clause, onTop: Boolean = false) : this(listOf(*clauses), onTop)

        constructor(clauses: Sequence<Clause>, onTop: Boolean = false) : this(clauses.asIterable(), onTop)

        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(
                dynamicKb =
                    context.dynamicKb
                        .let {
                            if (onTop) it.assertA(clauses) else it.assertZ(clauses)
                        }.toMutableTheory(),
            )
    }

    /** Removes [clauses] from the dynamic knowledge base. */
    data class RemoveDynamicClauses(
        override val clauses: Iterable<Clause>,
    ) : RemoveClausesFromKb(clauses) {
        constructor(vararg clauses: Clause) : this(listOf(*clauses))

        constructor(clauses: Sequence<Clause>) : this(clauses.asIterable())

        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(
                dynamicKb =
                    context.dynamicKb
                        .retract(clauses)
                        .theory
                        .toMutableTheory(),
            )
    }

    /** Base class for side effects altering the [FlagStore]. */
    abstract class AlterFlags : SideEffect()

    /** Base class for [AlterFlags] side effects carrying explicit name-value [flags] entries. */
    abstract class AlterFlagsByEntries(
        open val flags: Map<String, Term>,
    ) : AlterFlags() {
        /** [flags], normalized to a [FlagStore]. */
        val flagStore: FlagStore by lazy {
            flags.let {
                if (it is FlagStore) {
                    it
                } else {
                    FlagStore.of(it)
                }
            }
        }
    }

    /** Base class for [AlterFlags] side effects addressing flags by [names] only (e.g. to clear them). */
    abstract class AlterFlagsByName(
        open val names: Iterable<String>,
    ) : AlterFlags()

    /** Merges [flags] into the current [FlagStore], overriding any existing entry with the same name. */
    data class SetFlags(
        override val flags: Map<String, Term>,
    ) : AlterFlagsByEntries(flags) {
        constructor(vararg flags: Pair<String, Term>) : this(listOf(*flags))

        constructor(flags: Iterable<Pair<String, Term>>) : this(flags.toMap())

        constructor(flags: Sequence<Pair<String, Term>>) : this(flags.toMap())

        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(
                flags = context.flags + flags,
            )
    }

    /** Replaces the whole [FlagStore] with one built out of [flags]. */
    data class ResetFlags(
        override val flags: Map<String, Term>,
    ) : AlterFlagsByEntries(flags) {
        constructor(vararg flags: Pair<String, Term>) : this(listOf(*flags))

        constructor(flags: Iterable<Pair<String, Term>>) : this(flags.toMap())

        constructor(flags: Sequence<Pair<String, Term>>) : this(flags.toMap())

        override fun applyTo(context: ExecutionContext): ExecutionContext = context.update(flags = flagStore)
    }

    /** Removes the flags named in [names] from the [FlagStore]. */
    data class ClearFlags(
        override val names: Iterable<String>,
    ) : AlterFlagsByName(names) {
        constructor(vararg names: String) : this(listOf(*names))

        constructor(names: Sequence<String>) : this(names.toList())

        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(
                flags = context.flags - names,
            )
    }

    /** Base class for side effects altering the loaded [Runtime] of libraries. */
    abstract class AlterRuntime : SideEffect()

    /** Base class for [AlterRuntime] side effects expressible as a [Runtime] (a set of aliased [Library]s). */
    abstract class AlterAliasedRuntime : AlterRuntime() {
        /** The [Runtime] this side effect carries. */
        abstract val libraries: Runtime
    }

    /** Base class for [AlterRuntime] side effects concerning a single [library]. */
    abstract class AlterLibrary(
        open val library: Library,
    ) : AlterAliasedRuntime() {
        override val libraries: Runtime by lazy { Runtime.of(library) }
    }

    /** Base class for [AlterRuntime] side effects addressing libraries by [aliases] only. */
    abstract class AlterLibrariesByName(
        open val aliases: Iterable<String>,
    ) : AlterRuntime()

    /** Adds [library] to the loaded [Runtime]. */
    data class LoadLibrary(
        override val library: Library,
    ) : AlterLibrary(library) {
        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(libraries = context.libraries + library)
    }

    /** Removes the libraries named in [aliases] from the loaded [Runtime]. */
    data class UnloadLibraries(
        override val aliases: Iterable<String>,
    ) : AlterLibrariesByName(aliases) {
        constructor(aliases: List<String>) : this(aliases.asIterable())

        constructor(aliases: Sequence<String>) : this(aliases.asIterable())

        constructor(vararg aliases: String) : this(listOf(*aliases))

        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(
                libraries = context.libraries - aliases,
            )
    }

    /** Replaces, within the loaded [Runtime], the library sharing [library]'s alias with [library] itself. */
    data class UpdateLibrary(
        override val library: Library,
    ) : AlterLibrary(library) {
        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(libraries = context.libraries.update(library))
    }

    /** Adds every library in [libraries] to the loaded [Runtime]. */
    data class AddLibraries(
        override val libraries: Runtime,
    ) : AlterAliasedRuntime() {
        constructor(libraries: Iterable<Library>) : this(Runtime.of(libraries))

        constructor(libraries: Sequence<Library>) : this(Runtime.of(libraries))

        constructor(vararg libraries: Library) : this(Runtime.of(*libraries))

        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(
                libraries = context.libraries + libraries,
            )
    }

    /** Replaces the whole loaded [Runtime] with [libraries]. */
    data class ResetRuntime(
        override val libraries: Runtime,
    ) : AlterAliasedRuntime() {
        constructor(libraries: Iterable<Library>) : this(Runtime.of(libraries))

        constructor(libraries: Sequence<Library>) : this(Runtime.of(libraries))

        constructor(vararg libraries: Library) : this(Runtime.of(*libraries))

        override fun applyTo(context: ExecutionContext): ExecutionContext = context.update(libraries = libraries)
    }

    /** Base class for side effects altering the declared [OperatorSet]. */
    abstract class AlterOperators(
        open val operators: Iterable<Operator>,
    ) : SideEffect() {
        /** [operators], normalized to an [OperatorSet]. */
        val operatorSet: OperatorSet by lazy {
            operators.let {
                if (it is OperatorSet) {
                    it
                } else {
                    OperatorSet(it)
                }
            }
        }
    }

    /** Merges [operators] into the current [OperatorSet]. */
    data class SetOperators(
        override val operators: Iterable<Operator>,
    ) : AlterOperators(operators) {
        constructor(vararg operators: Operator) : this(listOf(*operators))

        constructor(operators: Sequence<Operator>) : this(operators.toOperatorSet())

        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(
                operators = context.operators + operatorSet,
            )
    }

    /**
     * Nominally replaces the whole [OperatorSet] with [operators].
     *
     * @implNote as of this writing, [applyTo] actually merges [operators] into the existing set (same as
     * [SetOperators]) rather than replacing it outright — likely a bug, since every sibling `Reset*` side effect
     * in this file does replace the corresponding piece of state.
     */
    data class ResetOperators(
        override val operators: Iterable<Operator>,
    ) : AlterOperators(operators) {
        constructor(vararg operators: Operator) : this(listOf(*operators))

        constructor(operators: Sequence<Operator>) : this(operators.toOperatorSet())

        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(
                operators = context.operators + operatorSet,
            )
    }

    /** Removes [operators] from the current [OperatorSet]. */
    data class RemoveOperators(
        override val operators: Iterable<Operator>,
    ) : AlterOperators(operators) {
        constructor(vararg operators: Operator) : this(listOf(*operators))

        constructor(operators: Sequence<Operator>) : this(operators.toOperatorSet())

        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(
                operators = context.operators - operatorSet,
            )
    }

    /** Base class for side effects altering the I/O channel stores. */
    abstract class AlterChannels : SideEffect()

    /** Base class for [AlterChannels] side effects addressing channels by [names] only (e.g. to close them). */
    abstract class AlterChannelsByName(
        open val names: Iterable<String>,
    ) : AlterChannels()

    /** Base class for [AlterChannels] side effects carrying named [inputChannels] to add/set. */
    abstract class AlterInputChannels(
        open val inputChannels: Map<String, InputChannel<String>>,
    ) : AlterChannels()

    /** Base class for [AlterChannels] side effects carrying named [outputChannels] to add/set. */
    abstract class AlterOutputChannels(
        open val outputChannels: Map<String, OutputChannel<String>>,
    ) : AlterChannels()

    /** Adds [inputChannels] to the input channel store (see [it.unibo.tuprolog.solve.channel.InputStore]). */
    data class OpenInputChannels(
        override val inputChannels: Map<String, InputChannel<String>>,
    ) : AlterInputChannels(inputChannels) {
        constructor(vararg inputChannels: Pair<String, InputChannel<String>>) : this(listOf(*inputChannels))

        constructor(inputChannels: Iterable<Pair<String, InputChannel<String>>>) : this(inputChannels.toMap())

        constructor(inputChannels: Sequence<Pair<String, InputChannel<String>>>) : this(inputChannels.toMap())

        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(inputChannels = context.inputChannels + inputChannels)
    }

    /** Replaces the whole input channel store with [inputChannels]. */
    data class ResetInputChannels(
        override val inputChannels: Map<String, InputChannel<String>>,
    ) : AlterInputChannels(inputChannels) {
        constructor(vararg inputChannels: Pair<String, InputChannel<String>>) : this(listOf(*inputChannels))

        constructor(inputChannels: Iterable<Pair<String, InputChannel<String>>>) : this(inputChannels.toMap())

        constructor(inputChannels: Sequence<Pair<String, InputChannel<String>>>) : this(inputChannels.toMap())

        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(
                inputChannels = InputStore.of(inputChannels),
            )
    }

    /** Closes/removes the input channels named in [names]. */
    data class CloseInputChannels(
        override val names: Iterable<String>,
    ) : AlterChannelsByName(names) {
        constructor(vararg names: String) : this(listOf(*names))

        constructor(names: Sequence<String>) : this(names.toList())

        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(
                inputChannels = context.inputChannels - names,
            )
    }

    /** Adds [outputChannels] to the output channel store (see [it.unibo.tuprolog.solve.channel.OutputStore]). */
    data class OpenOutputChannels(
        override val outputChannels: Map<String, OutputChannel<String>>,
    ) : AlterOutputChannels(outputChannels) {
        constructor(vararg outputChannels: Pair<String, OutputChannel<String>>) : this(listOf(*outputChannels))

        constructor(outputChannels: Iterable<Pair<String, OutputChannel<String>>>) : this(outputChannels.toMap())

        constructor(outputChannels: Sequence<Pair<String, OutputChannel<String>>>) : this(outputChannels.toMap())

        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(outputChannels = context.outputChannels + outputChannels)
    }

    /** Replaces the whole output channel store with [outputChannels]. */
    data class ResetOutputChannels(
        override val outputChannels: Map<String, OutputChannel<String>>,
    ) : AlterOutputChannels(outputChannels) {
        constructor(vararg outputChannels: Pair<String, OutputChannel<String>>) : this(listOf(*outputChannels))

        constructor(outputChannels: Iterable<Pair<String, OutputChannel<String>>>) : this(outputChannels.toMap())

        constructor(outputChannels: Sequence<Pair<String, OutputChannel<String>>>) : this(outputChannels.toMap())

        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(
                outputChannels = OutputStore.of(outputChannels),
            )
    }

    /** Closes/removes the output channels named in [names]. */
    data class CloseOutputChannels(
        override val names: Iterable<String>,
    ) : AlterChannelsByName(names) {
        constructor(vararg names: String) : this(listOf(*names))

        constructor(names: Sequence<String>) : this(names.toList())

        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(
                outputChannels = context.outputChannels - names,
            )
    }

    /**
     * Base class for side effects altering one [it.unibo.tuprolog.solve.data.CustomDataStore] tier with [data],
     * either merging it in, or replacing the tier outright if [reset].
     */
    abstract class AlterCustomData(
        open val data: Map<String, Any>,
        open val reset: Boolean = false,
    ) : SideEffect()

    /** Alters the [it.unibo.tuprolog.solve.data.CustomDataStore.persistent] tier with [data]. */
    data class SetPersistentData(
        override val data: Map<String, Any>,
        override val reset: Boolean = false,
    ) : AlterCustomData(data, reset) {
        constructor(key: String, value: Any, reset: Boolean = false) : this(listOf(key to value), reset)

        constructor(vararg data: Pair<String, Any>, reset: Boolean = false) : this(listOf(*data), reset)

        constructor(data: Iterable<Pair<String, Any>>, reset: Boolean = false) : this(data.toMap(), reset)

        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(
                customData =
                    context.customData.copy(
                        persistent =
                            if (reset) {
                                data
                            } else {
                                context.customData.persistent + data
                            },
                    ),
            )
    }

    /** Alters the [it.unibo.tuprolog.solve.data.CustomDataStore.durable] tier with [data]. */
    data class SetDurableData(
        override val data: Map<String, Any>,
        override val reset: Boolean = false,
    ) : AlterCustomData(data, reset) {
        constructor(key: String, value: Any, reset: Boolean = false) : this(listOf(key to value), reset)

        constructor(vararg data: Pair<String, Any>, reset: Boolean = false) : this(listOf(*data), reset)

        constructor(data: Iterable<Pair<String, Any>>, reset: Boolean = false) : this(data.toMap(), reset)

        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(
                customData =
                    context.customData.copy(
                        durable =
                            if (reset) {
                                data
                            } else {
                                context.customData.durable + data
                            },
                    ),
            )
    }

    /** Alters the [it.unibo.tuprolog.solve.data.CustomDataStore.ephemeral] tier with [data]. */
    data class SetEphemeralData(
        override val data: Map<String, Any>,
        override val reset: Boolean = false,
    ) : AlterCustomData(data, reset) {
        constructor(key: String, value: Any, reset: Boolean = false) : this(listOf(key to value), reset)

        constructor(vararg data: Pair<String, Any>, reset: Boolean = false) : this(listOf(*data), reset)

        constructor(data: Iterable<Pair<String, Any>>, reset: Boolean = false) : this(data.toMap(), reset)

        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(
                customData =
                    context.customData.copy(
                        ephemeral =
                            if (reset) {
                                data
                            } else {
                                context.customData.ephemeral + data
                            },
                    ),
            )
    }
}
