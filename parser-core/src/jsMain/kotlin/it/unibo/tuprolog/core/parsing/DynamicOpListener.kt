package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.Specifier
import it.unibo.tuprolog.parser.Associativity
import it.unibo.tuprolog.parser.ClauseContext
import it.unibo.tuprolog.parser.PrologParser
import it.unibo.tuprolog.parser.PrologParserListener
import kotlin.math.max
import kotlin.math.min
import it.unibo.tuprolog.core.List as LogicList

@Suppress("CyclomaticComplexMethod", "NestedBlockDepth", "MagicNumber")
class DynamicOpListener private constructor(
    private val parser: PrologParser,
    private val operatorDefinedCallback: PrologParser?.(Operator) -> Unit,
) : PrologParserListener() {
    override fun exitClause(ctx: ClauseContext) {
        val expr = ctx.expression()
        if (ctx.exception != null) {
            return
        }
        if (expr._op != null && ":-" == expr._op?.symbol?.text && expr.associativity in Associativity.PREFIX) {
            val directive = ctx.accept(PrologVisitor()) as Directive
            val op = directive.body
            if (op.isOpDirective) {
                val priority = min(1200, max(0, ((op as Struct)[0] as Numeric).intValue.toInt()))
                val specifier = Specifier.fromTerm(op[1])
                when (val operator = op[2]) {
                    is Atom -> {
                        onOperatorDefined(Operator(operator.value, specifier, priority))
                    }
                    is LogicList -> {
                        if (operator.toSequence().all { it is Atom }) {
                            for (it in operator.toSequence().map { it as Atom }) {
                                onOperatorDefined(Operator(it.value, specifier, priority))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun onOperatorDefined(operator: Operator) {
        parser.addOperator(operator.functor, operator.specifier.toAssociativity(), operator.priority)
        parser.operatorDefinedCallback(operator)
    }

    companion object {
        fun of(parser: PrologParser): DynamicOpListener = DynamicOpListener(parser) { }

        fun of(
            parser: PrologParser,
            operatorDefinedCallback: PrologParser?.(Operator) -> Unit,
        ): DynamicOpListener = DynamicOpListener(parser, operatorDefinedCallback)
    }
}
