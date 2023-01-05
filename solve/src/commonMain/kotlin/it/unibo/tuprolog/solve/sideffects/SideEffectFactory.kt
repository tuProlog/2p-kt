package it.unibo.tuprolog.solve.sideffects

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.sideffects.impl.DefaultSideEffectFactory
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface SideEffectFactory {

    companion object {
        @JvmStatic
        @JsName("default")
        val default: SideEffectFactory = DefaultSideEffectFactory
    }

    @JsName("resetStaticKbIterable")
    fun resetStaticKb(clauses: Iterable<Clause>): SideEffect.ResetStaticKb

    @JsName("resetStaticKbSequence")
    fun resetStaticKb(clauses: Sequence<Clause>): SideEffect.ResetStaticKb

    @JsName("resetStaticKb")
    fun resetStaticKb(vararg clauses: Clause): SideEffect.ResetStaticKb

    @JsName("addStaticClausesIterable")
    fun addStaticClauses(clauses: Iterable<Clause>, onTop: Boolean = false): SideEffect.AddStaticClauses

    @JsName("addStaticClausesSequence")
    fun addStaticClauses(clauses: Sequence<Clause>, onTop: Boolean = false): SideEffect.AddStaticClauses

    @JsName("addStaticClauses")
    fun addStaticClauses(vararg clauses: Clause, onTop: Boolean = false): SideEffect.AddStaticClauses

    @JsName("removeStaticClausesIterable")
    fun removeStaticClauses(clauses: Iterable<Clause>): SideEffect.RemoveStaticClauses

    @JsName("removeStaticClausesSequence")
    fun removeStaticClauses(clauses: Sequence<Clause>): SideEffect.RemoveStaticClauses

    @JsName("removeStaticClauses")
    fun removeStaticClauses(vararg clauses: Clause): SideEffect.RemoveStaticClauses

    @JsName("resetDynamicKbIterable")
    fun resetDynamicKb(clauses: Iterable<Clause>): SideEffect.ResetDynamicKb

    @JsName("resetDynamicKbSequence")
    fun resetDynamicKb(clauses: Sequence<Clause>): SideEffect.ResetDynamicKb

    @JsName("resetDynamicKb")
    fun resetDynamicKb(vararg clauses: Clause): SideEffect.ResetDynamicKb

    @JsName("addDynamicClausesIterable")
    fun addDynamicClauses(clauses: Iterable<Clause>, onTop: Boolean = false): SideEffect.AddDynamicClauses

    @JsName("addDynamicClausesSequence")
    fun addDynamicClauses(clauses: Sequence<Clause>, onTop: Boolean = false): SideEffect.AddDynamicClauses

    @JsName("addDynamicClauses")
    fun addDynamicClauses(vararg clauses: Clause, onTop: Boolean = false): SideEffect.AddDynamicClauses

    @JsName("removeDynamicClausesIterable")
    fun removeDynamicClauses(clauses: Iterable<Clause>): SideEffect.RemoveDynamicClauses

    @JsName("removeDynamicClausesSequence")
    fun removeDynamicClauses(clauses: Sequence<Clause>): SideEffect.RemoveDynamicClauses

    @JsName("removeDynamicClauses")
    fun removeDynamicClauses(vararg clauses: Clause): SideEffect.RemoveDynamicClauses

    @JsName("setFlagsMap")
    fun setFlags(flags: Map<String, Term>): SideEffect.SetFlags

    @JsName("setFlags")
    fun setFlags(vararg flags: Pair<String, Term>): SideEffect.SetFlags

    @JsName("setFlag")
    fun setFlag(name: String, value: Term): SideEffect.SetFlags

    @JsName("resetFlagsMap")
    fun resetFlags(flags: Map<String, Term>): SideEffect.ResetFlags

    @JsName("resetFlags")
    fun resetFlags(vararg flags: Pair<String, Term>): SideEffect.ResetFlags

    @JsName("clearFlagsIterable")
    fun clearFlags(names: Iterable<String>): SideEffect.ClearFlags

    @JsName("clearFlagsSequence")
    fun clearFlags(names: Sequence<String>): SideEffect.ClearFlags

    @JsName("clearFlags")
    fun clearFlags(vararg names: String): SideEffect.ClearFlags

    @JsName("loadLibrary")
    fun loadLibrary(library: Library): SideEffect.LoadLibrary

    @JsName("unloadLibrariesIterable")
    fun unloadLibraries(aliases: Iterable<String>): SideEffect.UnloadLibraries

    @JsName("unloadLibrariesSequence")
    fun unloadLibraries(aliases: Sequence<String>): SideEffect.UnloadLibraries

    @JsName("unloadLibraries")
    fun unloadLibraries(vararg aliases: String): SideEffect.UnloadLibraries

    @JsName("updateLibrary")
    fun updateLibrary(library: Library): SideEffect.UpdateLibrary

    @JsName("resetRuntime")
    fun resetRuntime(libraries: Runtime): SideEffect.ResetRuntime

    @JsName("resetRuntimeIterable")
    fun resetRuntime(libraries: Iterable<Library>): SideEffect.ResetRuntime

    @JsName("resetRuntimeSequence")
    fun resetRuntime(libraries: Sequence<Library>): SideEffect.ResetRuntime

    @JsName("resetLibrary")
    fun resetRuntime(vararg libraries: Library): SideEffect.ResetRuntime

    @JsName("addLibraries")
    fun addLibraries(libraries: Runtime): SideEffect.AddLibraries

    @JsName("addLibrariesIterable")
    fun addLibraries(libraries: Iterable<Library>): SideEffect.AddLibraries

    @JsName("addLibrariesSequence")
    fun addLibraries(libraries: Sequence<Library>): SideEffect.AddLibraries

    @JsName("addLibrary")
    fun addLibraries(vararg libraries: Library): SideEffect.AddLibraries

    @JsName("setOperatorsIterable")
    fun setOperators(operators: Iterable<Operator>): SideEffect.SetOperators

    @JsName("setOperatorsSequence")
    fun setOperators(operators: Sequence<Operator>): SideEffect.SetOperators

    @JsName("setOperators")
    fun setOperators(vararg operators: Operator): SideEffect.SetOperators

    @JsName("resetOperatorsIterable")
    fun resetOperators(operators: Iterable<Operator>): SideEffect.ResetOperators

    @JsName("resetOperatorsSequence")
    fun resetOperators(operators: Sequence<Operator>): SideEffect.ResetOperators

    @JsName("resetOperators")
    fun resetOperators(vararg operators: Operator): SideEffect.ResetOperators

    @JsName("removeOperatorsIterable")
    fun removeOperators(operators: Iterable<Operator>): SideEffect.RemoveOperators

    @JsName("removeOperatorsSequence")
    fun removeOperators(operators: Sequence<Operator>): SideEffect.RemoveOperators

    @JsName("removeOperators")
    fun removeOperators(vararg operators: Operator): SideEffect.RemoveOperators

    @JsName("openInputChannelsMap")
    fun openInputChannels(inputChannels: Map<String, InputChannel<String>>): SideEffect.OpenInputChannels

    @JsName("openInputChannels")
    fun openInputChannels(vararg inputChannels: Pair<String, InputChannel<String>>): SideEffect.OpenInputChannels

    @JsName("openInputChannel")
    fun openInputChannel(name: String, inputChannel: InputChannel<String>): SideEffect.OpenInputChannels

    @JsName("resetInputChannels")
    fun resetInputChannels(vararg inputChannels: Pair<String, InputChannel<String>>): SideEffect.ResetInputChannels

    @JsName("resetInputChannelsIterable")
    fun resetInputChannels(inputChannels: Iterable<Pair<String, InputChannel<String>>>): SideEffect.ResetInputChannels

    @JsName("resetInputChannelsSequence")
    fun resetInputChannels(inputChannels: Sequence<Pair<String, InputChannel<String>>>): SideEffect.ResetInputChannels

    @JsName("resetInputChannelsMap")
    fun resetInputChannels(inputChannels: Map<String, InputChannel<String>>): SideEffect.ResetInputChannels

    @JsName("closeInputChannelsIterable")
    fun closeInputChannels(names: Iterable<String>): SideEffect.CloseInputChannels

    @JsName("closeInputChannelsSequence")
    fun closeInputChannels(names: Sequence<String>): SideEffect.CloseInputChannels

    @JsName("closeInputChannels")
    fun closeInputChannels(vararg names: String): SideEffect.CloseInputChannels

    @JsName("openOutputChannelsMap")
    fun openOutputChannels(outputChannels: Map<String, OutputChannel<String>>): SideEffect.OpenOutputChannels

    @JsName("openOutputChannels")
    fun openOutputChannels(vararg outputChannels: Pair<String, OutputChannel<String>>): SideEffect.OpenOutputChannels

    @JsName("openOutputChannel")
    fun openOutputChannel(name: String, outputChannel: OutputChannel<String>): SideEffect.OpenOutputChannels

    @JsName("resetOutputChannelsIterable")
    fun resetOutputChannels(outputChannels: Iterable<Pair<String, OutputChannel<String>>>): SideEffect.ResetOutputChannels

    @JsName("resetOutputChannelsSequence")
    fun resetOutputChannels(outputChannels: Sequence<Pair<String, OutputChannel<String>>>): SideEffect.ResetOutputChannels

    @JsName("resetOutputChannelsMap")
    fun resetOutputChannels(outputChannels: Map<String, OutputChannel<String>>): SideEffect.ResetOutputChannels

    @JsName("resetOutputChannels")
    fun resetOutputChannels(vararg outputChannels: Pair<String, OutputChannel<String>>): SideEffect.ResetOutputChannels

    @JsName("closeOutputChannelsIterable")
    fun closeOutputChannels(names: Iterable<String>): SideEffect.CloseOutputChannels

    @JsName("closeOutputChannelsSequence")
    fun closeOutputChannels(names: Sequence<String>): SideEffect.CloseOutputChannels

    @JsName("closeOutputChannels")
    fun closeOutputChannels(vararg names: String): SideEffect.CloseOutputChannels

    @JsName("addEphemeralData")
    fun addEphemeralData(key: String, value: Any): SideEffect.SetEphemeralData

    @JsName("addEphemeralDataMap")
    fun <X> addEphemeralData(data: Map<String, X>): SideEffect.SetEphemeralData

    @JsName("addEphemeralDataPairs")
    fun <X> addEphemeralData(vararg data: Pair<String, X>): SideEffect.SetEphemeralData

    @JsName("setEphemeralData")
    fun setEphemeralData(key: String, value: Any): SideEffect.SetEphemeralData

    @JsName("setEphemeralDataMap")
    fun <X> setEphemeralData(data: Map<String, X>): SideEffect.SetEphemeralData

    @JsName("setEphemeralDataPairs")
    fun <X> setEphemeralData(vararg data: Pair<String, X>): SideEffect.SetEphemeralData

    @JsName("addDurableData")
    fun addDurableData(key: String, value: Any): SideEffect.SetDurableData

    @JsName("addDurableDataMap")
    fun <X> addDurableData(data: Map<String, X>): SideEffect.SetDurableData

    @JsName("addDurableDataPairs")
    fun <X> addDurableData(vararg data: Pair<String, X>): SideEffect.SetDurableData

    @JsName("setDurableData")
    fun setDurableData(key: String, value: Any): SideEffect.SetDurableData

    @JsName("setDurableDataMap")
    fun <X> setDurableData(data: Map<String, X>): SideEffect.SetDurableData

    @JsName("setDurableDataPairs")
    fun <X> setDurableData(vararg data: Pair<String, X>): SideEffect.SetDurableData

    @JsName("addPersistentData")
    fun addPersistentData(key: String, value: Any): SideEffect.SetPersistentData

    @JsName("addPersistentDataMap")
    fun <X> addPersistentData(data: Map<String, X>): SideEffect.SetPersistentData

    @JsName("addPersistentDataPairs")
    fun <X> addPersistentData(vararg data: Pair<String, X>): SideEffect.SetPersistentData

    @JsName("setPersistentData")
    fun setPersistentData(key: String, value: Any): SideEffect.SetPersistentData

    @JsName("setPersistentDataMap")
    fun <X> setPersistentData(data: Map<String, X>): SideEffect.SetPersistentData

    @JsName("setPersistentDataPairs")
    fun <X> setPersistentData(vararg data: Pair<String, X>): SideEffect.SetPersistentData
}
