package it.unibo.tuprolog.core

interface Formatter<T> {
    fun format(value: T): String
}

