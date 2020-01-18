package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.parser.PrologParser
import it.unibo.tuprolog.parser.PrologParserBaseVisitor
import it.unibo.tuprolog.parser.dynamic.Associativity.*
import it.unibo.tuprolog.parser.dynamic.Associativity.Companion.INFIX
import it.unibo.tuprolog.parser.dynamic.Associativity.Companion.POSTFIX
import it.unibo.tuprolog.parser.dynamic.Associativity.Companion.PREFIX
import java.util.stream.Collectors
import java.util.stream.Stream
import kotlin.streams.asSequence

class PrologExpressionVisitor private constructor(): PrologParserBaseVisitor<Term>() {

    private object GetInstance{
        val INSTANCE = PrologExpressionVisitor()
    }

    companion object {
        val instance: PrologExpressionVisitor by lazy {
            GetInstance.INSTANCE
        }
    }

    private val variables: MutableMap<String,Var> = HashMap<String,Var>()

    override fun visitSingletonTerm(ctx: PrologParser.SingletonTermContext): Term =
        visitTerm(ctx.term())

    override fun visitSingletonExpression(ctx: PrologParser.SingletonExpressionContext): Term =
        visitExpression(ctx.expression())

    override fun visitClause(ctx: PrologParser.ClauseContext): Term =
        ctx.expression().accept(this)

    override fun visitExpression(ctx: PrologParser.ExpressionContext): Term {
        return if(ctx.isTerm)
            this.visitTerm(ctx.left)
        else if(INFIX.contains(ctx.associativity))
            visitInfixExpression(ctx)
        else if(POSTFIX.contains(ctx.associativity))
            visitPostfixExpression(ctx)
        else if(PREFIX.contains(ctx.associativity))
            visitPrefixExpression(ctx)
        else if(ctx.exception!=null)
            throw ctx.exception
        else
            throw IllegalArgumentException()
    }

    override fun visitTerm(ctx: PrologParser.TermContext): Term =
        if(ctx.isExpr) visitExpression(ctx.expression()) else ctx.children.get(0).accept(this)


    override fun visitNumber(ctx: PrologParser.NumberContext): Term =
        super.visitNumber(ctx)

    override fun visitInteger(ctx: PrologParser.IntegerContext): Term {
        return super.visitInteger(ctx)
    }

    override fun visitReal(ctx: PrologParser.RealContext): Term {
        return super.visitReal(ctx)
    }

    override fun visitVariable(ctx: PrologParser.VariableContext): Term =
        getVarByName(ctx.value.text)

    override fun visitStructure(ctx: PrologParser.StructureContext): Term{
        if (ctx.isList)
            return it.unibo.tuprolog.core.List.empty()
        else if (ctx.isSet)
            return it.unibo.tuprolog.core.Set.empty()
        if (ctx.arity === 0) {
            return it.unibo.tuprolog.core.Atom.of(ctx.functor.text)
        } else {
            return Struct.of(
                ctx.functor.text,
                ctx.args.stream().map(this::visitExpression).asSequence()
            )
        }
    }

    override fun visitList(ctx: PrologParser.ListContext): Term {
        return super.visitList(ctx)
    }

    override fun visitSet(ctx: PrologParser.SetContext): Term {
        return if(ctx.length==1)
            it.unibo.tuprolog.core.Set.of(ctx.items[0].accept(this))
        else
            it.unibo.tuprolog.core.Set.of(ctx.items.stream().map(this::visitExpression).asSequence())
    }

    private fun visitPostfixExpression(ctx: PrologParser.ExpressionContext): Term =
        postfix(ctx.left.accept(this), ctx.operators.stream().map{
            it->it.symbol.text
        })

    private fun postfix(term: Term,ops: Stream<String>) :Term {
        val operator = ops.iterator()
        var result: Term = Struct.of(operator.next(), term)
        while (operator.hasNext()) {
            result = Struct.of(operator.next(), result)
        }
        return result
    }

    private fun visitPrefixExpression(ctx: PrologParser.ExpressionContext): Term =
        prefix(ctx.right[0].accept(this),ctx.operators.stream().map{
            it -> it.symbol.text
        })

    private fun prefix(term: Term, ops: Stream<String>): Term{
        val operators =
            ops.collect(Collectors.toList())
        var i = operators.size - 1
        var result: Term = Struct.of(operators[i--], term)
        while (i >= 0) {
            result = Struct.of(operators[i], result)
            i--
        }
        return result
    }

    private fun visitInfixExpression(ctx: PrologParser.ExpressionContext) : Term{
        return when(ctx.associativity){
            XFY -> visitInfixRightAssociativeExpression(ctx)
            YFX -> visitInfixLeftAssociativeExpression(ctx)
            XFX -> visitInfixNonAssociativeExpression(ctx)
            else -> throw IllegalStateException()
        }
    }

    private fun visitInfixNonAssociativeExpression(ctx: PrologParser.ExpressionContext): Term{
        val operands:Stream<Term> = Stream.concat(Stream.of(ctx.left),ctx.right.stream()).map{
            it -> it.accept(this)
        }
        val operators: Stream<String> = ctx.operators.stream().map{
            op -> op.symbol.text
        }
        return infixNonAssociative(operands,operators)
    }

    private fun infixNonAssociative(terms: Stream<Term>,ops: Stream<String>) : Term{
        val operands: List<Term> = terms.collect(Collectors.toList())
        val operators: List<String> = ops.collect(Collectors.toList())
        return Struct.of(operators[0], operands[0], operands[1])
    }


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

    private fun infixRight(terms: Stream<Term>, ops: Stream<String>): Term {
        val operands: List<Term> = terms.collect(Collectors.toList<Term>())
        val operators = ops.collect(
            Collectors.toList()
        )
        var i = operands.size - 1
        var j = operators.size - 1
        var result: Term = Struct.of(operators[j--], operands[i - 1], operands[i])
        i -= 2
        while (i >= 0) {
            result = Struct.of(operators[j--], operands[i], result)
            i--
        }
        return result
    }

    private fun infixLeft(terms: Stream<Term>, ops: Stream<String>): Term {
        val operands: List<Term> = terms.collect(Collectors.toList<Term>())
        val operators =
            ops.collect(Collectors.toList())
        var i = 0
        var j = 0
        var result: Term = Struct.of(operators[j++], operands[i++], operands[i++])
        while (i < operands.size) {
            result = Struct.of(operators[j++], result, operands[i])
            i++
        }
        return result
    }

    private fun streamOfOperands(ctx: PrologParser.ExpressionContext ): Stream<Term> =
        Stream.concat(Stream.of(ctx.left),ctx.right.stream().map{
            it -> it.accept(this)
        })as Stream<Term>

    private fun streamOfOperators(ctx : PrologParser.ExpressionContext) : Stream<String> =
        ctx.operators.stream().map{
            op -> op.symbol.text
        }


    private fun visitInfixRightAssociativeExpression(ctx: PrologParser.ExpressionContext) : Term =
        infixRight(streamOfOperands(ctx),streamOfOperators(ctx))

    private fun visitInfixLeftAssociativeExpression(ctx: PrologParser.ExpressionContext) : Term =
        infixLeft(streamOfOperands(ctx),streamOfOperators(ctx))

    private fun getVarByName(name: String): Var {
        return if ("_" == name) {
            Var.of(name)
        } else {
            var variable: Var = variables.getValue(name)
            if (variable == null) {
                variables[name] = Var.of(name).also { variable = it }
            }
            return variable
        }
    }


}