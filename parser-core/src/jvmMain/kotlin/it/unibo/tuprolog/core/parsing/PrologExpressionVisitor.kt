package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.parser.PrologParserBaseVisitor
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.parser.PrologParser
import it.unibo.tuprolog.parser.dynamic.Associativity.*
import java.util.stream.Stream

class PrologExpressionVisitor private constructor(): PrologParserBaseVisitor<Term>() {

    private object GetInstance{
        val INSTANCE = PrologExpressionVisitor()
    }

    companion object {
        val instance: PrologExpressionVisitor by lazy {
            GetInstance.INSTANCE
        }
    }


    override fun visitSingletonTerm(ctx: PrologParser.SingletonTermContext): Term {
        return super.visitSingletonTerm(ctx)
    }

    override fun visitSingletonExpression(ctx: PrologParser.SingletonExpressionContext): Term {
        return super.visitSingletonExpression(ctx)
    }

    override fun visitTheory(ctx: PrologParser.TheoryContext): Term {
        return super.visitTheory(ctx)
    }

    override fun visitOptClause(ctx: PrologParser.OptClauseContext): Term {
        return super.visitOptClause(ctx)
    }

    override fun visitClause(ctx: PrologParser.ClauseContext): Term =
        ctx.expression().accept(this)

    override fun visitExpression(ctx: PrologParser.ExpressionContext): Term {
        return super.visitExpression(ctx)
    }

    override fun visitOuter(ctx: PrologParser.OuterContext): Term {
        return super.visitOuter(ctx)
    }

    override fun visitOp(ctx: PrologParser.OpContext): Term {
        return super.visitOp(ctx)
    }

    override fun visitTerm(ctx: PrologParser.TermContext): Term =
        if(ctx.isExpr) visitExpression(ctx.expression()) else ctx.children.get(0).accept(this)


    override fun visitNumber(ctx: PrologParser.NumberContext): Term {
        return super.visitNumber(ctx)
    }

    override fun visitInteger(ctx: PrologParser.IntegerContext): Term {
        return super.visitInteger(ctx)
    }

    override fun visitReal(ctx: PrologParser.RealContext): Term {
        return super.visitReal(ctx)
    }

    override fun visitVariable(ctx: PrologParser.VariableContext): Term {
        return super.visitVariable(ctx)
    }

    override fun visitStructure(ctx: PrologParser.StructureContext): Term {
        return super.visitStructure(ctx)
    }

    override fun visitList(ctx: PrologParser.ListContext): Term {
        return super.visitList(ctx)
    }

    override fun visitSet(ctx: PrologParser.SetContext): Term {
        return super.visitSet(ctx)
    }
    private fun wtf() = println("ciao")

    private fun handleOuters(expression: Term, outers : Stream<PrologParser.OuterContext>): Term {
        val result = expression
        outers.forEach{
            val operands = Stream.concat(
                Stream.of(result),
                it.right.stream().map {
                    it -> it.accept(this)
                }
            )
            val operators = it.operators.stream().map{
                op -> op.symbol.text
            }
            result =  when(it.associativity){
                XFY -> TODO()
                XF -> TODO()
                YF -> TODO()
                XFX -> TODO()
                YFX -> TODO()
                FX -> TODO()
                FY -> TODO()
           }
        }
        return result
    }

}