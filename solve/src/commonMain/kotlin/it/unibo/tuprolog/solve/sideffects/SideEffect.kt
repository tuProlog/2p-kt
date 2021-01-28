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
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.toOperatorSet
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.Theory

abstract class SideEffect {

    abstract fun applyTo(context: ExecutionContext): ExecutionContext

    abstract class SetClausesOfKb(open val clauses: Iterable<Clause>) : SideEffect() {
        val theory: Theory by lazy {
            clauses.let {
                if (it is Theory) { it } else { Theory.indexedOf(it) }
            }
        }

        val mutableTheory: Theory by lazy {
            clauses.let {
                when (it) {
                    is MutableTheory -> it
                    is Theory -> it.toMutableTheory()
                    else -> MutableTheory.indexedOf(it)
                }
            }
        }
    }

    abstract class AddClausesToKb(clauses: Iterable<Clause>, open val onTop: Boolean) : SetClausesOfKb(clauses)

    abstract class RemoveClausesFromKb(clauses: Iterable<Clause>) : SetClausesOfKb(clauses)

    data class ResetStaticKb(override val clauses: Iterable<Clause>) : SetClausesOfKb(clauses) {
        constructor(vararg clauses: Clause) : this(listOf(*clauses))

        constructor(clauses: Sequence<Clause>) : this(clauses.asIterable())

        override fun applyTo(context: ExecutionContext): ExecutionContext = context.update(staticKb = theory)
    }

    data class AddStaticClauses(
        override val clauses: Iterable<Clause>,
        override val onTop: Boolean = false
    ) : AddClausesToKb(clauses, onTop) {
        constructor(vararg clauses: Clause, onTop: Boolean = false) : this(listOf(*clauses), onTop)

        constructor(clauses: Sequence<Clause>, onTop: Boolean = false) : this(clauses.asIterable(), onTop)

        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(staticKb = context.staticKb.let { if (onTop) it.assertA(clauses) else it.assertZ(clauses) })
    }

    data class RemoveStaticClauses(override val clauses: Iterable<Clause>) : RemoveClausesFromKb(clauses) {
        constructor(vararg clauses: Clause) : this(listOf(*clauses))

        constructor(clauses: Sequence<Clause>) : this(clauses.asIterable())

        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(staticKb = context.staticKb.retract(clauses).theory)
    }

    data class ResetDynamicKb(override val clauses: Iterable<Clause>) : SetClausesOfKb(clauses) {
        constructor(vararg clauses: Clause) : this(listOf(*clauses))

        constructor(clauses: Sequence<Clause>) : this(clauses.asIterable())

        override fun applyTo(context: ExecutionContext): ExecutionContext = context.update(dynamicKb = mutableTheory)
    }

    data class AddDynamicClauses(
        override val clauses: Iterable<Clause>,
        override val onTop: Boolean = false
    ) : AddClausesToKb(clauses, onTop) {
        constructor(vararg clauses: Clause, onTop: Boolean = false) : this(listOf(*clauses), onTop)

        constructor(clauses: Sequence<Clause>, onTop: Boolean = false) : this(clauses.asIterable(), onTop)

        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(
                dynamicKb = context.dynamicKb.let {
                    if (onTop) it.assertA(clauses) else it.assertZ(clauses)
                }.toMutableTheory()
            )
    }

    data class RemoveDynamicClauses(override val clauses: Iterable<Clause>) : RemoveClausesFromKb(clauses) {
        constructor(vararg clauses: Clause) : this(listOf(*clauses))

        constructor(clauses: Sequence<Clause>) : this(clauses.asIterable())

        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(dynamicKb = context.dynamicKb.retract(clauses).theory.toMutableTheory())
    }

    abstract class AlterFlags : SideEffect()

    abstract class AlterFlagsByEntries(open val flags: Map<String, Term>) : AlterFlags() {
        val flagStore: FlagStore by lazy {
            flags.let {
                if (it is FlagStore) { it } else { FlagStore.of(it) }
            }
        }
    }

    abstract class AlterFlagsByName(open val names: Iterable<String>) : AlterFlags()

    data class SetFlags(override val flags: Map<String, Term>) : AlterFlagsByEntries(flags) {
        constructor(vararg flags: Pair<String, Term>) : this(listOf(*flags))

        constructor(flags: Iterable<Pair<String, Term>>) : this(flags.toMap())

        constructor(flags: Sequence<Pair<String, Term>>) : this(flags.toMap())

        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(flags = context.flags + flags)
    }

    data class ResetFlags(override val flags: Map<String, Term>) : AlterFlagsByEntries(flags) {
        constructor(vararg flags: Pair<String, Term>) : this(listOf(*flags))

        constructor(flags: Iterable<Pair<String, Term>>) : this(flags.toMap())

        constructor(flags: Sequence<Pair<String, Term>>) : this(flags.toMap())

        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(flags = flagStore)
    }

    data class ClearFlags(override val names: Iterable<String>) : AlterFlagsByName(names) {
        constructor(vararg names: String) : this(listOf(*names))

        constructor(names: Sequence<String>) : this(names.toList())

        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(flags = context.flags - names)
    }

    abstract class AlterLibraries : SideEffect()

    abstract class AlterAliasedLibraries : AlterLibraries() {
        abstract val libraries: Libraries
    }

    abstract class AlterLibrary(open val alias: String, open val library: Library) : AlterAliasedLibraries() {
        init {
            when (val lib = library) {
                is AliasedLibrary -> require(lib.alias == alias)
            }
        }

        val aliasedLibrary: AliasedLibrary by lazy {
            library.let {
                if (it is AliasedLibrary) { it } else { Library.of(it, alias) }
            }
        }

        override val libraries: Libraries by lazy { Libraries.of(aliasedLibrary) }
    }

    abstract class AlterLibrariesByName(open val aliases: Iterable<String>) : AlterLibraries()

    data class LoadLibrary(override val alias: String, override val library: Library) : AlterLibrary(alias, library) {
        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(libraries = context.libraries + aliasedLibrary)
    }

    data class UnloadLibraries(override val aliases: Iterable<String>) : AlterLibrariesByName(aliases) {
        constructor(aliases: List<String>) : this(aliases.asIterable())

        constructor(aliases: Sequence<String>) : this(aliases.asIterable())

        constructor(vararg aliases: String) : this(listOf(*aliases))

        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(libraries = context.libraries - aliases)
    }

    data class UpdateLibrary(override val alias: String, override val library: Library) : AlterLibrary(alias, library) {
        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(libraries = context.libraries.update(aliasedLibrary))
    }

    data class AddLibraries(override val libraries: Libraries) : AlterAliasedLibraries() {
        constructor(libraries: Iterable<AliasedLibrary>) : this(Libraries.of(libraries))

        constructor(libraries: Sequence<AliasedLibrary>) : this(Libraries.of(libraries))

        constructor(vararg libraries: AliasedLibrary) : this(Libraries.of(*libraries))

        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(libraries = context.libraries + libraries)
    }

    data class ResetLibraries(override val libraries: Libraries) : AlterAliasedLibraries() {
        constructor(libraries: Iterable<AliasedLibrary>) : this(Libraries.of(libraries))

        constructor(libraries: Sequence<AliasedLibrary>) : this(Libraries.of(libraries))

        constructor(vararg libraries: AliasedLibrary) : this(Libraries.of(*libraries))

        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(libraries = libraries)
    }

    abstract class AlterOperators(open val operators: Iterable<Operator>) : SideEffect() {
        val operatorSet: OperatorSet by lazy {
            operators.let { if (it is OperatorSet) { it } else { OperatorSet(it) } }
        }
    }

    data class SetOperators(override val operators: Iterable<Operator>) : AlterOperators(operators) {
        constructor(vararg operators: Operator) : this(listOf(*operators))

        constructor(operators: Sequence<Operator>) : this(operators.toOperatorSet())

        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(operators = context.operators + operatorSet)
    }

    data class ResetOperators(override val operators: Iterable<Operator>) : AlterOperators(operators) {
        constructor(vararg operators: Operator) : this(listOf(*operators))

        constructor(operators: Sequence<Operator>) : this(operators.toOperatorSet())

        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(operators = context.operators + operatorSet)
    }

    data class RemoveOperators(override val operators: Iterable<Operator>) : AlterOperators(operators) {
        constructor(vararg operators: Operator) : this(listOf(*operators))

        constructor(operators: Sequence<Operator>) : this(operators.toOperatorSet())

        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(operators = context.operators - operatorSet)
    }

    abstract class AlterChannels : SideEffect()

    abstract class AlterChannelsByName(open val names: Iterable<String>) : AlterChannels()

    abstract class AlterInputChannels(open val inputChannels: Map<String, InputChannel<String>>) : AlterChannels()

    abstract class AlterOutputChannels(open val outputChannels: Map<String, OutputChannel<String>>) : AlterChannels()

    data class OpenInputChannels(
        override val inputChannels: Map<String, InputChannel<String>>
    ) : AlterInputChannels(inputChannels) {
        constructor(vararg inputChannels: Pair<String, InputChannel<String>>) : this(listOf(*inputChannels))

        constructor(inputChannels: Iterable<Pair<String, InputChannel<String>>>) : this(inputChannels.toMap())

        constructor(inputChannels: Sequence<Pair<String, InputChannel<String>>>) : this(inputChannels.toMap())

        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(inputChannels = context.inputChannels + inputChannels)
    }

    data class ResetInputChannels(
        override val inputChannels: Map<String, InputChannel<String>>
    ) : AlterInputChannels(inputChannels) {
        constructor(vararg inputChannels: Pair<String, InputChannel<String>>) : this(listOf(*inputChannels))

        constructor(inputChannels: Iterable<Pair<String, InputChannel<String>>>) : this(inputChannels.toMap())

        constructor(inputChannels: Sequence<Pair<String, InputChannel<String>>>) : this(inputChannels.toMap())

        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(inputChannels = InputStore.of(inputChannels))
    }

    data class CloseInputChannels(override val names: Iterable<String>) : AlterChannelsByName(names) {
        constructor(vararg names: String) : this(listOf(*names))

        constructor(names: Sequence<String>) : this(names.toList())

        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(inputChannels = context.inputChannels - names)
    }

    data class OpenOutputChannels(
        override val outputChannels: Map<String, OutputChannel<String>>
    ) : AlterOutputChannels(outputChannels) {
        constructor(vararg outputChannels: Pair<String, OutputChannel<String>>) : this(listOf(*outputChannels))

        constructor(outputChannels: Iterable<Pair<String, OutputChannel<String>>>) : this(outputChannels.toMap())

        constructor(outputChannels: Sequence<Pair<String, OutputChannel<String>>>) : this(outputChannels.toMap())

        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(outputChannels = context.outputChannels + outputChannels)
    }

    data class ResetOutputChannels(
        override val outputChannels: Map<String, OutputChannel<String>>
    ) : AlterOutputChannels(outputChannels) {
        constructor(vararg outputChannels: Pair<String, OutputChannel<String>>) : this(listOf(*outputChannels))

        constructor(outputChannels: Iterable<Pair<String, OutputChannel<String>>>) : this(outputChannels.toMap())

        constructor(outputChannels: Sequence<Pair<String, OutputChannel<String>>>) : this(outputChannels.toMap())

        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(outputChannels = OutputStore.of(outputChannels))
    }

    data class CloseOutputChannels(override val names: Iterable<String>) : AlterChannelsByName(names) {
        constructor(vararg names: String) : this(listOf(*names))

        constructor(names: Sequence<String>) : this(names.toList())

        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(outputChannels = context.outputChannels - names)
    }

    abstract class AlterCustomData(open val data: Map<String, Any>, open val reset: Boolean = false) : SideEffect()

    data class SetDurableData(
        override val data: Map<String, Any>,
        override val reset: Boolean = false
    ) : AlterCustomData(data, reset) {
        constructor(key: String, value: Any, reset: Boolean = false) : this(listOf(key to value), reset)

        constructor(vararg data: Pair<String, Any>, reset: Boolean = false) : this(listOf(*data), reset)

        constructor(data: Iterable<Pair<String, Any>>, reset: Boolean = false) : this(data.toMap(), reset)

        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(durableData = if (reset) { data } else { context.durableData + data })
    }

    data class SetEphemeralData(
        override val data: Map<String, Any>,
        override val reset: Boolean = false
    ) : AlterCustomData(data, reset) {
        constructor(key: String, value: Any, reset: Boolean = false) : this(listOf(key to value), reset)

        constructor(vararg data: Pair<String, Any>, reset: Boolean = false) : this(listOf(*data), reset)

        constructor(data: Iterable<Pair<String, Any>>, reset: Boolean = false) : this(data.toMap(), reset)

        override fun applyTo(context: ExecutionContext): ExecutionContext =
            context.update(ephemeralData = if (reset) { data } else { context.ephemeralData + data })
    }
}
