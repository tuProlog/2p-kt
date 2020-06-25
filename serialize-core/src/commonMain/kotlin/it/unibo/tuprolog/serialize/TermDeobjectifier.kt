package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Term

interface TermDeobjectifier : Deobjectifier<Term> {

    companion object {
        val instance: TermDeobjectifier = termDeobjectifier()
    }

}