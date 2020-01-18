package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.parser.PrologParserBaseVisitor
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.parser.PrologParser
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

    override fun visitClause(ctx: PrologParser.ClauseContext): Term {
        return super.visitClause(ctx)
    }

    override fun visitExpression(ctx: PrologParser.ExpressionContext): Term {
        return super.visitExpression(ctx)
    }

    override fun visitOuter(ctx: PrologParser.OuterContext): Term {
        return super.visitOuter(ctx)
    }

    override fun visitOp(ctx: PrologParser.OpContext): Term {
        return super.visitOp(ctx)
    }

    override fun visitTerm(ctx: PrologParser.TermContext): Term {
        return super.visitTerm(ctx)
    }

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

    private fun handleOuters(expression: Term, outers : Stream<PrologParser.OuterContext>): Term {
       TODO()
    }

}