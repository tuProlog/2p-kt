package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.ClauseDatabase
import it.unibo.tuprolog.theory.RetractResult

internal class MutableClassicSolver(
    libraries: Libraries = Libraries(),
    flags: PrologFlags = emptyMap(),
    staticKB: ClauseDatabase = ClauseDatabase.empty(),
    dynamicKB: ClauseDatabase = ClauseDatabase.empty(),
    inputChannels: PrologInputChannels<*> = ExecutionContextAware.defaultInputChannels(),
    outputChannels: PrologOutputChannels<*> = ExecutionContextAware.defaultOutputChannels()
) : ClassicSolver(libraries, flags, staticKB, dynamicKB, inputChannels, outputChannels), MutableSolver {

    override fun loadLibrary(library: AliasedLibrary) {
        TODO("Not yet implemented")
    }

    override fun unloadLibrary(library: AliasedLibrary) {
        TODO("Not yet implemented")
    }

    override fun setLibraries(libraries: Libraries) {
        TODO("Not yet implemented")
    }

    override fun loadStaticKb(theory: ClauseDatabase) {
        TODO("Not yet implemented")
    }

    override fun appendStaticKb(theory: ClauseDatabase) {
        TODO("Not yet implemented")
    }

    override fun resetStaticKb() {
        TODO("Not yet implemented")
    }

    override fun loadDynamicKb(theory: ClauseDatabase) {
        TODO("Not yet implemented")
    }

    override fun appendDynamicKb(theory: ClauseDatabase) {
        TODO("Not yet implemented")
    }

    override fun resetDynamicKb() {
        TODO("Not yet implemented")
    }

    override fun assertA(clause: Clause) {
        TODO("Not yet implemented")
    }

    override fun assertA(fact: Struct) {
        TODO("Not yet implemented")
    }

    override fun assertZ(clause: Clause) {
        TODO("Not yet implemented")
    }

    override fun assertZ(fact: Struct) {
        TODO("Not yet implemented")
    }

    override fun retract(clause: Clause): RetractResult {
        TODO("Not yet implemented")
    }

    override fun retract(fact: Struct): RetractResult {
        TODO("Not yet implemented")
    }

    override fun retractAll(clause: Clause): List<RetractResult> {
        TODO("Not yet implemented")
    }

    override fun retractAll(fact: Struct): List<RetractResult> {
        TODO("Not yet implemented")
    }

}