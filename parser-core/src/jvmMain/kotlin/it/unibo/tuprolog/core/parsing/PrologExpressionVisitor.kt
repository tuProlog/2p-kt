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
import kotlin.streams.toList

class PrologExpressionVisitor private constructor(): PrologParserBaseVisitor<Term>() {

    //SINGLETON
    private object GetInstance{
        val INSTANCE = PrologExpressionVisitor()
    }
    companion object {
        val instance: PrologExpressionVisitor by lazy {
            GetInstance.INSTANCE
        }
    }

    //VARIABLES
    private val variables: MutableMap<String,Var> = HashMap<String,Var>()

    override fun visitSingletonTerm(ctx: PrologParser.SingletonTermContext): Term =
        visitTerm(ctx.term())

    override fun visitSingletonExpression(ctx: PrologParser.SingletonExpressionContext): Term =
        visitExpression(ctx.expression())

    override fun visitClause(ctx: PrologParser.ClauseContext): Term =
        ctx.expression().accept(this)

    override fun visitExpression(ctx: PrologParser.ExpressionContext): Term {
        return when{
            ctx.isTerm -> visitTerm(ctx.left)
            INFIX.contains(ctx.associativity) -> visitInfixExpression(ctx)
            POSTFIX.contains(ctx.associativity) -> visitPostfixExpression(ctx)
            PREFIX.contains(ctx.associativity) -> visitPrefixExpression(ctx)
            ctx.exception != null -> throw ctx.exception
            else -> throw java.lang.IllegalArgumentException()
        }
    }

    override fun visitTerm(ctx: PrologParser.TermContext): Term =
        if(ctx.isExpr) visitExpression(ctx.expression()) else ctx.children.get(0).accept(this)


    override fun visitNumber(ctx: PrologParser.NumberContext): Term =
        super.visitNumber(ctx)

    override fun visitInteger(ctx: PrologParser.IntegerContext): Term {
        val value = parseInteger(ctx)
        return Numeric.of(value)
    }

    override fun visitReal(ctx: PrologParser.RealContext): Term {
        var raw = ctx.value.text
        if (ctx.sign != null) {
            raw = ctx.sign.text.toString() + raw
        }
        return try {
            Real.of(raw.toDouble())
        } catch (notAFloating: NumberFormatException) {
            throw ParseException(ctx.value, notAFloating)
        }
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
        val terms: Stream<Term> = ctx.items.stream().map(this::visitExpression)
        return if(ctx.hasTail) createListExact(Stream.concat(terms,Stream.of(this.visitExpression(ctx.tail)))) else it.unibo.tuprolog.core.List.from(terms.asSequence())
    }



    override fun visitSet(ctx: PrologParser.SetContext): Term {
        return if(ctx.length==1)
            it.unibo.tuprolog.core.Set.of(ctx.items[0].accept(this))
        else
            it.unibo.tuprolog.core.Set.of(ctx.items.stream().map(this::visitExpression).asSequence())
    }


    //PRIVATE METHODS
    private fun parseInteger(ctx: PrologParser.IntegerContext): Number{
        val str = ctx.value.text
        val base: Int
        var clean: String
        if (ctx.isBin) {
            base = 2
            clean = str.substring(2)
        } else if (ctx.isOct) {
            base = 8
            clean = str.substring(2)
        } else if (ctx.isHex) {
            base = 16
            clean = str.substring(2)
        } else if (ctx.isChar) {
            clean = str.substring(2)
            if (clean.length != 1) {
                throw ParseException(
                    null,
                    ctx.text,
                    ctx.value.line,
                    ctx.value.charPositionInLine,
                    "Invalid character literal: " + ctx.text,
                    null
                )
            }
            return clean[0].toInt()
        } else {
            base = 10
            clean = str
        }
        if (ctx.sign != null) {
            clean = ctx.sign.text.toString() + clean
        }
        return try {
            clean.toInt(base)
        } catch (mayBeLong: NumberFormatException) {
            try {
                clean.toLong(base)
            } catch (notEvenLong: NumberFormatException) {
                throw ParseException(ctx.value, notEvenLong)
            }
        }
    }

    private fun createListExact(terms: Stream<Term>): Struct {
        val termsList: List<Term> = terms.collect(Collectors.toList<Term>())
        var i = termsList.size - 1
        var result: Struct = Cons.of(termsList[i - 1], termsList[i])
        i -= 2
        while (i >= 0) {
            result = Cons.of(termsList[i], result)
            i--
        }
        return result
    }

    private fun visitPostfixExpression(ctx: PrologParser.ExpressionContext): Term =
        postfix(ctx.left.accept(this), ctx.operators.stream().map{
            it->it.symbol.text
        }.asSequence())

    private fun postfix(term: Term,ops: Sequence<String>) :Term {
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
        }.asSequence())

    private fun prefix(term: Term, ops: Sequence<String>): Term{
        val operators = ops.toList()
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
        val operands= Stream.concat(Stream.of(ctx.left),ctx.right.stream()).map{
            it -> it.accept(this)
        }.asSequence()
        val operators = ctx.operators.stream().map{
            op -> op.symbol.text
        }.asSequence()
        return infixNonAssociative(operands,operators)
    }

    private fun infixNonAssociative(terms: Sequence<Term>,ops: Sequence<String>) : Term{
        val operands: List<Term> = terms.toList()
        val operators: List<String> = ops.toList()
        return Struct.of(operators[0], operands[0], operands[1])
    }

    private fun handleOuters(expression: Term, outers : Stream<PrologParser.OuterContext>): Term {
        var result = expression
        outers.forEach{
            val operands = Stream.concat(
                Stream.of(result),
                it.right.stream().map {
                    it -> it.accept(this)
                }
            ).asSequence()
            val operators = it.operators.stream().map{
                op -> op.symbol.text
            }.asSequence()
            result =  when(it.associativity){
                XFY -> infixRight(operands,operators)
                XF,YF -> postfix(result,operators)
                XFX -> infixNonAssociative(operands,operators)
                YFX -> infixLeft(operands,operators)
                FX, FY -> prefix(result,operators)
           }
        }
        return result
    }

    //Prova refactoring sequence Kotlin
    private fun infixRight(terms: Sequence<Term>, ops: Sequence<String>): Term {
        val operands = terms.toList()
        val operators = ops.toList()
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

    private fun infixLeft(terms: Sequence<Term>, ops: Sequence<String>): Term {
        val operands: List<Term> = terms.toList()
        val operators = ops.toList()
        var i = 0
        var j = 0
        var result: Term = Struct.of(operators[j++], operands[i++], operands[i++])
        while (i < operands.size) {
            result = Struct.of(operators[j++], result, operands[i])
            i++
        }
        return result
    }

    private fun sequenceOfOperands(ctx: PrologParser.ExpressionContext ): Sequence<Term> =
        Stream.concat(Stream.of(ctx.left),ctx.right.stream().map{
            it -> it.accept(this)
        }).asSequence() as Sequence<Term>

    private fun sequenceOfOperators(ctx : PrologParser.ExpressionContext) : Sequence<String> =
        ctx.operators.stream().map{
            op -> op.symbol.text
        }.asSequence()


    private fun visitInfixRightAssociativeExpression(ctx: PrologParser.ExpressionContext) : Term =
        infixRight(sequenceOfOperands(ctx),sequenceOfOperators(ctx))

    private fun visitInfixLeftAssociativeExpression(ctx: PrologParser.ExpressionContext) : Term =
        infixLeft(sequenceOfOperands(ctx),sequenceOfOperators(ctx))

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