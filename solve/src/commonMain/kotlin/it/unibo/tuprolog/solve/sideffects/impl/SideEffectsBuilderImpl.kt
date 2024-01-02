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
import it.unibo.tuprolog.solve.sideffects.SideEffectsBuilder

internal data class SideEffectsBuilderImpl(
    override val sideEffects: MutableList<SideEffect>,
    private val factory: SideEffectFactory = DefaultSideEffectFactory,
) : SideEffectsBuilder {
    private fun <T : SideEffect> adding(f: () -> T): T = f().also { sideEffects.add(it) }

    override fun resetStaticKb(clauses: Iterable<Clause>): SideEffect.ResetStaticKb = adding { factory.resetStaticKb(clauses) }

    override fun resetStaticKb(clauses: Sequence<Clause>): SideEffect.ResetStaticKb = adding { factory.resetStaticKb(clauses) }

    override fun resetStaticKb(vararg clauses: Clause): SideEffect.ResetStaticKb = adding { factory.resetStaticKb(*clauses) }

    override fun addStaticClauses(
        clauses: Iterable<Clause>,
        onTop: Boolean,
    ): SideEffect.AddStaticClauses = adding { factory.addStaticClauses(clauses, onTop) }

    override fun addStaticClauses(
        clauses: Sequence<Clause>,
        onTop: Boolean,
    ): SideEffect.AddStaticClauses = adding { factory.addStaticClauses(clauses, onTop) }

    override fun addStaticClauses(
        vararg clauses: Clause,
        onTop: Boolean,
    ): SideEffect.AddStaticClauses = adding { factory.addStaticClauses(*clauses, onTop = onTop) }

    override fun removeStaticClauses(clauses: Iterable<Clause>): SideEffect.RemoveStaticClauses =
        adding { factory.removeStaticClauses(clauses) }

    override fun removeStaticClauses(clauses: Sequence<Clause>): SideEffect.RemoveStaticClauses =
        adding { factory.removeStaticClauses(clauses) }

    override fun removeStaticClauses(vararg clauses: Clause): SideEffect.RemoveStaticClauses =
        adding { factory.removeStaticClauses(*clauses) }

    override fun resetDynamicKb(clauses: Iterable<Clause>): SideEffect.ResetDynamicKb = adding { factory.resetDynamicKb(clauses) }

    override fun resetDynamicKb(clauses: Sequence<Clause>): SideEffect.ResetDynamicKb = adding { factory.resetDynamicKb(clauses) }

    override fun resetDynamicKb(vararg clauses: Clause): SideEffect.ResetDynamicKb = adding { factory.resetDynamicKb(*clauses) }

    override fun addDynamicClauses(
        clauses: Iterable<Clause>,
        onTop: Boolean,
    ): SideEffect.AddDynamicClauses = adding { factory.addDynamicClauses(clauses, onTop) }

    override fun addDynamicClauses(
        clauses: Sequence<Clause>,
        onTop: Boolean,
    ): SideEffect.AddDynamicClauses = adding { factory.addDynamicClauses(clauses, onTop) }

    override fun addDynamicClauses(
        vararg clauses: Clause,
        onTop: Boolean,
    ): SideEffect.AddDynamicClauses = adding { factory.addDynamicClauses(*clauses, onTop = onTop) }

    override fun removeDynamicClauses(clauses: Iterable<Clause>): SideEffect.RemoveDynamicClauses =
        adding { factory.removeDynamicClauses(clauses) }

    override fun removeDynamicClauses(clauses: Sequence<Clause>): SideEffect.RemoveDynamicClauses =
        adding { factory.removeDynamicClauses(clauses) }

    override fun removeDynamicClauses(vararg clauses: Clause): SideEffect.RemoveDynamicClauses =
        adding { factory.removeDynamicClauses(*clauses) }

    override fun setFlags(flags: Map<String, Term>): SideEffect.SetFlags = adding { factory.setFlags(flags) }

    override fun setFlags(vararg flags: Pair<String, Term>): SideEffect.SetFlags = adding { factory.setFlags(*flags) }

    override fun setFlag(
        name: String,
        value: Term,
    ): SideEffect.SetFlags = adding { factory.setFlag(name, value) }

    override fun resetFlags(flags: Map<String, Term>): SideEffect.ResetFlags = adding { factory.resetFlags(flags) }

    override fun resetFlags(vararg flags: Pair<String, Term>): SideEffect.ResetFlags = adding { factory.resetFlags(*flags) }

    override fun clearFlags(names: Iterable<String>): SideEffect.ClearFlags = adding { factory.clearFlags(names) }

    override fun clearFlags(names: Sequence<String>): SideEffect.ClearFlags = adding { factory.clearFlags(names) }

    override fun clearFlags(vararg names: String): SideEffect.ClearFlags = adding { factory.clearFlags(*names) }

    override fun loadLibrary(library: Library): SideEffect.LoadLibrary = adding { factory.loadLibrary(library) }

    override fun updateLibrary(library: Library): SideEffect.UpdateLibrary = adding { factory.updateLibrary(library) }

    override fun setOperators(operators: Iterable<Operator>): SideEffect.SetOperators = adding { factory.setOperators(operators) }

    override fun setOperators(operators: Sequence<Operator>): SideEffect.SetOperators = adding { factory.setOperators(operators) }

    override fun setOperators(vararg operators: Operator): SideEffect.SetOperators = adding { factory.setOperators(*operators) }

    override fun resetOperators(operators: Iterable<Operator>): SideEffect.ResetOperators = adding { factory.resetOperators(operators) }

    override fun resetOperators(operators: Sequence<Operator>): SideEffect.ResetOperators = adding { factory.resetOperators(operators) }

    override fun resetOperators(vararg operators: Operator): SideEffect.ResetOperators = adding { factory.resetOperators(*operators) }

    override fun removeOperators(operators: Iterable<Operator>): SideEffect.RemoveOperators = adding { factory.removeOperators(operators) }

    override fun removeOperators(operators: Sequence<Operator>): SideEffect.RemoveOperators = adding { factory.removeOperators(operators) }

    override fun removeOperators(vararg operators: Operator): SideEffect.RemoveOperators = adding { factory.removeOperators(*operators) }

    override fun openInputChannels(inputChannels: Map<String, InputChannel<String>>): SideEffect.OpenInputChannels =
        adding { factory.openInputChannels(inputChannels) }

    override fun openInputChannels(vararg inputChannels: Pair<String, InputChannel<String>>): SideEffect.OpenInputChannels =
        adding { factory.openInputChannels(*inputChannels) }

    override fun openInputChannel(
        name: String,
        inputChannel: InputChannel<String>,
    ): SideEffect.OpenInputChannels = adding { factory.openInputChannel(name, inputChannel) }

    override fun resetInputChannels(vararg inputChannels: Pair<String, InputChannel<String>>): SideEffect.ResetInputChannels =
        adding { factory.resetInputChannels(*inputChannels) }

    override fun closeInputChannels(names: Iterable<String>): SideEffect.CloseInputChannels = adding { factory.closeInputChannels(names) }

    override fun closeInputChannels(names: Sequence<String>): SideEffect.CloseInputChannels = adding { factory.closeInputChannels(names) }

    override fun closeInputChannels(vararg names: String): SideEffect.CloseInputChannels = adding { factory.closeInputChannels(*names) }

    override fun openOutputChannels(outputChannels: Map<String, OutputChannel<String>>): SideEffect.OpenOutputChannels =
        adding { factory.openOutputChannels(outputChannels) }

    override fun openOutputChannels(vararg outputChannels: Pair<String, OutputChannel<String>>): SideEffect.OpenOutputChannels =
        adding { factory.openOutputChannels(*outputChannels) }

    override fun openOutputChannel(
        name: String,
        outputChannel: OutputChannel<String>,
    ): SideEffect.OpenOutputChannels = adding { factory.openOutputChannel(name, outputChannel) }

    override fun resetOutputChannels(outputChannels: Map<String, OutputChannel<String>>): SideEffect.ResetOutputChannels =
        adding { factory.resetOutputChannels(outputChannels) }

    override fun resetOutputChannels(vararg outputChannels: Pair<String, OutputChannel<String>>): SideEffect.ResetOutputChannels =
        adding { factory.resetOutputChannels(*outputChannels) }

    override fun closeOutputChannels(names: Iterable<String>): SideEffect.CloseOutputChannels =
        adding { factory.closeOutputChannels(names) }

    override fun closeOutputChannels(names: Sequence<String>): SideEffect.CloseOutputChannels =
        adding { factory.closeOutputChannels(names) }

    override fun closeOutputChannels(vararg names: String): SideEffect.CloseOutputChannels = adding { factory.closeOutputChannels(*names) }

    override fun unloadLibraries(aliases: Iterable<String>): SideEffect.UnloadLibraries = adding { factory.unloadLibraries(aliases) }

    override fun unloadLibraries(aliases: Sequence<String>): SideEffect.UnloadLibraries = adding { factory.unloadLibraries(aliases) }

    override fun unloadLibraries(vararg aliases: String): SideEffect.UnloadLibraries = adding { factory.unloadLibraries(*aliases) }

    override fun resetRuntime(libraries: Runtime): SideEffect.ResetRuntime = adding { factory.resetRuntime(libraries) }

    override fun resetRuntime(libraries: Iterable<Library>): SideEffect.ResetRuntime = adding { factory.resetRuntime(libraries) }

    override fun resetRuntime(libraries: Sequence<Library>): SideEffect.ResetRuntime = adding { factory.resetRuntime(libraries) }

    override fun resetRuntime(vararg libraries: Library): SideEffect.ResetRuntime = adding { factory.resetRuntime(*libraries) }

    override fun addLibraries(libraries: Runtime): SideEffect.AddLibraries = adding { factory.addLibraries(libraries) }

    override fun addLibraries(libraries: Iterable<Library>): SideEffect.AddLibraries = adding { factory.addLibraries(libraries) }

    override fun addLibraries(libraries: Sequence<Library>): SideEffect.AddLibraries = adding { factory.addLibraries(libraries) }

    override fun addLibraries(vararg libraries: Library): SideEffect.AddLibraries = adding { factory.addLibraries(*libraries) }

    override fun resetInputChannels(inputChannels: Iterable<Pair<String, InputChannel<String>>>): SideEffect.ResetInputChannels =
        adding { factory.resetInputChannels(inputChannels) }

    override fun resetInputChannels(inputChannels: Sequence<Pair<String, InputChannel<String>>>): SideEffect.ResetInputChannels =
        adding { factory.resetInputChannels(inputChannels) }

    override fun resetInputChannels(inputChannels: Map<String, InputChannel<String>>): SideEffect.ResetInputChannels =
        adding { factory.resetInputChannels(inputChannels) }

    override fun resetOutputChannels(outputChannels: Iterable<Pair<String, OutputChannel<String>>>): SideEffect.ResetOutputChannels =
        adding { factory.resetOutputChannels(outputChannels) }

    override fun resetOutputChannels(outputChannels: Sequence<Pair<String, OutputChannel<String>>>): SideEffect.ResetOutputChannels =
        adding { factory.resetOutputChannels(outputChannels) }

    override fun addEphemeralData(
        key: String,
        value: Any,
    ): SideEffect.SetEphemeralData = adding { factory.addEphemeralData(key, value) }

    override fun <X> addEphemeralData(data: Map<String, X>): SideEffect.SetEphemeralData = adding { factory.addEphemeralData(data) }

    override fun <X> addEphemeralData(vararg data: Pair<String, X>): SideEffect.SetEphemeralData =
        adding { factory.addEphemeralData(*data) }

    override fun setEphemeralData(
        key: String,
        value: Any,
    ): SideEffect.SetEphemeralData = adding { factory.setEphemeralData(key, value) }

    override fun <X> setEphemeralData(data: Map<String, X>): SideEffect.SetEphemeralData = adding { factory.setEphemeralData(data) }

    override fun <X> setEphemeralData(vararg data: Pair<String, X>): SideEffect.SetEphemeralData =
        adding { factory.setEphemeralData(*data) }

    override fun addDurableData(
        key: String,
        value: Any,
    ): SideEffect.SetDurableData = adding { factory.addDurableData(key, value) }

    override fun <X> addDurableData(data: Map<String, X>): SideEffect.SetDurableData = adding { factory.addDurableData(data) }

    override fun <X> addDurableData(vararg data: Pair<String, X>): SideEffect.SetDurableData = adding { factory.addDurableData(*data) }

    override fun setDurableData(
        key: String,
        value: Any,
    ): SideEffect.SetDurableData = adding { factory.setDurableData(key, value) }

    override fun <X> setDurableData(data: Map<String, X>): SideEffect.SetDurableData = adding { factory.setDurableData(data) }

    override fun <X> setDurableData(vararg data: Pair<String, X>): SideEffect.SetDurableData = adding { factory.setDurableData(*data) }

    override fun addPersistentData(
        key: String,
        value: Any,
    ): SideEffect.SetPersistentData = adding { factory.addPersistentData(key, value) }

    override fun <X> addPersistentData(data: Map<String, X>): SideEffect.SetPersistentData = adding { factory.addPersistentData(data) }

    override fun <X> addPersistentData(vararg data: Pair<String, X>): SideEffect.SetPersistentData =
        adding { factory.addPersistentData(*data) }

    override fun setPersistentData(
        key: String,
        value: Any,
    ): SideEffect.SetPersistentData = adding { factory.setPersistentData(key, value) }

    override fun <X> setPersistentData(data: Map<String, X>): SideEffect.SetPersistentData = adding { factory.setPersistentData(data) }

    override fun <X> setPersistentData(vararg data: Pair<String, X>): SideEffect.SetPersistentData =
        adding { factory.setPersistentData(*data) }
}
