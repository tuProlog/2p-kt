package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.operators.Specifier
import it.unibo.tuprolog.parser.dynamic.Associativity

fun Associativity.toSpecifier(): Specifier =
    when (this) {
        Associativity.FX -> Specifier.FX
        Associativity.FY -> Specifier.FY
        Associativity.XF -> Specifier.XF
        Associativity.YF -> Specifier.YF
        Associativity.XFX -> Specifier.XFX
        Associativity.YFX -> Specifier.YFX
        Associativity.XFY -> Specifier.XFY
    }

fun Specifier.toAssociativity(): Associativity =
    when (this) {
        Specifier.FX -> Associativity.FX
        Specifier.FY -> Associativity.FY
        Specifier.XF -> Associativity.XF
        Specifier.YF -> Associativity.YF
        Specifier.XFX -> Associativity.XFX
        Specifier.YFX -> Associativity.YFX
        Specifier.XFY -> Associativity.XFY
    }