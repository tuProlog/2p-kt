package it.unibo.tuprolog.solve.sideffects.impl

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.sideffects.SideEffect
import it.unibo.tuprolog.solve.sideffects.SideEffectFactory

internal abstract class AbstractSideEffectFactory : SideEffectFactory {
    override fun resetStaticKb(clauses: Iterable<Clause>) =
        SideEffect.ResetStaticKb(clauses)

    override fun resetStaticKb(clauses: Sequence<Clause>) =
        SideEffect.ResetStaticKb(clauses)

    override fun resetStaticKb(vararg clauses: Clause) =
        SideEffect.ResetStaticKb(*clauses)

    override fun addStaticClauses(clauses: Iterable<Clause>, onTop: Boolean) =
        SideEffect.AddStaticClauses(clauses, onTop)

    override fun addStaticClauses(clauses: Sequence<Clause>, onTop: Boolean) =
        SideEffect.AddStaticClauses(clauses, onTop)

    override fun addStaticClauses(vararg clauses: Clause, onTop: Boolean) =
        SideEffect.AddStaticClauses(*clauses, onTop = onTop)

    override fun removeStaticClauses(clauses: Iterable<Clause>) =
        SideEffect.RemoveStaticClauses(clauses)

    override fun removeStaticClauses(clauses: Sequence<Clause>) =
        SideEffect.RemoveStaticClauses(clauses)

    override fun removeStaticClauses(vararg clauses: Clause) =
        SideEffect.RemoveStaticClauses(*clauses)

    override fun resetDynamicKb(clauses: Iterable<Clause>) =
        SideEffect.ResetDynamicKb(clauses)

    override fun resetDynamicKb(clauses: Sequence<Clause>) =
        SideEffect.ResetDynamicKb(clauses)

    override fun resetDynamicKb(vararg clauses: Clause) =
        SideEffect.ResetDynamicKb(*clauses)

    override fun addDynamicClauses(clauses: Iterable<Clause>, onTop: Boolean) =
        SideEffect.AddDynamicClauses(clauses, onTop)

    override fun addDynamicClauses(clauses: Sequence<Clause>, onTop: Boolean) =
        SideEffect.AddDynamicClauses(clauses, onTop)

    override fun addDynamicClauses(vararg clauses: Clause, onTop: Boolean) =
        SideEffect.AddDynamicClauses(*clauses, onTop = onTop)

    override fun removeDynamicClauses(clauses: Iterable<Clause>) =
        SideEffect.RemoveDynamicClauses(clauses)

    override fun removeDynamicClauses(clauses: Sequence<Clause>) =
        SideEffect.RemoveDynamicClauses(clauses)

    override fun removeDynamicClauses(vararg clauses: Clause) =
        SideEffect.RemoveDynamicClauses(*clauses)

    override fun setFlags(flags: Map<String, Term>) =
        SideEffect.SetFlags(flags)

    override fun setFlags(vararg flags: Pair<String, Term>) =
        SideEffect.SetFlags(*flags)

    override fun setFlag(name: String, value: Term) =
        SideEffect.SetFlags(name to value)

    override fun resetFlags(flags: Map<String, Term>) =
        SideEffect.ResetFlags(flags)

    override fun resetFlags(vararg flags: Pair<String, Term>) =
        SideEffect.ResetFlags(*flags)

    override fun clearFlags(names: Iterable<String>) =
        SideEffect.ClearFlags(names)

    override fun clearFlags(names: Sequence<String>) =
        SideEffect.ClearFlags(names)

    override fun clearFlags(vararg names: String) =
        SideEffect.ClearFlags(*names)

    override fun loadLibrary(library: Library) =
        SideEffect.LoadLibrary(library)

    override fun unloadLibraries(aliases: Iterable<String>) =
        SideEffect.UnloadLibraries(aliases)

    override fun unloadLibraries(aliases: Sequence<String>) =
        SideEffect.UnloadLibraries(aliases)

    override fun unloadLibraries(vararg aliases: String) =
        SideEffect.UnloadLibraries(*aliases)

    override fun updateLibrary(library: Library) =
        SideEffect.UpdateLibrary(library)

    override fun resetRuntime(libraries: Runtime) =
        SideEffect.ResetRuntime(libraries)

    override fun resetRuntime(libraries: Iterable<Library>) =
        SideEffect.ResetRuntime(libraries)

    override fun resetRuntime(libraries: Sequence<Library>) =
        SideEffect.ResetRuntime(libraries)

    override fun resetRuntime(vararg libraries: Library) =
        SideEffect.ResetRuntime(*libraries)

    override fun addLibraries(libraries: Runtime) =
        SideEffect.AddLibraries(libraries)

    override fun addLibraries(libraries: Iterable<Library>) =
        SideEffect.AddLibraries(libraries)

    override fun addLibraries(libraries: Sequence<Library>) =
        SideEffect.AddLibraries(libraries)

    override fun addLibraries(vararg libraries: Library) =
        SideEffect.AddLibraries(*libraries)

    override fun setOperators(operators: Iterable<Operator>) =
        SideEffect.SetOperators(operators)

    override fun setOperators(operators: Sequence<Operator>) =
        SideEffect.SetOperators(operators)

    override fun setOperators(vararg operators: Operator) =
        SideEffect.SetOperators(*operators)

    override fun resetOperators(operators: Iterable<Operator>) =
        SideEffect.ResetOperators(operators)

    override fun resetOperators(operators: Sequence<Operator>) =
        SideEffect.ResetOperators(operators)

    override fun resetOperators(vararg operators: Operator) =
        SideEffect.ResetOperators(*operators)

    override fun removeOperators(operators: Iterable<Operator>) =
        SideEffect.RemoveOperators(operators)

    override fun removeOperators(operators: Sequence<Operator>) =
        SideEffect.RemoveOperators(operators)

    override fun removeOperators(vararg operators: Operator) =
        SideEffect.RemoveOperators(*operators)

    override fun openInputChannels(inputChannels: Map<String, InputChannel<String>>) =
        SideEffect.OpenInputChannels(inputChannels)

    override fun openInputChannels(vararg inputChannels: Pair<String, InputChannel<String>>) =
        SideEffect.OpenInputChannels(*inputChannels)

    override fun openInputChannel(name: String, inputChannel: InputChannel<String>) =
        SideEffect.OpenInputChannels(name to inputChannel)

    override fun resetInputChannels(vararg inputChannels: Pair<String, InputChannel<String>>) =
        SideEffect.ResetInputChannels(*inputChannels)

    override fun resetInputChannels(inputChannels: Iterable<Pair<String, InputChannel<String>>>) =
        SideEffect.ResetInputChannels(inputChannels)

    override fun resetInputChannels(inputChannels: Sequence<Pair<String, InputChannel<String>>>) =
        SideEffect.ResetInputChannels(inputChannels)

    override fun resetInputChannels(inputChannels: Map<String, InputChannel<String>>) =
        SideEffect.ResetInputChannels(inputChannels)

    override fun closeInputChannels(names: Iterable<String>) =
        SideEffect.CloseInputChannels(names)

    override fun closeInputChannels(names: Sequence<String>) =
        SideEffect.CloseInputChannels(names)

    override fun closeInputChannels(vararg names: String) =
        SideEffect.CloseInputChannels(*names)

    override fun openOutputChannels(outputChannels: Map<String, OutputChannel<String>>) =
        SideEffect.OpenOutputChannels(outputChannels)

    override fun openOutputChannels(vararg outputChannels: Pair<String, OutputChannel<String>>) =
        SideEffect.OpenOutputChannels(*outputChannels)

    override fun openOutputChannel(name: String, outputChannel: OutputChannel<String>) =
        SideEffect.OpenOutputChannels(name to outputChannel)

    override fun resetOutputChannels(outputChannels: Iterable<Pair<String, OutputChannel<String>>>) =
        SideEffect.ResetOutputChannels(outputChannels)

    override fun resetOutputChannels(outputChannels: Sequence<Pair<String, OutputChannel<String>>>) =
        SideEffect.ResetOutputChannels(outputChannels)

    override fun resetOutputChannels(outputChannels: Map<String, OutputChannel<String>>) =
        SideEffect.ResetOutputChannels(outputChannels)

    override fun resetOutputChannels(vararg outputChannels: Pair<String, OutputChannel<String>>) =
        SideEffect.ResetOutputChannels(*outputChannels)

    override fun closeOutputChannels(names: Iterable<String>) =
        SideEffect.CloseOutputChannels(names)

    override fun closeOutputChannels(names: Sequence<String>) =
        SideEffect.CloseOutputChannels(names)

    override fun closeOutputChannels(vararg names: String) =
        SideEffect.CloseOutputChannels(*names)

    override fun addEphemeralData(key: String, value: Any) =
        SideEffect.SetEphemeralData(key, value, reset = false)

    override fun <X> addEphemeralData(data: Map<String, X>) =
        SideEffect.SetEphemeralData(data.mapValues { it }, reset = false)

    override fun <X> addEphemeralData(vararg data: Pair<String, X>) =
        SideEffect.SetEphemeralData(data.map { (a, b) -> a to (b as Any) }, reset = false)

    override fun setEphemeralData(key: String, value: Any) =
        SideEffect.SetEphemeralData(key, value, reset = false)

    override fun <X> setEphemeralData(data: Map<String, X>) =
        SideEffect.SetEphemeralData(data.mapValues { it }, reset = false)

    override fun <X> setEphemeralData(vararg data: Pair<String, X>) =
        SideEffect.SetEphemeralData(data.map { (a, b) -> a to (b as Any) }, reset = false)

    override fun addDurableData(key: String, value: Any) =
        SideEffect.SetDurableData(key, value, reset = false)

    override fun <X> addDurableData(data: Map<String, X>) =
        SideEffect.SetDurableData(data.mapValues { it }, reset = false)

    override fun <X> addDurableData(vararg data: Pair<String, X>) =
        SideEffect.SetDurableData(data.map { (a, b) -> a to (b as Any) }, reset = false)

    override fun setDurableData(key: String, value: Any) =
        SideEffect.SetDurableData(key, value, reset = false)

    override fun <X> setDurableData(data: Map<String, X>) =
        SideEffect.SetDurableData(data.mapValues { it }, reset = false)

    override fun <X> setDurableData(vararg data: Pair<String, X>) =
        SideEffect.SetDurableData(data.map { (a, b) -> a to (b as Any) }, reset = false)

    override fun addPersistentData(key: String, value: Any) =
        SideEffect.SetPersistentData(key, value, reset = false)

    override fun <X> addPersistentData(data: Map<String, X>) =
        SideEffect.SetPersistentData(data.mapValues { it }, reset = false)

    override fun <X> addPersistentData(vararg data: Pair<String, X>) =
        SideEffect.SetPersistentData(data.map { (a, b) -> a to (b as Any) }, reset = false)

    override fun setPersistentData(key: String, value: Any) =
        SideEffect.SetPersistentData(key, value, reset = false)

    override fun <X> setPersistentData(data: Map<String, X>) =
        SideEffect.SetPersistentData(data.mapValues { it }, reset = false)

    override fun <X> setPersistentData(vararg data: Pair<String, X>) =
        SideEffect.SetPersistentData(data.map { (a, b) -> a to (b as Any) }, reset = false)
}
