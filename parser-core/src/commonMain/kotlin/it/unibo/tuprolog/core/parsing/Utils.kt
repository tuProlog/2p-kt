package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.operators.Specifier
import it.unibo.tuprolog.parser.exceptions.PrologSyntaxException
import it.unibo.tuprolog.parser.operators.Associativity
import it.unibo.tuprolog.parser.operators.OperatorDefinition
import it.unibo.tuprolog.parser.operators.OperatorTable
import it.unibo.tuprolog.parser.operators.OperatorTables

/** Converts a parser-level Prolog associativity specifier to its core equivalent. */
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

/** Converts a core Prolog operator specifier to its parser-level equivalent. */
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

/**
 * Converts a core operator to the representation consumed by `parser-impl`.
 *
 * @throws it.unibo.tuprolog.parser.exceptions.InvalidOperatorDefinitionException if the name is
 * empty or the priority is outside `1..1200`
 */
fun Operator.toDefinition(): OperatorDefinition = OperatorDefinition(functor, specifier.toAssociativity(), priority)

/** Converts a parser-level operator definition to its core equivalent. */
fun OperatorDefinition.toOperator(): Operator = Operator(name, specifier.toSpecifier(), priority)

/**
 * Creates an immutable parser-level table containing all operators in this set.
 *
 * @throws it.unibo.tuprolog.parser.exceptions.InvalidOperatorDefinitionException if any converted
 * definition is invalid
 */
fun OperatorSet.toOperatorTable(): OperatorTable = OperatorTables.of(map { it.toDefinition() })

internal fun PrologSyntaxException.toParseException(input: Any?): ParseException =
    ParseException(
        input = input,
        offendingSymbol = offendingText,
        line = span.start.line + 1,
        column = span.start.column + 1,
        message = message,
        throwable = this,
    )
