package it.unibo.tuprolog.solve.sideffects.impl

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.sideffects.SideEffect
import it.unibo.tuprolog.solve.sideffects.SideEffectsBuilder

data class SideEffectsBuilderImpl(override val sideEffects: MutableList<SideEffect>) : SideEffectsBuilder {
    private fun <T : SideEffect> adding(f: () -> T): T = f().also { sideEffects.add(it) }

    override fun resetStaticKb(clauses: Iterable<Clause>): SideEffect.ResetStaticKb =
        adding { super.resetStaticKb(clauses) }

    override fun resetStaticKb(clauses: Sequence<Clause>): SideEffect.ResetStaticKb =
        adding { super.resetStaticKb(clauses) }

    override fun resetStaticKb(vararg clauses: Clause): SideEffect.ResetStaticKb =
        adding { super.resetStaticKb(*clauses) }

    override fun addStaticClauses(clauses: Iterable<Clause>, onTop: Boolean): SideEffect.AddStaticClauses =
        adding { super.addStaticClauses(clauses, onTop) }

    override fun addStaticClauses(clauses: Sequence<Clause>, onTop: Boolean): SideEffect.AddStaticClauses =
        adding { super.addStaticClauses(clauses, onTop) }

    override fun addStaticClauses(vararg clauses: Clause, onTop: Boolean): SideEffect.AddStaticClauses =
        adding { super.addStaticClauses(*clauses, onTop = onTop) }

    override fun removeStaticClauses(clauses: Iterable<Clause>): SideEffect.RemoveStaticClauses =
        adding { super.removeStaticClauses(clauses) }

    override fun removeStaticClauses(clauses: Sequence<Clause>): SideEffect.RemoveStaticClauses =
        adding { super.removeStaticClauses(clauses) }

    override fun removeStaticClauses(vararg clauses: Clause): SideEffect.RemoveStaticClauses =
        adding { super.removeStaticClauses(*clauses) }

    override fun resetDynamicKb(clauses: Iterable<Clause>): SideEffect.ResetDynamicKb =
        adding { super.resetDynamicKb(clauses) }

    override fun resetDynamicKb(clauses: Sequence<Clause>): SideEffect.ResetDynamicKb =
        adding { super.resetDynamicKb(clauses) }

    override fun resetDynamicKb(vararg clauses: Clause): SideEffect.ResetDynamicKb =
        adding { super.resetDynamicKb(*clauses) }

    override fun addDynamicClauses(clauses: Iterable<Clause>, onTop: Boolean): SideEffect.AddDynamicClauses =
        adding { super.addDynamicClauses(clauses, onTop) }

    override fun addDynamicClauses(clauses: Sequence<Clause>, onTop: Boolean): SideEffect.AddDynamicClauses =
        adding { super.addDynamicClauses(clauses, onTop) }

    override fun addDynamicClauses(vararg clauses: Clause, onTop: Boolean): SideEffect.AddDynamicClauses =
        adding { super.addDynamicClauses(*clauses, onTop = onTop) }

    override fun removeDynamicClauses(clauses: Iterable<Clause>): SideEffect.RemoveDynamicClauses =
        adding { super.removeDynamicClauses(clauses) }

    override fun removeDynamicClauses(clauses: Sequence<Clause>): SideEffect.RemoveDynamicClauses =
        adding { super.removeDynamicClauses(clauses) }

    override fun removeDynamicClauses(vararg clauses: Clause): SideEffect.RemoveDynamicClauses =
        adding { super.removeDynamicClauses(*clauses) }

    override fun setFlags(flags: Map<String, Term>): SideEffect.SetFlags = adding { super.setFlags(flags) }

    override fun setFlags(vararg flags: Pair<String, Term>): SideEffect.SetFlags = adding { super.setFlags(*flags) }

    override fun setFlag(name: String, value: Term): SideEffect.SetFlags = adding { super.setFlag(name, value) }

    override fun resetFlags(flags: Map<String, Term>): SideEffect.ResetFlags = adding { super.resetFlags(flags) }

    override fun resetFlags(vararg flags: Pair<String, Term>): SideEffect.ResetFlags =
        adding { super.resetFlags(*flags) }

    override fun clearFlags(names: Iterable<String>): SideEffect.ClearFlags = adding { super.clearFlags(names) }

    override fun clearFlags(names: Sequence<String>): SideEffect.ClearFlags = adding { super.clearFlags(names) }

    override fun clearFlags(vararg names: String): SideEffect.ClearFlags = adding { super.clearFlags(*names) }

    override fun loadLibrary(library: Library): SideEffect.LoadLibrary =
        adding { super.loadLibrary(library) }

    override fun updateLibrary(library: Library): SideEffect.UpdateLibrary =
        adding { super.updateLibrary(library) }

    override fun setOperators(operators: Iterable<Operator>): SideEffect.SetOperators =
        adding { super.setOperators(operators) }

    override fun setOperators(operators: Sequence<Operator>): SideEffect.SetOperators =
        adding { super.setOperators(operators) }

    override fun setOperators(vararg operators: Operator): SideEffect.SetOperators =
        adding { super.setOperators(*operators) }

    override fun resetOperators(operators: Iterable<Operator>): SideEffect.ResetOperators =
        adding { super.resetOperators(operators) }

    override fun resetOperators(operators: Sequence<Operator>): SideEffect.ResetOperators =
        adding { super.resetOperators(operators) }

    override fun resetOperators(vararg operators: Operator): SideEffect.ResetOperators =
        adding { super.resetOperators(*operators) }

    override fun removeOperators(operators: Iterable<Operator>): SideEffect.RemoveOperators =
        adding { super.removeOperators(operators) }

    override fun removeOperators(operators: Sequence<Operator>): SideEffect.RemoveOperators =
        adding { super.removeOperators(operators) }

    override fun removeOperators(vararg operators: Operator): SideEffect.RemoveOperators =
        adding { super.removeOperators(*operators) }

    override fun openInputChannels(inputChannels: Map<String, InputChannel<String>>): SideEffect.OpenInputChannels =
        adding { super.openInputChannels(inputChannels) }

    override fun openInputChannels(vararg inputChannels: Pair<String, InputChannel<String>>): SideEffect.OpenInputChannels =
        adding { super.openInputChannels(*inputChannels) }

    override fun openInputChannel(name: String, inputChannel: InputChannel<String>): SideEffect.OpenInputChannels =
        adding { super.openInputChannel(name, inputChannel) }

    override fun resetInputChannels(vararg inputChannels: Pair<String, InputChannel<String>>): SideEffect.ResetInputChannels =
        adding { super.resetInputChannels(*inputChannels) }

    override fun closeInputChannels(names: Iterable<String>): SideEffect.CloseInputChannels =
        adding { super.closeInputChannels(names) }

    override fun closeInputChannels(names: Sequence<String>): SideEffect.CloseInputChannels =
        adding { super.closeInputChannels(names) }

    override fun closeInputChannels(vararg names: String): SideEffect.CloseInputChannels =
        adding { super.closeInputChannels(*names) }

    override fun openOutputChannels(outputChannels: Map<String, OutputChannel<String>>): SideEffect.OpenOutputChannels =
        adding { super.openOutputChannels(outputChannels) }

    override fun openOutputChannels(vararg outputChannels: Pair<String, OutputChannel<String>>): SideEffect.OpenOutputChannels =
        adding { super.openOutputChannels(*outputChannels) }

    override fun openOutputChannel(name: String, outputChannel: OutputChannel<String>): SideEffect.OpenOutputChannels =
        adding { super.openOutputChannel(name, outputChannel) }

    override fun resetOutputChannels(outputChannels: Map<String, OutputChannel<String>>): SideEffect.ResetOutputChannels =
        adding { super.resetOutputChannels(outputChannels) }

    override fun resetOutputChannels(vararg outputChannels: Pair<String, OutputChannel<String>>): SideEffect.ResetOutputChannels =
        adding { super.resetOutputChannels(*outputChannels) }

    override fun closeOutputChannels(names: Iterable<String>): SideEffect.CloseOutputChannels =
        adding { super.closeOutputChannels(names) }

    override fun closeOutputChannels(names: Sequence<String>): SideEffect.CloseOutputChannels =
        adding { super.closeOutputChannels(names) }

    override fun closeOutputChannels(vararg names: String): SideEffect.CloseOutputChannels =
        adding { super.closeOutputChannels(*names) }

    override fun unloadLibraries(aliases: Iterable<String>): SideEffect.UnloadLibraries =
        adding { super.unloadLibraries(aliases) }

    override fun unloadLibraries(aliases: Sequence<String>): SideEffect.UnloadLibraries =
        adding { super.unloadLibraries(aliases) }

    override fun unloadLibraries(vararg aliases: String): SideEffect.UnloadLibraries =
        adding { super.unloadLibraries(*aliases) }

    override fun resetRuntime(libraries: Runtime): SideEffect.ResetRuntime =
        adding { super.resetRuntime(libraries) }

    override fun resetRuntime(libraries: Iterable<Library>): SideEffect.ResetRuntime =
        adding { super.resetRuntime(libraries) }

    override fun resetRuntime(libraries: Sequence<Library>): SideEffect.ResetRuntime =
        adding { super.resetRuntime(libraries) }

    override fun resetRuntime(vararg libraries: Library): SideEffect.ResetRuntime =
        adding { super.resetRuntime(*libraries) }

    override fun addLibraries(libraries: Runtime): SideEffect.AddLibraries = adding { super.addLibraries(libraries) }

    override fun addLibraries(libraries: Iterable<Library>): SideEffect.AddLibraries =
        adding { super.addLibraries(libraries) }

    override fun addLibraries(libraries: Sequence<Library>): SideEffect.AddLibraries =
        adding { super.addLibraries(libraries) }

    override fun addLibraries(vararg libraries: Library): SideEffect.AddLibraries =
        adding { super.addLibraries(*libraries) }

    override fun resetInputChannels(inputChannels: Iterable<Pair<String, InputChannel<String>>>): SideEffect.ResetInputChannels =
        adding { super.resetInputChannels(inputChannels) }

    override fun resetInputChannels(inputChannels: Sequence<Pair<String, InputChannel<String>>>): SideEffect.ResetInputChannels =
        adding { super.resetInputChannels(inputChannels) }

    override fun resetInputChannels(inputChannels: Map<String, InputChannel<String>>): SideEffect.ResetInputChannels =
        adding { super.resetInputChannels(inputChannels) }

    override fun resetOutputChannels(outputChannels: Iterable<Pair<String, OutputChannel<String>>>): SideEffect.ResetOutputChannels =
        adding { super.resetOutputChannels(outputChannels) }

    override fun resetOutputChannels(outputChannels: Sequence<Pair<String, OutputChannel<String>>>): SideEffect.ResetOutputChannels =
        adding { super.resetOutputChannels(outputChannels) }

    override fun addEphemeralData(key: String, value: Any): SideEffect.SetEphemeralData =
        adding { super.addEphemeralData(key, value) }

    override fun <X> addEphemeralData(data: Map<String, X>): SideEffect.SetEphemeralData =
        adding { super.addEphemeralData(data) }

    override fun <X> addEphemeralData(vararg data: Pair<String, X>): SideEffect.SetEphemeralData =
        adding { super.addEphemeralData(*data) }

    override fun setEphemeralData(key: String, value: Any): SideEffect.SetEphemeralData =
        adding { super.setEphemeralData(key, value) }

    override fun <X> setEphemeralData(data: Map<String, X>): SideEffect.SetEphemeralData =
        adding { super.setEphemeralData(data) }

    override fun <X> setEphemeralData(vararg data: Pair<String, X>): SideEffect.SetEphemeralData =
        adding { super.setEphemeralData(*data) }

    override fun addDurableData(key: String, value: Any): SideEffect.SetDurableData =
        adding { super.addDurableData(key, value) }

    override fun <X> addDurableData(data: Map<String, X>): SideEffect.SetDurableData =
        adding { super.addDurableData(data) }

    override fun <X> addDurableData(vararg data: Pair<String, X>): SideEffect.SetDurableData =
        adding { super.addDurableData(*data) }

    override fun setDurableData(key: String, value: Any): SideEffect.SetDurableData =
        adding { super.setDurableData(key, value) }

    override fun <X> setDurableData(data: Map<String, X>): SideEffect.SetDurableData =
        adding { super.setDurableData(data) }

    override fun <X> setDurableData(vararg data: Pair<String, X>): SideEffect.SetDurableData =
        adding { super.setDurableData(*data) }

    override fun addPersistentData(key: String, value: Any): SideEffect.SetPersistentData =
        adding { super.addPersistentData(key, value) }

    override fun <X> addPersistentData(data: Map<String, X>): SideEffect.SetPersistentData =
        adding { super.addPersistentData(data) }

    override fun <X> addPersistentData(vararg data: Pair<String, X>): SideEffect.SetPersistentData =
        adding { super.addPersistentData(*data) }

    override fun setPersistentData(key: String, value: Any): SideEffect.SetPersistentData =
        adding { super.setPersistentData(key, value) }

    override fun <X> setPersistentData(data: Map<String, X>): SideEffect.SetPersistentData =
        adding { super.setPersistentData(data) }

    override fun <X> setPersistentData(vararg data: Pair<String, X>): SideEffect.SetPersistentData =
        adding { super.setPersistentData(*data) }
}
