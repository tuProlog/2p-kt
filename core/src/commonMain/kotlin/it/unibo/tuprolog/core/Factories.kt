package it.unibo.tuprolog.core

import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger

fun atomOf(value: String): Atom {
    return Atom.of(value)
}

fun structOf(functor: String, vararg args: Term): Struct {
    return Struct.of(functor, *args)
}

fun varOf(name: String = Var.ANONYMOUS_VAR_NAME): Var {
    return Var.of(name)
}

fun lstOf(vararg terms: Term): List {
    return List.of(terms.map { it })
}

fun setOf(vararg terms: Term): Set {
    return Set.of(terms.map { it })
}

fun factOf(head: Term): Fact {
    return Fact.of(head as Struct)
}

fun ruleOf(head: Term, body1: Term, vararg body: Term): Rule {
    return Rule.of(head as Struct, *(arrayOf(body1) + body))
}

fun directiveOf(body1: Term, vararg body: Term): Directive {
    return Directive.of(body1, *body)
}

fun clauseOf(head: Term?, vararg body: Term): Clause {
    return Clause.of(head as Struct?, *body)
}

fun consOf(term1: Term, vararg terms: Term): Cons {
    return List.from(sequenceOf(term1) + sequenceOf(*terms)) as Cons
}

fun anonymous(): Term {
    return Var.anonymous()
}

fun whatever(): Term {
    return Var.anonymous()
}

fun numOf(decimal: BigDecimal): Real {
    return Real.of(decimal)
}

fun numOf(decimal: Double): Real {
    return Real.of(decimal)
}

fun numOf(decimal: Float): Real {
    return Real.of(decimal)
}

fun numOf(integer: BigInteger): Integer {
    return Integer.of(integer)
}

fun numOf(integer: Int): Integer {
    return Integer.of(integer)
}

fun numOf(integer: Long): Integer {
    return Integer.of(integer)
}

fun numOf(integer: Short): Integer {
    return Integer.of(integer)
}

fun numOf(integer: Byte): Integer {
    return Integer.of(integer)
}

fun numOf(number: String): Numeric {
    return try {
        Integer.of(number)
    } catch (ex: NumberFormatException) {
        Real.of(number)
    }

}





