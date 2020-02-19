package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.parser.*
import it.unibo.tuprolog.parser.Associativity.FX
import it.unibo.tuprolog.parser.Associativity.FY
import it.unibo.tuprolog.parser.Associativity.INFIX
import it.unibo.tuprolog.parser.Associativity.POSTFIX
import it.unibo.tuprolog.parser.Associativity.PREFIX
import it.unibo.tuprolog.parser.Associativity.XF
import it.unibo.tuprolog.parser.Associativity.XFX
import it.unibo.tuprolog.parser.Associativity.XFY
import it.unibo.tuprolog.parser.Associativity.YF
import it.unibo.tuprolog.parser.Associativity.YFX
import org.gciatto.kt.math.BigInteger


class PrologVisitor : PrologParserVisitor<Term>() {
    private val scope: Scope = Scope.empty()

    override fun visitSingletonTerm(ctx: SingletonTermContext): Term =
        visitTerm(ctx.term())

    override fun visitSingletonExpression(ctx: SingletonExpressionContext): Term =
        visitExpression(ctx.expression())

    override fun visitClause(ctx: ClauseContext): Term =
        ctx.expression().accept(this)

    override fun visitExpression(ctx: ExpressionContext): Term {
        return handleOuters(
            when {
                ctx.isTerm -> visitTerm(ctx.left!!)
                INFIX.contains(ctx.associativity) -> visitInfixExpression(ctx)
                POSTFIX.contains(ctx.associativity) -> visitPostfixExpression(ctx)
                PREFIX.contains(ctx.associativity) -> visitPrefixExpression(ctx)
                else -> throw IllegalArgumentException("Associativity unknown: ${ctx.associativity} INFIX=${INFIX} PREFIX=${PREFIX} POSTFIX=${POSTFIX}") // use kotlin's IllegalArgumentException
            }, flatten(ctx.outers.asList())
        )
    }

    override fun visitTerm(ctx: TermContext): Term =
        if (ctx.isExpr) {
            visitExpression(ctx.expression())
        } else {
            ctx.children[0].accept(this) as Term
        }


    override fun visitInteger(ctx: IntegerContext): Term {
        val value = parseInteger(ctx)
        return Integer.of(value)
    }

    override fun visitNumber(ctx: NumberContext): Term {
        return ctx.children[0].accept(this) as Term
    }

    override fun visitReal(ctx: RealContext): Term {
        var raw = ctx.value.text
        if (ctx.sign != null) {
            raw = ctx.sign?.text + raw
        }
        return try {
            println("Real: $raw")
            Real.of(raw)
        } catch (notAFloating: NumberFormatException) {
            throw parseException(
                ctx.value,
                "Invalid real number format: " + ctx.value.text
            )
        }
    }

    override fun visitVariable(ctx: VariableContext): Term =
        if (ctx.isAnonymous) {
            scope.anonymous()
        } else {
            scope.varOf(ctx.value.text)
        }

    override fun visitStructure(ctx: StructureContext): Term {
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

    override fun visitList(ctx: ListContext): Term {
        val terms = ctx.items.map(this::visitExpression)
        return if (ctx.hasTail) {
            scope.listFrom(terms, ctx.tail?.accept(this))
        } else {
            scope.listOf(terms)
        }
    }

    override fun visitSet(ctx: SetContext): Term {
        return if (ctx.length == 1)
            scope.setOf(ctx.items[0].accept<Term>(this))
        else
            scope.setOf(ctx.items.map(this::visitExpression))
    }

    //PRIVATE METHODS
    private fun parseInteger(ctx: IntegerContext): BigInteger {
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
                    throw parseException(
                        ctx.value,
                        "Invalid character literal: " + ctx.value.text
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
            clean = ctx.sign?.text + clean
        }
        return BigInteger.of(clean, base)
    }

    private fun visitPostfixExpression(ctx: ExpressionContext): Term =
        postfix(ctx.left?.accept(this)!!, ctx.operators.map {
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

    private fun visitPrefixExpression(ctx: ExpressionContext): Term =
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

    private fun visitInfixExpression(ctx: ExpressionContext): Term {
        return when (ctx.associativity) {
            XFY -> visitInfixRightAssociativeExpression(ctx)
            YFX -> visitInfixLeftAssociativeExpression(ctx)
            XFX -> visitInfixNonAssociativeExpression(ctx)
            else -> throw IllegalStateException()
        }
    }

    private fun visitInfixNonAssociativeExpression(ctx: ExpressionContext): Term {
        val operands: List<Term> = (listOf(ctx.left!!) + ctx.right.asList()).map { it.accept<Term>(this) }
        val operators = ctx.operators.map { it.symbol.text }
        return infixNonAssociative(operands, operators)
    }

    private fun infixNonAssociative(terms: List<Term>, ops: List<String>): Term {
        return scope.structOf(ops[0], terms[0], terms[1])
    }

    private fun handleOuters(expression: Term, outers: List<OuterContext>): Term {
        var result = expression
        for (o in outers) {
            val operands = listOf(result) + o.right.map { it.accept<Term>(this) }
            val operators = o.operators.map { it.symbol.text }
            result = when (o.associativity) {
                XFY -> infixRight(operands, operators)
                XF, YF -> postfix(result, operators)
                XFX -> infixNonAssociative(operands, operators)
                YFX -> infixLeft(operands, operators)
                FX, FY -> prefix(result, operators)
                else -> throw IllegalStateException()
            }
        }
        return result
    }

    //Prova refactoring List Kotlin
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

    private fun listOfOperands(ctx: ExpressionContext): List<Term> =
        (listOf(ctx.left) + ctx.right.toList()).map { it!!.accept<Term>(this) }

    private fun listOfOperators(ctx: ExpressionContext): List<String> =
        ctx.operators.map { it.symbol.text }


    private fun visitInfixRightAssociativeExpression(ctx: ExpressionContext): Term =
        infixRight(listOfOperands(ctx), listOfOperators(ctx))

    private fun visitInfixLeftAssociativeExpression(ctx: ExpressionContext): Term =
        infixLeft(listOfOperands(ctx), listOfOperators(ctx))

    private fun flatten(outers: Sequence<OuterContext>): Sequence<OuterContext> =
        outers.asSequence().flatMap {
            sequenceOf(it) + flatten(it.outers.asSequence())
        }

    private fun flatten(outers: List<OuterContext>): List<OuterContext> =
        flatten(outers.asSequence()).toList()
}