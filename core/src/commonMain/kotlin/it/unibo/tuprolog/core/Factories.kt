package it.unibo.tuprolog.core

import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger

fun atomOf(value: String): Atom = Atom.of(value)

fun structOf(functor: String, vararg args: Term): Struct = Struct.of(functor, *args)

fun varOf(name: String = Var.ANONYMOUS_VAR_NAME): Var = Var.of(name)

fun lstOf(vararg terms: Term): List = List.of(terms.map { it })

fun setOf(vararg terms: Term): Set = Set.of(terms.map { it })

fun factOf(head: Term): Fact = Fact.of(head as Struct)

fun ruleOf(head: Term, body1: Term, vararg body: Term): Rule =
        Rule.of(head as Struct, *(arrayOf(body1) + body))

fun directiveOf(body1: Term, vararg body: Term): Directive = Directive.of(body1, *body)

fun clauseOf(head: Term?, vararg body: Term): Clause = Clause.of(head as Struct?, *body)

fun consOf(term1: Term, vararg terms: Term): Cons =
        List.from(sequenceOf(term1) + sequenceOf(*terms)) as Cons

fun anonymous(): Term = Var.anonymous()

fun whatever(): Term = Var.anonymous()

fun numOf(decimal: BigDecimal): Real = Real.of(decimal)

fun numOf(decimal: Double): Real = Real.of(decimal)

fun numOf(decimal: Float): Real = Real.of(decimal)

fun numOf(integer: BigInteger): Integer = Integer.of(integer)

fun numOf(integer: Int): Integer = Integer.of(integer)

fun numOf(integer: Long): Integer = Integer.of(integer)

fun numOf(integer: Short): Integer = Integer.of(integer)

fun numOf(integer: Byte): Integer = Integer.of(integer)

fun numOf(number: String): Numeric =
        try {
            Integer.of(number)
        } catch (ex: NumberFormatException) {
            Real.of(number)
        }
