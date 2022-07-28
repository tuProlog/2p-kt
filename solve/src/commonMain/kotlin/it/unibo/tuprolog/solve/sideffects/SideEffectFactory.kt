package it.unibo.tuprolog.solve.sideffects

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.library.Library
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface SideEffectFactory {

    companion object {
        @JvmStatic
        @JsName("default")
        val default = object : SideEffectFactory { }
    }

    @JsName("resetStaticKbIterable")
    fun resetStaticKb(clauses: Iterable<Clause>) =
        SideEffect.ResetStaticKb(clauses)

    @JsName("resetStaticKbSequence")
    fun resetStaticKb(clauses: Sequence<Clause>) =
        SideEffect.ResetStaticKb(clauses)

    @JsName("resetStaticKb")
    fun resetStaticKb(vararg clauses: Clause) =
        SideEffect.ResetStaticKb(*clauses)

    @JsName("addStaticClausesIterable")
    fun addStaticClauses(clauses: Iterable<Clause>, onTop: Boolean = false) =
        SideEffect.AddStaticClauses(clauses, onTop)

    @JsName("addStaticClausesSequence")
    fun addStaticClauses(clauses: Sequence<Clause>, onTop: Boolean = false) =
        SideEffect.AddStaticClauses(clauses, onTop)

    @JsName("addStaticClauses")
    fun addStaticClauses(vararg clauses: Clause, onTop: Boolean = false) =
        SideEffect.AddStaticClauses(*clauses, onTop = onTop)

    @JsName("removeStaticClausesIterable")
    fun removeStaticClauses(clauses: Iterable<Clause>) =
        SideEffect.RemoveStaticClauses(clauses)

    @JsName("removeStaticClausesSequence")
    fun removeStaticClauses(clauses: Sequence<Clause>) =
        SideEffect.RemoveStaticClauses(clauses)

    @JsName("removeStaticClauses")
    fun removeStaticClauses(vararg clauses: Clause) =
        SideEffect.RemoveStaticClauses(*clauses)

    @JsName("resetDynamicKbIterable")
    fun resetDynamicKb(clauses: Iterable<Clause>) =
        SideEffect.ResetDynamicKb(clauses)

    @JsName("resetDynamicKbSequence")
    fun resetDynamicKb(clauses: Sequence<Clause>) =
        SideEffect.ResetDynamicKb(clauses)

    @JsName("resetDynamicKb")
    fun resetDynamicKb(vararg clauses: Clause) =
        SideEffect.ResetDynamicKb(*clauses)

    @JsName("addDynamicClausesIterable")
    fun addDynamicClauses(clauses: Iterable<Clause>, onTop: Boolean = false) =
        SideEffect.AddDynamicClauses(clauses, onTop)

    @JsName("addDynamicClausesSequence")
    fun addDynamicClauses(clauses: Sequence<Clause>, onTop: Boolean = false) =
        SideEffect.AddDynamicClauses(clauses, onTop)

    @JsName("addDynamicClauses")
    fun addDynamicClauses(vararg clauses: Clause, onTop: Boolean = false) =
        SideEffect.AddDynamicClauses(*clauses, onTop = onTop)

    @JsName("removeDynamicClausesIterable")
    fun removeDynamicClauses(clauses: Iterable<Clause>) =
        SideEffect.RemoveDynamicClauses(clauses)

    @JsName("removeDynamicClausesSequence")
    fun removeDynamicClauses(clauses: Sequence<Clause>) =
        SideEffect.RemoveDynamicClauses(clauses)

    @JsName("removeDynamicClauses")
    fun removeDynamicClauses(vararg clauses: Clause) =
        SideEffect.RemoveDynamicClauses(*clauses)

    @JsName("setFlagsMap")
    fun setFlags(flags: Map<String, Term>) =
        SideEffect.SetFlags(flags)

    @JsName("setFlags")
    fun setFlags(vararg flags: Pair<String, Term>) =
        SideEffect.SetFlags(*flags)

    @JsName("setFlag")
    fun setFlag(name: String, value: Term) =
        SideEffect.SetFlags(name to value)

    @JsName("resetFlagsMap")
    fun resetFlags(flags: Map<String, Term>) =
        SideEffect.ResetFlags(flags)

    @JsName("resetFlags")
    fun resetFlags(vararg flags: Pair<String, Term>) =
        SideEffect.ResetFlags(*flags)

    @JsName("clearFlagsIterable")
    fun clearFlags(names: Iterable<String>) =
        SideEffect.ClearFlags(names)

    @JsName("clearFlagsSequence")
    fun clearFlags(names: Sequence<String>) =
        SideEffect.ClearFlags(names)

    @JsName("clearFlags")
    fun clearFlags(vararg names: String) =
        SideEffect.ClearFlags(*names)

    @JsName("loadLibrary")
    fun loadLibrary(library: Library) =
        SideEffect.LoadLibrary(library)

    @JsName("unloadLibrariesIterable")
    fun unloadLibraries(aliases: Iterable<String>) =
        SideEffect.UnloadLibraries(aliases)

    @JsName("unloadLibrariesSequence")
    fun unloadLibraries(aliases: Sequence<String>) =
        SideEffect.UnloadLibraries(aliases)

    @JsName("unloadLibraries")
    fun unloadLibraries(vararg aliases: String) =
        SideEffect.UnloadLibraries(*aliases)

    @JsName("updateLibrary")
    fun updateLibrary(library: Library) =
        SideEffect.UpdateLibrary(library)

    @JsName("resetRuntime")
    fun resetRuntime(libraries: Runtime) =
        SideEffect.ResetRuntime(libraries)

    @JsName("resetRuntimeIterable")
    fun resetRuntime(libraries: Iterable<Library>) =
        SideEffect.ResetRuntime(libraries)

    @JsName("resetRuntimeSequence")
    fun resetRuntime(libraries: Sequence<Library>) =
        SideEffect.ResetRuntime(libraries)

    @JsName("resetLibrary")
    fun resetRuntime(vararg libraries: Library) =
        SideEffect.ResetRuntime(*libraries)

    @JsName("addLibraries")
    fun addLibraries(libraries: Runtime) =
        SideEffect.AddLibraries(libraries)

    @JsName("addLibrariesIterable")
    fun addLibraries(libraries: Iterable<Library>) =
        SideEffect.AddLibraries(libraries)

    @JsName("addLibrariesSequence")
    fun addLibraries(libraries: Sequence<Library>) =
        SideEffect.AddLibraries(libraries)

    @JsName("addLibrary")
    fun addLibraries(vararg libraries: Library) =
        SideEffect.AddLibraries(*libraries)

    @JsName("setOperatorsIterable")
    fun setOperators(operators: Iterable<Operator>) =
        SideEffect.SetOperators(operators)

    @JsName("setOperatorsSequence")
    fun setOperators(operators: Sequence<Operator>) =
        SideEffect.SetOperators(operators)

    @JsName("setOperators")
    fun setOperators(vararg operators: Operator) =
        SideEffect.SetOperators(*operators)

    @JsName("resetOperatorsIterable")
    fun resetOperators(operators: Iterable<Operator>) =
        SideEffect.ResetOperators(operators)

    @JsName("resetOperatorsSequence")
    fun resetOperators(operators: Sequence<Operator>) =
        SideEffect.ResetOperators(operators)

    @JsName("resetOperators")
    fun resetOperators(vararg operators: Operator) =
        SideEffect.ResetOperators(*operators)

    @JsName("removeOperatorsIterable")
    fun removeOperators(operators: Iterable<Operator>) =
        SideEffect.RemoveOperators(operators)

    @JsName("removeOperatorsSequence")
    fun removeOperators(operators: Sequence<Operator>) =
        SideEffect.RemoveOperators(operators)

    @JsName("removeOperators")
    fun removeOperators(vararg operators: Operator) =
        SideEffect.RemoveOperators(*operators)

    @JsName("openInputChannelsMap")
    fun openInputChannels(inputChannels: Map<String, InputChannel<String>>) =
        SideEffect.OpenInputChannels(inputChannels)

    @JsName("openInputChannels")
    fun openInputChannels(vararg inputChannels: Pair<String, InputChannel<String>>) =
        SideEffect.OpenInputChannels(*inputChannels)

    @JsName("openInputChannel")
    fun openInputChannel(name: String, inputChannel: InputChannel<String>) =
        SideEffect.OpenInputChannels(name to inputChannel)

    @JsName("resetInputChannels")
    fun resetInputChannels(vararg inputChannels: Pair<String, InputChannel<String>>) =
        SideEffect.ResetInputChannels(*inputChannels)

    @JsName("resetInputChannelsIterable")
    fun resetInputChannels(inputChannels: Iterable<Pair<String, InputChannel<String>>>) =
        SideEffect.ResetInputChannels(inputChannels)

    @JsName("resetInputChannelsSequence")
    fun resetInputChannels(inputChannels: Sequence<Pair<String, InputChannel<String>>>) =
        SideEffect.ResetInputChannels(inputChannels)

    @JsName("resetInputChannelsMap")
    fun resetInputChannels(inputChannels: Map<String, InputChannel<String>>) =
        SideEffect.ResetInputChannels(inputChannels)

    @JsName("closeInputChannelsIterable")
    fun closeInputChannels(names: Iterable<String>) =
        SideEffect.CloseInputChannels(names)

    @JsName("closeInputChannelsSequence")
    fun closeInputChannels(names: Sequence<String>) =
        SideEffect.CloseInputChannels(names)

    @JsName("closeInputChannels")
    fun closeInputChannels(vararg names: String) =
        SideEffect.CloseInputChannels(*names)

    @JsName("openOutputChannelsMap")
    fun openOutputChannels(outputChannels: Map<String, OutputChannel<String>>) =
        SideEffect.OpenOutputChannels(outputChannels)

    @JsName("openOutputChannels")
    fun openOutputChannels(vararg outputChannels: Pair<String, OutputChannel<String>>) =
        SideEffect.OpenOutputChannels(*outputChannels)

    @JsName("openOutputChannel")
    fun openOutputChannel(name: String, outputChannel: OutputChannel<String>) =
        SideEffect.OpenOutputChannels(name to outputChannel)

    @JsName("resetOutputChannelsIterable")
    fun resetOutputChannels(outputChannels: Iterable<Pair<String, OutputChannel<String>>>) =
        SideEffect.ResetOutputChannels(outputChannels)

    @JsName("resetOutputChannelsSequence")
    fun resetOutputChannels(outputChannels: Sequence<Pair<String, OutputChannel<String>>>) =
        SideEffect.ResetOutputChannels(outputChannels)

    @JsName("resetOutputChannelsMap")
    fun resetOutputChannels(outputChannels: Map<String, OutputChannel<String>>) =
        SideEffect.ResetOutputChannels(outputChannels)

    @JsName("resetOutputChannels")
    fun resetOutputChannels(vararg outputChannels: Pair<String, OutputChannel<String>>) =
        SideEffect.ResetOutputChannels(*outputChannels)

    @JsName("closeOutputChannelsIterable")
    fun closeOutputChannels(names: Iterable<String>) =
        SideEffect.CloseOutputChannels(names)

    @JsName("closeOutputChannelsSequence")
    fun closeOutputChannels(names: Sequence<String>) =
        SideEffect.CloseOutputChannels(names)

    @JsName("closeOutputChannels")
    fun closeOutputChannels(vararg names: String) =
        SideEffect.CloseOutputChannels(*names)

    @JsName("addEphemeralData")
    fun addEphemeralData(key: String, value: Any) =
        SideEffect.SetEphemeralData(key, value, reset = false)

    @JsName("addEphemeralDataMap")
    fun <X> addEphemeralData(data: Map<String, X>) =
        SideEffect.SetEphemeralData(data.mapValues { it }, reset = false)

    @JsName("addEphemeralDataPairs")
    fun <X> addEphemeralData(vararg data: Pair<String, X>) =
        SideEffect.SetEphemeralData(data.map { (a, b) -> a to (b as Any) }, reset = false)

    @JsName("setEphemeralData")
    fun setEphemeralData(key: String, value: Any) =
        SideEffect.SetEphemeralData(key, value, reset = false)

    @JsName("setEphemeralDataMap")
    fun <X> setEphemeralData(data: Map<String, X>) =
        SideEffect.SetEphemeralData(data.mapValues { it }, reset = false)

    @JsName("setEphemeralDataPairs")
    fun <X> setEphemeralData(vararg data: Pair<String, X>) =
        SideEffect.SetEphemeralData(data.map { (a, b) -> a to (b as Any) }, reset = false)

    @JsName("addDurableData")
    fun addDurableData(key: String, value: Any) =
        SideEffect.SetDurableData(key, value, reset = false)

    @JsName("addDurableDataMap")
    fun <X> addDurableData(data: Map<String, X>) =
        SideEffect.SetDurableData(data.mapValues { it }, reset = false)

    @JsName("addDurableDataPairs")
    fun <X> addDurableData(vararg data: Pair<String, X>) =
        SideEffect.SetDurableData(data.map { (a, b) -> a to (b as Any) }, reset = false)

    @JsName("setDurableData")
    fun setDurableData(key: String, value: Any) =
        SideEffect.SetDurableData(key, value, reset = false)

    @JsName("setDurableDataMap")
    fun <X> setDurableData(data: Map<String, X>) =
        SideEffect.SetDurableData(data.mapValues { it }, reset = false)

    @JsName("setDurableDataPairs")
    fun <X> setDurableData(vararg data: Pair<String, X>) =
        SideEffect.SetDurableData(data.map { (a, b) -> a to (b as Any) }, reset = false)

    @JsName("addPersistentData")
    fun addPersistentData(key: String, value: Any) =
        SideEffect.SetPersistentData(key, value, reset = false)

    @JsName("addPersistentDataMap")
    fun <X> addPersistentData(data: Map<String, X>) =
        SideEffect.SetPersistentData(data.mapValues { it }, reset = false)

    @JsName("addPersistentDataPairs")
    fun <X> addPersistentData(vararg data: Pair<String, X>) =
        SideEffect.SetPersistentData(data.map { (a, b) -> a to (b as Any) }, reset = false)

    @JsName("setPersistentData")
    fun setPersistentData(key: String, value: Any) =
        SideEffect.SetPersistentData(key, value, reset = false)

    @JsName("setPersistentDataMap")
    fun <X> setPersistentData(data: Map<String, X>) =
        SideEffect.SetPersistentData(data.mapValues { it }, reset = false)

    @JsName("setPersistentDataPairs")
    fun <X> setPersistentData(vararg data: Pair<String, X>) =
        SideEffect.SetPersistentData(data.map { (a, b) -> a to (b as Any) }, reset = false)
}
