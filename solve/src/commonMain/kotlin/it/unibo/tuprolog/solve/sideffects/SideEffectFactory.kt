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

interface SideEffectFactory {
    companion object {
        @JvmStatic
        val default: SideEffectFactory = DefaultSideEffectFactory
    }

    fun resetStaticKb(clauses: Iterable<Clause>): SideEffect.ResetStaticKb

    fun resetStaticKb(clauses: Sequence<Clause>): SideEffect.ResetStaticKb

    fun resetStaticKb(vararg clauses: Clause): SideEffect.ResetStaticKb

    fun addStaticClauses(
        clauses: Iterable<Clause>,
        onTop: Boolean = false,
    ): SideEffect.AddStaticClauses

    fun addStaticClauses(
        clauses: Sequence<Clause>,
        onTop: Boolean = false,
    ): SideEffect.AddStaticClauses

    fun addStaticClauses(
        vararg clauses: Clause,
        onTop: Boolean = false,
    ): SideEffect.AddStaticClauses

    fun removeStaticClauses(clauses: Iterable<Clause>): SideEffect.RemoveStaticClauses

    fun removeStaticClauses(clauses: Sequence<Clause>): SideEffect.RemoveStaticClauses

    fun removeStaticClauses(vararg clauses: Clause): SideEffect.RemoveStaticClauses

    fun resetDynamicKb(clauses: Iterable<Clause>): SideEffect.ResetDynamicKb

    fun resetDynamicKb(clauses: Sequence<Clause>): SideEffect.ResetDynamicKb

    fun resetDynamicKb(vararg clauses: Clause): SideEffect.ResetDynamicKb

    fun addDynamicClauses(
        clauses: Iterable<Clause>,
        onTop: Boolean = false,
    ): SideEffect.AddDynamicClauses

    fun addDynamicClauses(
        clauses: Sequence<Clause>,
        onTop: Boolean = false,
    ): SideEffect.AddDynamicClauses

    fun addDynamicClauses(
        vararg clauses: Clause,
        onTop: Boolean = false,
    ): SideEffect.AddDynamicClauses

    fun removeDynamicClauses(clauses: Iterable<Clause>): SideEffect.RemoveDynamicClauses

    fun removeDynamicClauses(clauses: Sequence<Clause>): SideEffect.RemoveDynamicClauses

    fun removeDynamicClauses(vararg clauses: Clause): SideEffect.RemoveDynamicClauses

    fun setFlags(flags: Map<String, Term>): SideEffect.SetFlags

    fun setFlags(vararg flags: Pair<String, Term>): SideEffect.SetFlags

    fun setFlag(
        name: String,
        value: Term,
    ): SideEffect.SetFlags

    fun resetFlags(flags: Map<String, Term>): SideEffect.ResetFlags

    fun resetFlags(vararg flags: Pair<String, Term>): SideEffect.ResetFlags

    fun clearFlags(names: Iterable<String>): SideEffect.ClearFlags

    fun clearFlags(names: Sequence<String>): SideEffect.ClearFlags

    fun clearFlags(vararg names: String): SideEffect.ClearFlags

    fun loadLibrary(library: Library): SideEffect.LoadLibrary

    fun unloadLibraries(aliases: Iterable<String>): SideEffect.UnloadLibraries

    fun unloadLibraries(aliases: Sequence<String>): SideEffect.UnloadLibraries

    fun unloadLibraries(vararg aliases: String): SideEffect.UnloadLibraries

    fun updateLibrary(library: Library): SideEffect.UpdateLibrary

    fun resetRuntime(libraries: Runtime): SideEffect.ResetRuntime

    fun resetRuntime(libraries: Iterable<Library>): SideEffect.ResetRuntime

    fun resetRuntime(libraries: Sequence<Library>): SideEffect.ResetRuntime

    fun resetRuntime(vararg libraries: Library): SideEffect.ResetRuntime

    fun addLibraries(libraries: Runtime): SideEffect.AddLibraries

    fun addLibraries(libraries: Iterable<Library>): SideEffect.AddLibraries

    fun addLibraries(libraries: Sequence<Library>): SideEffect.AddLibraries

    fun addLibraries(vararg libraries: Library): SideEffect.AddLibraries

    fun setOperators(operators: Iterable<Operator>): SideEffect.SetOperators

    fun setOperators(operators: Sequence<Operator>): SideEffect.SetOperators

    fun setOperators(vararg operators: Operator): SideEffect.SetOperators

    fun resetOperators(operators: Iterable<Operator>): SideEffect.ResetOperators

    fun resetOperators(operators: Sequence<Operator>): SideEffect.ResetOperators

    fun resetOperators(vararg operators: Operator): SideEffect.ResetOperators

    fun removeOperators(operators: Iterable<Operator>): SideEffect.RemoveOperators

    fun removeOperators(operators: Sequence<Operator>): SideEffect.RemoveOperators

    fun removeOperators(vararg operators: Operator): SideEffect.RemoveOperators

    fun openInputChannels(inputChannels: Map<String, InputChannel<String>>): SideEffect.OpenInputChannels

    fun openInputChannels(vararg inputChannels: Pair<String, InputChannel<String>>): SideEffect.OpenInputChannels

    fun openInputChannel(
        name: String,
        inputChannel: InputChannel<String>,
    ): SideEffect.OpenInputChannels

    fun resetInputChannels(vararg inputChannels: Pair<String, InputChannel<String>>): SideEffect.ResetInputChannels

    fun resetInputChannels(inputChannels: Iterable<Pair<String, InputChannel<String>>>): SideEffect.ResetInputChannels

    fun resetInputChannels(inputChannels: Sequence<Pair<String, InputChannel<String>>>): SideEffect.ResetInputChannels

    fun resetInputChannels(inputChannels: Map<String, InputChannel<String>>): SideEffect.ResetInputChannels

    fun closeInputChannels(names: Iterable<String>): SideEffect.CloseInputChannels

    fun closeInputChannels(names: Sequence<String>): SideEffect.CloseInputChannels

    fun closeInputChannels(vararg names: String): SideEffect.CloseInputChannels

    fun openOutputChannels(outputChannels: Map<String, OutputChannel<String>>): SideEffect.OpenOutputChannels

    fun openOutputChannels(vararg outputChannels: Pair<String, OutputChannel<String>>): SideEffect.OpenOutputChannels

    fun openOutputChannel(
        name: String,
        outputChannel: OutputChannel<String>,
    ): SideEffect.OpenOutputChannels

    fun resetOutputChannels(
        outputChannels: Iterable<Pair<String, OutputChannel<String>>>,
    ): SideEffect.ResetOutputChannels

    fun resetOutputChannels(
        outputChannels: Sequence<Pair<String, OutputChannel<String>>>,
    ): SideEffect.ResetOutputChannels

    fun resetOutputChannels(outputChannels: Map<String, OutputChannel<String>>): SideEffect.ResetOutputChannels

    fun resetOutputChannels(vararg outputChannels: Pair<String, OutputChannel<String>>): SideEffect.ResetOutputChannels

    fun closeOutputChannels(names: Iterable<String>): SideEffect.CloseOutputChannels

    fun closeOutputChannels(names: Sequence<String>): SideEffect.CloseOutputChannels

    fun closeOutputChannels(vararg names: String): SideEffect.CloseOutputChannels

    fun addEphemeralData(
        key: String,
        value: Any,
    ): SideEffect.SetEphemeralData

    fun <X> addEphemeralData(data: Map<String, X>): SideEffect.SetEphemeralData

    fun <X> addEphemeralData(vararg data: Pair<String, X>): SideEffect.SetEphemeralData

    fun setEphemeralData(
        key: String,
        value: Any,
    ): SideEffect.SetEphemeralData

    fun <X> setEphemeralData(data: Map<String, X>): SideEffect.SetEphemeralData

    fun <X> setEphemeralData(vararg data: Pair<String, X>): SideEffect.SetEphemeralData

    fun addDurableData(
        key: String,
        value: Any,
    ): SideEffect.SetDurableData

    fun <X> addDurableData(data: Map<String, X>): SideEffect.SetDurableData

    fun <X> addDurableData(vararg data: Pair<String, X>): SideEffect.SetDurableData

    fun setDurableData(
        key: String,
        value: Any,
    ): SideEffect.SetDurableData

    fun <X> setDurableData(data: Map<String, X>): SideEffect.SetDurableData

    fun <X> setDurableData(vararg data: Pair<String, X>): SideEffect.SetDurableData

    fun addPersistentData(
        key: String,
        value: Any,
    ): SideEffect.SetPersistentData

    fun <X> addPersistentData(data: Map<String, X>): SideEffect.SetPersistentData

    fun <X> addPersistentData(vararg data: Pair<String, X>): SideEffect.SetPersistentData

    fun setPersistentData(
        key: String,
        value: Any,
    ): SideEffect.SetPersistentData

    fun <X> setPersistentData(data: Map<String, X>): SideEffect.SetPersistentData

    fun <X> setPersistentData(vararg data: Pair<String, X>): SideEffect.SetPersistentData
}
