package it.unibo.tuprolog.core.parsing



import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.operators.Specifier
import it.unibo.tuprolog.parser.PrologParser
import it.unibo.tuprolog.parser.PrologParserBaseListener
import it.unibo.tuprolog.parser.dynamic.Associativity
import java.lang.ref.WeakReference


internal class DynamicOpListener private constructor(
    parser: PrologParser,
    private val operatorDefinedCallback: ((Operator) -> OperatorSet)?
) :
    PrologParserBaseListener() {
    private val parser: WeakReference<PrologParser> = WeakReference(parser)

    override fun exitClause(ctx: PrologParser.ClauseContext) {
        val expr: PrologParser.ExpressionContext = ctx.expression()
        if (ctx.exception != null) {
            return
        }
        if (expr.op != null && ":-" == expr.op.symbol.text && Associativity.PREFIX.contains(expr.associativity)) {
            val directive = ctx.accept(PrologExpressionVisitor.instance) as Struct
            if (directive.arity === 1 && directive.getArgAt(0) is Struct) {
                val op: Struct = directive.getArgAt(0) as Struct
                if ("op" == op.functor && op.arity === 3 && op.getArgAt(0) is Number && op.getArgAt(1).isAtom && op.getArgAt(
                        2
                    ).isAtom
                ) {
                    val number = op.getArgAt(0) as Numeric
                    val priority: Int = kotlin.math.min(
                        PrologParser.TOP,
                        kotlin.math.max(
                            PrologParser.BOTTOM,
                            number.intValue as Int
                        )
                    )
                    val struct1 = op.getArgAt(1) as Struct
                    val associativity: Associativity =
                        Associativity.valueOf(struct1.functor.toUpperCase())
                    val struct2 = op.getArgAt(2) as Struct
                    val functor: String = struct2.functor
                    (parser.get())
                        ?.addOperator(functor, associativity, priority)
                    onOperatorDefined(Operator(functor, Specifier.valueOf(associativity.name), priority))
                }
            }
        }
    }

    private fun onOperatorDefined(operator: Operator) {
        operatorDefinedCallback?.invoke(operator)
    }

    companion object {
        fun of(parser: PrologParser): DynamicOpListener {
            return DynamicOpListener(parser, null)
        }

        fun of(
            parser: PrologParser,
            operatorDefinedCallback: (Operator) -> OperatorSet
        ): DynamicOpListener {
            return DynamicOpListener(parser, operatorDefinedCallback)
        }
    }

}