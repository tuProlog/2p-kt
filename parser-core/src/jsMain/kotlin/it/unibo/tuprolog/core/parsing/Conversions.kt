package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.operators.Specifier

fun String.toSpecifier(): Specifier =
    Specifier.valueOf(toUpperCase())

fun Specifier.toAssociativity(): String =
    toString()