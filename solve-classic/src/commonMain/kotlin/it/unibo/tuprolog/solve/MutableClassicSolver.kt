package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.flags.NotableFlag
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.RetractResult
import it.unibo.tuprolog.theory.Theory

internal class MutableClassicSolver(
    libraries: Libraries = Libraries(),
    flags: FlagStore = FlagStore.EMPTY,
    staticKb: Theory = Theory.empty(),
    dynamicKb: Theory = Theory.empty(),
    inputChannels: InputStore<*> = ExecutionContextAware.defaultInputChannels(),
    outputChannels: OutputStore<*> = ExecutionContextAware.defaultOutputChannels()
) : ClassicSolver(libraries, flags, staticKb, dynamicKb, inputChannels, outputChannels), MutableSolver {

    override fun loadLibrary(library: AliasedLibrary) {
        updateContext {
            copy(libraries = libraries + library)
        }
    }

    override fun unloadLibrary(library: AliasedLibrary) {
        updateContext {
            copy(libraries = libraries - library)
        }
    }

    override fun setLibraries(libraries: Libraries) {
        updateContext {
            copy(libraries = libraries)
        }
    }

    override fun loadStaticKb(theory: Theory) {
        updateContext {
            copy(staticKb = theory)
        }
    }

    override fun appendStaticKb(theory: Theory) {
        updateContext {
            copy(staticKb = staticKb + theory)
        }
    }

    override fun resetStaticKb() {
        updateContext {
            copy(staticKb = Theory.empty())
        }
    }

    override fun loadDynamicKb(theory: Theory) {
        updateContext {
            copy(dynamicKb = theory)
        }
    }

    override fun appendDynamicKb(theory: Theory) {
        updateContext {
            copy(dynamicKb = dynamicKb + theory)
        }
    }

    override fun resetDynamicKb() {
        updateContext {
            copy(dynamicKb = Theory.empty())
        }
    }

    override fun assertA(clause: Clause) {
        updateContext {
            copy(dynamicKb = dynamicKb.assertA(clause))
        }
    }

    override fun assertA(fact: Struct) {
        updateContext {
            copy(dynamicKb = dynamicKb.assertA(fact))
        }
    }

    override fun assertZ(clause: Clause) {
        updateContext {
            copy(dynamicKb = dynamicKb.assertZ(clause))
        }
    }

    override fun assertZ(fact: Struct) {
        updateContext {
            copy(dynamicKb = dynamicKb.assertZ(fact))
        }
    }

    override fun retract(clause: Clause): RetractResult {
        val result = dynamicKb.retract(clause)
        updateContext {
            copy(dynamicKb = result.theory)
        }
        return result
    }

    override fun retract(fact: Struct): RetractResult {
        val result = dynamicKb.retract(fact)
        updateContext {
            copy(dynamicKb = result.theory)
        }
        return result
    }

    override fun retractAll(clause: Clause): RetractResult {
        val result = dynamicKb.retractAll(clause)
        updateContext {
            copy(dynamicKb = result.theory)
        }
        return result
    }

    override fun retractAll(fact: Struct): RetractResult {
        val result = dynamicKb.retractAll(fact)
        updateContext {
            copy(dynamicKb = result.theory)
        }
        return result
    }

    override fun setFlag(name: String, value: Term) {
        updateContext {
            copy(flags = flags.set(name, value))
        }
    }

    override fun setFlag(flag: Pair<String, Term>) {
        updateContext {
            copy(flags = flags + flag)
        }
    }

    override fun setFlag(flag: NotableFlag) {
        updateContext {
            copy(flags = flags + flag)
        }
    }
}
