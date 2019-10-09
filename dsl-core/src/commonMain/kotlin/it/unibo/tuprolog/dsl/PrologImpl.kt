package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Scope

class PrologImpl : Prolog, Scope by Scope.empty()