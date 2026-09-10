package it.unibo.tuprolog.solve.sideffects

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.sideffects.impl.DefaultSideEffectFactory
import kotlin.jvm.JvmStatic

/**
 * A factory of [SideEffect]s, offering one (often overloaded) method per concrete [SideEffect] subtype, as an
 * ergonomic alternative to instantiating each subtype's constructor directly. Implemented by [SideEffectsBuilder]
 * (which additionally collects the created instances), and available stand-alone via [default].
 */
interface SideEffectFactory {
    companion object {
        /** The default [SideEffectFactory] implementation. */
        @JvmStatic
        val default: SideEffectFactory = DefaultSideEffectFactory
    }

    /** Creates a [SideEffect.ResetStaticKb]. */
    fun resetStaticKb(clauses: Iterable<Clause>): SideEffect.ResetStaticKb

    /** Creates a [SideEffect.ResetStaticKb]. */
    fun resetStaticKb(clauses: Sequence<Clause>): SideEffect.ResetStaticKb

    /** Creates a [SideEffect.ResetStaticKb]. */
    fun resetStaticKb(vararg clauses: Clause): SideEffect.ResetStaticKb

    /** Creates a [SideEffect.AddStaticClauses]. */
    fun addStaticClauses(
        clauses: Iterable<Clause>,
        onTop: Boolean = false,
    ): SideEffect.AddStaticClauses

    /** Creates a [SideEffect.AddStaticClauses]. */
    fun addStaticClauses(
        clauses: Sequence<Clause>,
        onTop: Boolean = false,
    ): SideEffect.AddStaticClauses

    /** Creates a [SideEffect.AddStaticClauses]. */
    fun addStaticClauses(
        vararg clauses: Clause,
        onTop: Boolean = false,
    ): SideEffect.AddStaticClauses

    /** Creates a [SideEffect.RemoveStaticClauses]. */
    fun removeStaticClauses(clauses: Iterable<Clause>): SideEffect.RemoveStaticClauses

    /** Creates a [SideEffect.RemoveStaticClauses]. */
    fun removeStaticClauses(clauses: Sequence<Clause>): SideEffect.RemoveStaticClauses

    /** Creates a [SideEffect.RemoveStaticClauses]. */
    fun removeStaticClauses(vararg clauses: Clause): SideEffect.RemoveStaticClauses

    /** Creates a [SideEffect.ResetDynamicKb]. */
    fun resetDynamicKb(clauses: Iterable<Clause>): SideEffect.ResetDynamicKb

    /** Creates a [SideEffect.ResetDynamicKb]. */
    fun resetDynamicKb(clauses: Sequence<Clause>): SideEffect.ResetDynamicKb

    /** Creates a [SideEffect.ResetDynamicKb]. */
    fun resetDynamicKb(vararg clauses: Clause): SideEffect.ResetDynamicKb

    /** Creates a [SideEffect.AddDynamicClauses]. */
    fun addDynamicClauses(
        clauses: Iterable<Clause>,
        onTop: Boolean = false,
    ): SideEffect.AddDynamicClauses

    /** Creates a [SideEffect.AddDynamicClauses]. */
    fun addDynamicClauses(
        clauses: Sequence<Clause>,
        onTop: Boolean = false,
    ): SideEffect.AddDynamicClauses

    /** Creates a [SideEffect.AddDynamicClauses]. */
    fun addDynamicClauses(
        vararg clauses: Clause,
        onTop: Boolean = false,
    ): SideEffect.AddDynamicClauses

    /** Creates a [SideEffect.RemoveDynamicClauses]. */
    fun removeDynamicClauses(clauses: Iterable<Clause>): SideEffect.RemoveDynamicClauses

    /** Creates a [SideEffect.RemoveDynamicClauses]. */
    fun removeDynamicClauses(clauses: Sequence<Clause>): SideEffect.RemoveDynamicClauses

    /** Creates a [SideEffect.RemoveDynamicClauses]. */
    fun removeDynamicClauses(vararg clauses: Clause): SideEffect.RemoveDynamicClauses

    /** Creates a [SideEffect.SetFlags]. */
    fun setFlags(flags: Map<String, Term>): SideEffect.SetFlags

    /** Creates a [SideEffect.SetFlags]. */
    fun setFlags(vararg flags: Pair<String, Term>): SideEffect.SetFlags

    /** Creates a [SideEffect.SetFlags]. */
    fun setFlag(
        name: String,
        value: Term,
    ): SideEffect.SetFlags

    /** Creates a [SideEffect.ResetFlags]. */
    fun resetFlags(flags: Map<String, Term>): SideEffect.ResetFlags

    /** Creates a [SideEffect.ResetFlags]. */
    fun resetFlags(vararg flags: Pair<String, Term>): SideEffect.ResetFlags

    /** Creates a [SideEffect.ClearFlags]. */
    fun clearFlags(names: Iterable<String>): SideEffect.ClearFlags

    /** Creates a [SideEffect.ClearFlags]. */
    fun clearFlags(names: Sequence<String>): SideEffect.ClearFlags

    /** Creates a [SideEffect.ClearFlags]. */
    fun clearFlags(vararg names: String): SideEffect.ClearFlags

    /** Creates a [SideEffect.LoadLibrary]. */
    fun loadLibrary(library: Library): SideEffect.LoadLibrary

    /** Creates a [SideEffect.UnloadLibraries]. */
    fun unloadLibraries(aliases: Iterable<String>): SideEffect.UnloadLibraries

    /** Creates a [SideEffect.UnloadLibraries]. */
    fun unloadLibraries(aliases: Sequence<String>): SideEffect.UnloadLibraries

    /** Creates a [SideEffect.UnloadLibraries]. */
    fun unloadLibraries(vararg aliases: String): SideEffect.UnloadLibraries

    /** Creates a [SideEffect.UpdateLibrary]. */
    fun updateLibrary(library: Library): SideEffect.UpdateLibrary

    /** Creates a [SideEffect.ResetRuntime]. */
    fun resetRuntime(libraries: Runtime): SideEffect.ResetRuntime

    /** Creates a [SideEffect.ResetRuntime]. */
    fun resetRuntime(libraries: Iterable<Library>): SideEffect.ResetRuntime

    /** Creates a [SideEffect.ResetRuntime]. */
    fun resetRuntime(libraries: Sequence<Library>): SideEffect.ResetRuntime

    /** Creates a [SideEffect.ResetRuntime]. */
    fun resetRuntime(vararg libraries: Library): SideEffect.ResetRuntime

    /** Creates a [SideEffect.AddLibraries]. */
    fun addLibraries(libraries: Runtime): SideEffect.AddLibraries

    /** Creates a [SideEffect.AddLibraries]. */
    fun addLibraries(libraries: Iterable<Library>): SideEffect.AddLibraries

    /** Creates a [SideEffect.AddLibraries]. */
    fun addLibraries(libraries: Sequence<Library>): SideEffect.AddLibraries

    /** Creates a [SideEffect.AddLibraries]. */
    fun addLibraries(vararg libraries: Library): SideEffect.AddLibraries

    /** Creates a [SideEffect.SetOperators]. */
    fun setOperators(operators: Iterable<Operator>): SideEffect.SetOperators

    /** Creates a [SideEffect.SetOperators]. */
    fun setOperators(operators: Sequence<Operator>): SideEffect.SetOperators

    /** Creates a [SideEffect.SetOperators]. */
    fun setOperators(vararg operators: Operator): SideEffect.SetOperators

    /** Creates a [SideEffect.ResetOperators]. */
    fun resetOperators(operators: Iterable<Operator>): SideEffect.ResetOperators

    /** Creates a [SideEffect.ResetOperators]. */
    fun resetOperators(operators: Sequence<Operator>): SideEffect.ResetOperators

    /** Creates a [SideEffect.ResetOperators]. */
    fun resetOperators(vararg operators: Operator): SideEffect.ResetOperators

    /** Creates a [SideEffect.RemoveOperators]. */
    fun removeOperators(operators: Iterable<Operator>): SideEffect.RemoveOperators

    /** Creates a [SideEffect.RemoveOperators]. */
    fun removeOperators(operators: Sequence<Operator>): SideEffect.RemoveOperators

    /** Creates a [SideEffect.RemoveOperators]. */
    fun removeOperators(vararg operators: Operator): SideEffect.RemoveOperators

    /** Creates a [SideEffect.OpenInputChannels]. */
    fun openInputChannels(inputChannels: Map<String, InputChannel<String>>): SideEffect.OpenInputChannels

    /** Creates a [SideEffect.OpenInputChannels]. */
    fun openInputChannels(vararg inputChannels: Pair<String, InputChannel<String>>): SideEffect.OpenInputChannels

    /** Creates a [SideEffect.OpenInputChannels]. */
    fun openInputChannel(
        name: String,
        inputChannel: InputChannel<String>,
    ): SideEffect.OpenInputChannels

    /** Creates a [SideEffect.ResetInputChannels]. */
    fun resetInputChannels(vararg inputChannels: Pair<String, InputChannel<String>>): SideEffect.ResetInputChannels

    /** Creates a [SideEffect.ResetInputChannels]. */
    fun resetInputChannels(inputChannels: Iterable<Pair<String, InputChannel<String>>>): SideEffect.ResetInputChannels

    /** Creates a [SideEffect.ResetInputChannels]. */
    fun resetInputChannels(inputChannels: Sequence<Pair<String, InputChannel<String>>>): SideEffect.ResetInputChannels

    /** Creates a [SideEffect.ResetInputChannels]. */
    fun resetInputChannels(inputChannels: Map<String, InputChannel<String>>): SideEffect.ResetInputChannels

    /** Creates a [SideEffect.CloseInputChannels]. */
    fun closeInputChannels(names: Iterable<String>): SideEffect.CloseInputChannels

    /** Creates a [SideEffect.CloseInputChannels]. */
    fun closeInputChannels(names: Sequence<String>): SideEffect.CloseInputChannels

    /** Creates a [SideEffect.CloseInputChannels]. */
    fun closeInputChannels(vararg names: String): SideEffect.CloseInputChannels

    /** Creates a [SideEffect.OpenOutputChannels]. */
    fun openOutputChannels(outputChannels: Map<String, OutputChannel<String>>): SideEffect.OpenOutputChannels

    /** Creates a [SideEffect.OpenOutputChannels]. */
    fun openOutputChannels(vararg outputChannels: Pair<String, OutputChannel<String>>): SideEffect.OpenOutputChannels

    /** Creates a [SideEffect.OpenOutputChannels]. */
    fun openOutputChannel(
        name: String,
        outputChannel: OutputChannel<String>,
    ): SideEffect.OpenOutputChannels

    /** Creates a [SideEffect.ResetOutputChannels]. */
    fun resetOutputChannels(
        outputChannels: Iterable<Pair<String, OutputChannel<String>>>,
    ): SideEffect.ResetOutputChannels

    /** Creates a [SideEffect.ResetOutputChannels]. */
    fun resetOutputChannels(
        outputChannels: Sequence<Pair<String, OutputChannel<String>>>,
    ): SideEffect.ResetOutputChannels

    /** Creates a [SideEffect.ResetOutputChannels]. */
    fun resetOutputChannels(outputChannels: Map<String, OutputChannel<String>>): SideEffect.ResetOutputChannels

    /** Creates a [SideEffect.ResetOutputChannels]. */
    fun resetOutputChannels(vararg outputChannels: Pair<String, OutputChannel<String>>): SideEffect.ResetOutputChannels

    /** Creates a [SideEffect.CloseOutputChannels]. */
    fun closeOutputChannels(names: Iterable<String>): SideEffect.CloseOutputChannels

    /** Creates a [SideEffect.CloseOutputChannels]. */
    fun closeOutputChannels(names: Sequence<String>): SideEffect.CloseOutputChannels

    /** Creates a [SideEffect.CloseOutputChannels]. */
    fun closeOutputChannels(vararg names: String): SideEffect.CloseOutputChannels

    /** Creates a [SideEffect.SetEphemeralData]. */
    fun addEphemeralData(
        key: String,
        value: Any,
    ): SideEffect.SetEphemeralData

    /** Creates a [SideEffect.SetEphemeralData]. */
    fun <X> addEphemeralData(data: Map<String, X>): SideEffect.SetEphemeralData

    /** Creates a [SideEffect.SetEphemeralData]. */
    fun <X> addEphemeralData(vararg data: Pair<String, X>): SideEffect.SetEphemeralData

    /** Creates a [SideEffect.SetEphemeralData]. */
    fun setEphemeralData(
        key: String,
        value: Any,
    ): SideEffect.SetEphemeralData

    /** Creates a [SideEffect.SetEphemeralData]. */
    fun <X> setEphemeralData(data: Map<String, X>): SideEffect.SetEphemeralData

    /** Creates a [SideEffect.SetEphemeralData]. */
    fun <X> setEphemeralData(vararg data: Pair<String, X>): SideEffect.SetEphemeralData

    /** Creates a [SideEffect.SetDurableData]. */
    fun addDurableData(
        key: String,
        value: Any,
    ): SideEffect.SetDurableData

    /** Creates a [SideEffect.SetDurableData]. */
    fun <X> addDurableData(data: Map<String, X>): SideEffect.SetDurableData

    /** Creates a [SideEffect.SetDurableData]. */
    fun <X> addDurableData(vararg data: Pair<String, X>): SideEffect.SetDurableData

    /** Creates a [SideEffect.SetDurableData]. */
    fun setDurableData(
        key: String,
        value: Any,
    ): SideEffect.SetDurableData

    /** Creates a [SideEffect.SetDurableData]. */
    fun <X> setDurableData(data: Map<String, X>): SideEffect.SetDurableData

    /** Creates a [SideEffect.SetDurableData]. */
    fun <X> setDurableData(vararg data: Pair<String, X>): SideEffect.SetDurableData

    /** Creates a [SideEffect.SetPersistentData]. */
    fun addPersistentData(
        key: String,
        value: Any,
    ): SideEffect.SetPersistentData

    /** Creates a [SideEffect.SetPersistentData]. */
    fun <X> addPersistentData(data: Map<String, X>): SideEffect.SetPersistentData

    /** Creates a [SideEffect.SetPersistentData]. */
    fun <X> addPersistentData(vararg data: Pair<String, X>): SideEffect.SetPersistentData

    /** Creates a [SideEffect.SetPersistentData]. */
    fun setPersistentData(
        key: String,
        value: Any,
    ): SideEffect.SetPersistentData

    /** Creates a [SideEffect.SetPersistentData]. */
    fun <X> setPersistentData(data: Map<String, X>): SideEffect.SetPersistentData

    /** Creates a [SideEffect.SetPersistentData]. */
    fun <X> setPersistentData(vararg data: Pair<String, X>): SideEffect.SetPersistentData
}
