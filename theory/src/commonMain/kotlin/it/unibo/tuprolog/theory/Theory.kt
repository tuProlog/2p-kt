package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Struct

interface Theory : Iterable<Clause> {
    operator fun plus(theory: Theory): Theory
    operator fun plus(clause: Clause): Theory

    operator fun contains(clause: Clause): Boolean
    operator fun contains(head: Struct): Boolean

    operator fun get(clause: Clause): Clause
    operator fun get(head: Struct): Clause

    fun assertA(clause: Clause): Theory
    fun assertZ(clause: Clause): Theory
    fun retract(clause: Clause): Theory
    fun retractAll(head: Struct): Theory
}