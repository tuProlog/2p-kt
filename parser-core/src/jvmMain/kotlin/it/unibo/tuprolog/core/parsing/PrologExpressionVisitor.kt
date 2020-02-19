package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.parser.PrologParser
import it.unibo.tuprolog.parser.PrologParserBaseVisitor
import it.unibo.tuprolog.parser.dynamic.Associativity.*
import it.unibo.tuprolog.parser.dynamic.Associativity.Companion.INFIX
import it.unibo.tuprolog.parser.dynamic.Associativity.Companion.POSTFIX
import it.unibo.tuprolog.parser.dynamic.Associativity.Companion.PREFIX
import org.gciatto.kt.math.BigInteger

class PrologExpressionVisitor : PrologParserBaseVisitor<Term>() {

    private val scope: Scope = Scope.empty()

    override fun visitSingletonTerm(ctx: PrologParser.SingletonTermContext): Term =
        visitTerm(ctx.term())

    override fun visitSingletonExpression(ctx: PrologParser.SingletonExpressionContext): Term =
        visitExpression(ctx.expression())

    override fun visitClause(ctx: PrologParser.ClauseContext): Term =
        ctx.expression().accept(this)

    override fun visitExpression(ctx: PrologParser.ExpressionContext): Term {
        return handleOuters(
            when {
                ctx.isTerm -> visitTerm(ctx.left)
                INFIX.contains(ctx.associativity) -> visitInfixExpression(ctx)
                POSTFIX.contains(ctx.associativity) -> visitPostfixExpression(ctx)
                PREFIX.contains(ctx.associativity) -> visitPrefixExpression(ctx)
                ctx.exception != null -> throw ctx.exception
                else -> throw IllegalArgumentException() // use kotlin's IllegalArgumentException
            }, flatten(ctx.outers)
        )
    }

    override fun visitTerm(ctx: PrologParser.TermContext): Term =
        if (ctx.isExpr) visitExpression(ctx.expression()) else ctx.children[0].accept(this)


    override fun visitInteger(ctx: PrologParser.IntegerContext): Term {
        val value = parseInteger(ctx)
        return scope.numOf(value)
    }

    override fun visitReal(ctx: PrologParser.RealContext): Term {
        var raw = ctx.value.text
        if (ctx.sign != null) {
            raw = ctx.sign.text + raw
        }
        return try {
            scope.numOf(raw.toDouble())
        } catch (notAFloating: NumberFormatException) {
            throw parseException(ctx.value, notAFloating)
        }
    }

    override fun visitVariable(ctx: PrologParser.VariableContext): Term =
        if (ctx.isAnonymous) {
            scope.anonymous()
        } else {
            scope.varOf(ctx.value.text)
        }

    override fun visitStructure(ctx: PrologParser.StructureContext): Term {
        if (ctx.isList)
            return scope.listOf()
        else if (ctx.isSet)
            return scope.setOf()
        return if (ctx.arity == 0) {
            scope.atomOf(ctx.functor.text)
        } else {
            scope.structOf(
                ctx.functor.text,
                ctx.args.asSequence().map(this::visitExpression)
            )
        }
    }

    override fun visitList(ctx: PrologParser.ListContext): Term {
        val terms = ctx.items.map(this::visitExpression)
        return if (ctx.hasTail) {
            scope.listFrom(terms, ctx.tail.accept(this))
        } else {
            scope.listOf(terms)
        }
    }

    override fun visitSet(ctx: PrologParser.SetContext): Term {
        return if (ctx.length == 1)
            scope.setOf(ctx.items[0].accept(this))
        else
            scope.setOf(ctx.items.map(this::visitExpression))
    }

    private fun parseInteger(ctx: PrologParser.IntegerContext): BigInteger {
        val str = ctx.value.text
        val base: Int
        var clean: String
        when {
            ctx.isBin -> {
                base = 2
                clean = str.substring(2)
            }
            ctx.isOct -> {
                base = 8
                clean = str.substring(2)
            }
            ctx.isHex -> {
                base = 16
                clean = str.substring(2)
            }
            ctx.isChar -> {
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
                return BigInteger.of(clean[0].toInt())
            }
            else -> {
                base = 10
                clean = str
            }
        }
        if (ctx.sign != null) {
            clean = ctx.sign.text + clean
        }
        return BigInteger.of(clean, base)
    }

    private fun visitPostfixExpression(ctx: PrologParser.ExpressionContext): Term =
        postfix(ctx.left.accept(this), ctx.operators.map {
            it.symbol.text
        })

    private fun postfix(term: Term, ops: List<String>): Term {
        val operator = ops.iterator()
        var result: Term = scope.structOf(operator.next(), term)
        while (operator.hasNext()) {
            result = scope.structOf(operator.next(), result)
        }
        return result
    }

    private fun visitPrefixExpression(ctx: PrologParser.ExpressionContext): Term =
        prefix(ctx.right[0].accept(this), ctx.operators.map {
            it.symbol.text
        })

    private fun prefix(term: Term, ops: List<String>): Term {
        var i = ops.size - 1
        var result: Term = scope.structOf(ops[i--], term)
        while (i >= 0) {
            result = scope.structOf(ops[i], result)
            i--
        }
        return result
    }

    private fun visitInfixExpression(ctx: PrologParser.ExpressionContext): Term {
        return when (ctx.associativity) {
            XFY -> visitInfixRightAssociativeExpression(ctx)
            YFX -> visitInfixLeftAssociativeExpression(ctx)
            XFX -> visitInfixNonAssociativeExpression(ctx)
            else -> throw IllegalStateException()
        }
    }

    private fun visitInfixNonAssociativeExpression(ctx: PrologParser.ExpressionContext): Term {
        val operands = (listOf(ctx.left) + ctx.right).map { it.accept(this) }
        val operators = ctx.operators.map { it.symbol.text }
        return infixNonAssociative(operands, operators)
    }

    private fun infixNonAssociative(terms: List<Term>, ops: List<String>): Term {
        return scope.structOf(ops[0], terms[0], terms[1])
    }

    private fun handleOuters(expression: Term, outers: List<PrologParser.OuterContext>): Term {
        var result = expression
        for (o in outers) {
            val operands = listOf(result) + o.right.map { it.accept(this) }
            val operators = o.operators.map { it.symbol.text }
            result = when (o.associativity!!) {
                XFY -> infixRight(operands, operators)
                XF, YF -> postfix(result, operators)
                XFX -> infixNonAssociative(operands, operators)
                YFX -> infixLeft(operands, operators)
                FX, FY -> prefix(result, operators)
            }
        }
        return result
    }

    private fun infixRight(terms: List<Term>, ops: List<String>): Term {
        var i = terms.size - 1
        var j = ops.size - 1
        var result: Term = scope.structOf(ops[j--], terms[i - 1], terms[i])
        i -= 2
        while (i >= 0) {
            result = scope.structOf(ops[j--], terms[i], result)
            i--
        }
        return result
    }

    private fun infixLeft(terms: List<Term>, ops: List<String>): Term {
        var i = 0
        var j = 0
        var result: Term = scope.structOf(ops[j++], terms[i++], terms[i++])
        while (i < terms.size) {
            result = scope.structOf(ops[j++], result, terms[i])
            i++
        }
        return result
    }

    private fun listOfOperands(ctx: PrologParser.ExpressionContext): List<Term> =
        (listOf(ctx.left) + ctx.right).map { it.accept(this) }

    private fun listOfOperators(ctx: PrologParser.ExpressionContext): List<String> =
        ctx.operators.map { it.symbol.text }


    private fun visitInfixRightAssociativeExpression(ctx: PrologParser.ExpressionContext): Term =
        infixRight(listOfOperands(ctx), listOfOperators(ctx))

    private fun visitInfixLeftAssociativeExpression(ctx: PrologParser.ExpressionContext): Term =
        infixLeft(listOfOperands(ctx), listOfOperators(ctx))

    private fun flatten(outers: Sequence<PrologParser.OuterContext>): Sequence<PrologParser.OuterContext> =
        outers.asSequence().flatMap {
            sequenceOf(it) + flatten(it.outers.asSequence())
        }

    private fun flatten(outers: List<PrologParser.OuterContext>): List<PrologParser.OuterContext> =
        flatten(outers.asSequence()).toList()
}