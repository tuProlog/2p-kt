package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Term

interface TermDeobjectifier<T> : Deobjectifier<Term, T>