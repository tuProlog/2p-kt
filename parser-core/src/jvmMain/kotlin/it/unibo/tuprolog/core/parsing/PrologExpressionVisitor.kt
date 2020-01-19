package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.parser.PrologParser
import it.unibo.tuprolog.parser.PrologParserBaseVisitor
import it.unibo.tuprolog.parser.dynamic.Associativity.*
import it.unibo.tuprolog.parser.dynamic.Associativity.Companion.INFIX
import it.unibo.tuprolog.parser.dynamic.Associativity.Companion.POSTFIX
import it.unibo.tuprolog.parser.dynamic.Associativity.Companion.PREFIX

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
        return handleOuters( when{
            ctx.isTerm -> visitTerm(ctx.left)
            INFIX.contains(ctx.associativity) -> visitInfixExpression(ctx)
            POSTFIX.contains(ctx.associativity) -> visitPostfixExpression(ctx)
            PREFIX.contains(ctx.associativity) -> visitPrefixExpression(ctx)
            ctx.exception != null -> throw ctx.exception
            else -> throw java.lang.IllegalArgumentException()
        }, flatten(ctx.outers))
    }

    override fun visitTerm(ctx: PrologParser.TermContext): Term =
        if(ctx.isExpr) visitExpression(ctx.expression()) else ctx.children.get(0).accept(this)


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
            return Atom.of(ctx.functor.text)
        } else {
            return Struct.of(
                ctx.functor.text,
                ctx.args.map(this::visitExpression)
            )
        }
    }

    override fun visitList(ctx: PrologParser.ListContext): Term {
        val terms = ctx.items.map(this::visitExpression)
        return if(ctx.hasTail) createListExact(terms.plus(this.visitExpression(ctx.tail))) else it.unibo.tuprolog.core.List.from(terms)
    }



    override fun visitSet(ctx: PrologParser.SetContext): Term {
        return if(ctx.length==1)
            it.unibo.tuprolog.core.Set.of(ctx.items[0].accept(this))
        else
            it.unibo.tuprolog.core.Set.of(ctx.items.map(this::visitExpression))
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

    private fun createListExact(terms: List<Term>): Struct {
        var i = terms.size - 1
        var result: Struct = Cons.of(terms[i - 1], terms[i])
        i -= 2
        while (i >= 0) {
            result = Cons.of(terms[i], result)
            i--
        }
        return result
    }

    private fun visitPostfixExpression(ctx: PrologParser.ExpressionContext): Term =
        postfix(ctx.left.accept(this), ctx.operators.map{
            it.symbol.text
        })

    private fun postfix(term: Term,ops: List<String>) :Term {
        val operator = ops.iterator()
        var result: Term = Struct.of(operator.next(), term)
        while (operator.hasNext()) {
            result = Struct.of(operator.next(), result)
        }
        return result
    }

    private fun visitPrefixExpression(ctx: PrologParser.ExpressionContext): Term =
        prefix(ctx.right[0].accept(this),ctx.operators.map{
            it.symbol.text
        })

    private fun prefix(term: Term, ops: List<String>): Term{
        var i = ops.size - 1
        var result: Term = Struct.of(ops[i--], term)
        while (i >= 0) {
            result = Struct.of(ops[i], result)
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
        val operands = listOf(ctx.left).plus(ctx.right).map{
            it.accept(this)
        }
        val operators = ctx.operators.map{
            it.symbol.text
        }
        return infixNonAssociative(operands,operators)
    }

    private fun infixNonAssociative(terms: List<Term>,ops: List<String>) : Term{
        return Struct.of(ops[0], terms[0], terms[1])
    }

    private fun handleOuters(expression: Term, outers : List<PrologParser.OuterContext>): Term {
        var result = expression
        outers.forEach{ it ->
            val operands = listOf(result).plus(
                it.right.map{
                    it.accept(this)
                }
            )
            val operators = it.operators.map{
                op -> op.symbol.text
            }
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

    //Prova refactoring List Kotlin
    private fun infixRight(terms: List<Term>, ops: List<String>): Term {
        var i = terms.size - 1
        var j = ops.size - 1
        var result: Term = Struct.of(ops[j--], terms[i - 1], terms[i])
        i -= 2
        while (i >= 0) {
            result = Struct.of(ops[j--], terms[i], result)
            i--
        }
        return result
    }

    private fun infixLeft(terms: List<Term>, ops: List<String>): Term {
        var i = 0
        var j = 0
        var result: Term = Struct.of(ops[j++], terms[i++], terms[i++])
        while (i < terms.size) {
            result = Struct.of(ops[j++], result, terms[i])
            i++
        }
        return result
    }

    private fun ListOfOperands(ctx: PrologParser.ExpressionContext ): List<Term> =
        listOf(ctx.left).plus(ctx.right.map{
            it.accept(this)
        }) as List<Term>

    private fun ListOfOperators(ctx : PrologParser.ExpressionContext) : List<String> =
        ctx.operators.map{
            op -> op.symbol.text
        }


    private fun visitInfixRightAssociativeExpression(ctx: PrologParser.ExpressionContext) : Term =
        infixRight(ListOfOperands(ctx),ListOfOperators(ctx))

    private fun visitInfixLeftAssociativeExpression(ctx: PrologParser.ExpressionContext) : Term =
        infixLeft(ListOfOperands(ctx),ListOfOperators(ctx))

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

    private fun flatten(outers: List<PrologParser.OuterContext>): List<PrologParser.OuterContext> =
        outers.flatMap{
            o -> listOf(o).plus(flatten(o.outers))
        }
}