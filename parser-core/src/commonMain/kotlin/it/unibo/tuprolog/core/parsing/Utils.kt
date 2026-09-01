package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.operators.Specifier
import it.unibo.tuprolog.parser.operators.Associativity
import it.unibo.tuprolog.parser.operators.OperatorDefinition
import it.unibo.tuprolog.parser.operators.OperatorTable
import it.unibo.tuprolog.parser.operators.OperatorTables

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

fun Operator.toDefinition(): OperatorDefinition = OperatorDefinition(functor, specifier.toAssociativity(), priority)

fun OperatorDefinition.toOperator(): Operator = Operator(name, specifier.toSpecifier(), priority)

fun OperatorSet.toOperatorTable(): OperatorTable = OperatorTables.of(map { it.toDefinition() })
