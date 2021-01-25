package it.unibo.tuprolog.solve.sideffects

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.toOperatorSet
import it.unibo.tuprolog.theory.Theory

sealed class SideEffect {

    data class ResetStaticKb(val clauses: Iterable<Clause>) : SideEffect() {
        constructor(vararg clauses: Clause) : this(listOf(*clauses))

        constructor(clauses: Sequence<Clause>) : this(clauses.asIterable())

        val theory by lazy {
            if (clauses is Theory) {
                clauses
            } else {
                Theory.indexedOf(clauses)
            }
        }
    }

    data class AddStaticClauses(val clauses: Iterable<Clause>, val onTop: Boolean = false) : SideEffect() {
        constructor(vararg clauses: Clause, onTop: Boolean = false) : this(listOf(*clauses), onTop)

        constructor(clauses: Sequence<Clause>, onTop: Boolean = false) : this(clauses.asIterable(), onTop)
    }

    data class RemoveStaticClauses(val clauses: Iterable<Clause>) : SideEffect() {
        constructor(vararg clauses: Clause) : this(listOf(*clauses))

        constructor(clauses: Sequence<Clause>) : this(clauses.asIterable())
    }

    data class ResetDynamicKb(val clauses: Iterable<Clause>) : SideEffect() {
        constructor(vararg clauses: Clause) : this(listOf(*clauses))

        constructor(clauses: Sequence<Clause>) : this(clauses.asIterable())

        val theory by lazy {
            if (clauses is Theory) {
                clauses
            } else {
                Theory.indexedOf(clauses)
            }
        }
    }

    data class AddDynamicClauses(val clauses: Iterable<Clause>, val onTop: Boolean = false) : SideEffect() {
        constructor(vararg clauses: Clause, onTop: Boolean = false) : this(listOf(*clauses), onTop)

        constructor(clauses: Sequence<Clause>, onTop: Boolean = false) : this(clauses.asIterable(), onTop)
    }

    data class RemoveDynamicClauses(val clauses: Iterable<Clause>) : SideEffect() {
        constructor(vararg clauses: Clause) : this(listOf(*clauses))

        constructor(clauses: Sequence<Clause>) : this(clauses.asIterable())
    }

    data class SetFlags(val flags: Map<String, Term>) : SideEffect() {
        constructor(vararg flags: Pair<String, Term>) : this(listOf(*flags))

        constructor(flags: Iterable<Pair<String, Term>>) : this(flags.toMap())

        constructor(flags: Sequence<Pair<String, Term>>) : this(flags.toMap())
    }

    data class ResetFlags(val flags: Map<String, Term>) : SideEffect() {
        constructor(vararg flags: Pair<String, Term>) : this(listOf(*flags))

        constructor(flags: Iterable<Pair<String, Term>>) : this(flags.toMap())

        constructor(flags: Sequence<Pair<String, Term>>) : this(flags.toMap())
    }

    data class ClearFlags(val names: Iterable<String>) : SideEffect() {
        constructor(vararg names: String) : this(listOf(*names))

        constructor(names: Sequence<String>) : this(names.toList())
    }

    data class LoadLibrary(val alias: String, val library: Library) : SideEffect() {

        init {
            when (val lib = library) {
                is AliasedLibrary -> require(lib.alias == alias)
            }
        }

        val aliasedLibrary: AliasedLibrary by lazy {
            if (library is AliasedLibrary) {
                library
            } else {
                Library.of(library, alias)
            }
        }
    }

    data class UnloadLibraries(val aliases: List<String>) : SideEffect() {
        constructor(aliases: Iterable<String>) : this(aliases.toList())
        constructor(aliases: Sequence<String>) : this(aliases.toList())
        constructor(vararg aliases: String) : this(listOf(*aliases))
    }

    data class UpdateLibrary(val alias: String, val library: Library) : SideEffect() {
        init {
            when (val lib = library) {
                is AliasedLibrary -> require(lib.alias == alias)
            }
        }

        val aliasedLibrary: AliasedLibrary by lazy {
            if (library is AliasedLibrary) {
                library
            } else {
                Library.of(library, alias)
            }
        }
    }

    data class AddLibraries(val libraries: Libraries) : SideEffect() {
        constructor(libraries: Iterable<AliasedLibrary>) : this(Libraries.of(libraries))
        constructor(libraries: Sequence<AliasedLibrary>) : this(Libraries.of(libraries))
        constructor(vararg libraries: AliasedLibrary) : this(Libraries.of(*libraries))
    }

    data class ResetLibraries(val libraries: Libraries) : SideEffect() {
        constructor(libraries: Iterable<AliasedLibrary>) : this(Libraries.of(libraries))
        constructor(libraries: Sequence<AliasedLibrary>) : this(Libraries.of(libraries))
        constructor(vararg libraries: AliasedLibrary) : this(Libraries.of(*libraries))
    }

    data class SetOperators(val operators: Iterable<Operator>) : SideEffect() {
        constructor(vararg operators: Operator) : this(listOf(*operators))

        constructor(operators: Sequence<Operator>) : this(operators.toOperatorSet())

        val operatorSet: OperatorSet by lazy {
            if (operators is OperatorSet) {
                operators
            } else {
                OperatorSet(operators)
            }
        }
    }

    data class ResetOperators(val operators: Iterable<Operator>) : SideEffect() {
        constructor(vararg operators: Operator) : this(listOf(*operators))

        constructor(operators: Sequence<Operator>) : this(operators.toOperatorSet())

        val operatorSet: OperatorSet by lazy {
            if (operators is OperatorSet) {
                operators
            } else {
                OperatorSet(operators)
            }
        }
    }

    data class RemoveOperators(val operators: Iterable<Operator>) : SideEffect() {
        constructor(vararg operators: Operator) : this(listOf(*operators))

        constructor(operators: Sequence<Operator>) : this(operators.toOperatorSet())

        val operatorSet: OperatorSet by lazy {
            if (operators is OperatorSet) {
                operators
            } else {
                OperatorSet(operators)
            }
        }
    }

    data class OpenInputChannels(val inputChannels: Map<String, InputChannel<String>>) : SideEffect() {
        constructor(vararg inputChannels: Pair<String, InputChannel<String>>) : this(listOf(*inputChannels))

        constructor(inputChannels: Iterable<Pair<String, InputChannel<String>>>) : this(inputChannels.toMap())

        constructor(inputChannels: Sequence<Pair<String, InputChannel<String>>>) : this(inputChannels.toMap())
    }

    data class ResetInputChannels(val inputChannels: Map<String, InputChannel<String>>) : SideEffect() {
        constructor(vararg inputChannels: Pair<String, InputChannel<String>>) : this(listOf(*inputChannels))

        constructor(inputChannels: Iterable<Pair<String, InputChannel<String>>>) : this(inputChannels.toMap())

        constructor(inputChannels: Sequence<Pair<String, InputChannel<String>>>) : this(inputChannels.toMap())
    }

    data class CloseInputChannels(val names: Iterable<String>) : SideEffect() {
        constructor(vararg names: String) : this(listOf(*names))

        constructor(names: Sequence<String>) : this(names.toList())
    }

    data class OpenOutputChannels(val outputChannels: Map<String, OutputChannel<String>>) : SideEffect() {
        constructor(vararg outputChannels: Pair<String, OutputChannel<String>>) : this(listOf(*outputChannels))

        constructor(outputChannels: Iterable<Pair<String, OutputChannel<String>>>) : this(outputChannels.toMap())

        constructor(outputChannels: Sequence<Pair<String, OutputChannel<String>>>) : this(outputChannels.toMap())
    }

    data class ResetOutputChannels(val outputChannels: Map<String, OutputChannel<String>>) : SideEffect() {
        constructor(vararg outputChannels: Pair<String, OutputChannel<String>>) : this(listOf(*outputChannels))

        constructor(outputChannels: Iterable<Pair<String, OutputChannel<String>>>) : this(outputChannels.toMap())

        constructor(outputChannels: Sequence<Pair<String, OutputChannel<String>>>) : this(outputChannels.toMap())
    }

    data class CloseOutputChannels(val names: Iterable<String>) : SideEffect() {
        constructor(vararg names: String) : this(listOf(*names))

        constructor(names: Sequence<String>) : this(names.toList())
    }

    abstract class SetCustomData(open val data: Map<String, Any>, open val reset: Boolean = false) : SideEffect()

    data class SetDurableData(
        override val data: Map<String, Any>,
        override val reset: Boolean = false
    ) : SetCustomData(data, reset) {
        constructor(key: String, value: Any, reset: Boolean = false) : this(listOf(key to value), reset)

        constructor(vararg data: Pair<String, Any>, reset: Boolean = false) : this(listOf(*data), reset)

        constructor(data: Iterable<Pair<String, Any>>, reset: Boolean = false) : this(data.toMap(), reset)
    }

    data class SetEphemeralData(
        override val data: Map<String, Any>,
        override val reset: Boolean = false
    ) : SetCustomData(data, reset) {
        constructor(key: String, value: Any, reset: Boolean = false) : this(listOf(key to value), reset)

        constructor(vararg data: Pair<String, Any>, reset: Boolean = false) : this(listOf(*data), reset)

        constructor(data: Iterable<Pair<String, Any>>, reset: Boolean = false) : this(data.toMap(), reset)
    }
}
