package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.operators.Specifier

fun String.toSpecifier(): Specifier = Specifier.valueOf(uppercase())

fun Specifier.toAssociativity(): String = toString()
