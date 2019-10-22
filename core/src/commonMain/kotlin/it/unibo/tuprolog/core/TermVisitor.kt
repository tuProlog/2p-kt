package it.unibo.tuprolog.core

interface TermVisitor<T> {

    fun defaultValue(term: Term): T

    fun visit(term: Term): T =
            when (term) {
                is Var -> visit(term)
                is Constant -> visit(term)
                is Struct -> visit(term)
                else -> visitTerm(term)
            }

    fun visitTerm(term: Term): T = defaultValue(term)

    fun visit(term: Var): T = visitVar(term)

    fun visitVar(term: Var): T = defaultValue(term)

    fun visit(term: Constant): T =
            when (term) {
                is Numeric -> visit(term)
                is Atom -> visit(term)
                else -> visitConstant(term)
            }

    fun visitConstant(term: Constant): T = defaultValue(term)

    fun visit(term: Struct): T =
            when (term) {
                is List -> visit(term)
                is Set -> visit(term)
                is Atom -> visit(term)
                is Tuple -> visit(term)
                is Clause -> visit(term)
                is Indicator -> visit(term)
                else -> visitStruct(term)
            }

    fun visitStruct(term: Struct): T = defaultValue(term)

    fun visit(term: Atom): T =
            when (term) {
                is Empty -> visit(term)
                is Truth -> visit(term)
                else -> visitAtom(term)
            }

    fun visitAtom(term: Atom): T = defaultValue(term)

    fun visit(term: Truth): T = visitTruth(term)

    fun visitTruth(term: Truth): T = defaultValue(term)

    fun visit(term: Numeric): T =
            when (term) {
                is Real -> visit(term)
                is Integer -> visit(term)
                else -> visitNumeric(term)
            }

    fun visitNumeric(term: Numeric): T = defaultValue(term)

    fun visit(term: Integer): T = visitInteger(term)

    fun visitInteger(term: Integer): T = defaultValue(term)

    fun visit(term: Real): T = visitReal(term)

    fun visitReal(term: Real): T = defaultValue(term)

    fun visit(term: Set): T =
            when (term) {
                is EmptySet -> visit(term)
                else -> visitSet(term)
            }

    fun visitSet(term: Set): T = defaultValue(term)

    fun visit(term: Empty): T =
            when (term) {
                is EmptySet -> visit(term)
                is EmptyList -> visit(term)
                else -> visitEmpty(term)
            }

    fun visitEmpty(term: Empty): T = defaultValue(term)

    fun visit(term: EmptySet): T = visitEmptySet(term)

    fun visitEmptySet(term: EmptySet): T = defaultValue(term)

    fun visit(term: List): T =
            when (term) {
                is Cons -> visit(term)
                is EmptyList -> visit(term)
                else -> visitList(term)
            }

    fun visitList(term: List): T = defaultValue(term)

    fun visit(term: Cons): T = visitCons(term)

    fun visitCons(term: Cons): T = defaultValue(term)

    fun visit(term: EmptyList): T = visitEmptyList(term)

    fun visitEmptyList(term: EmptyList): T = defaultValue(term)

    fun visit(term: Tuple): T = visitTuple(term)

    fun visitTuple(term: Tuple): T = defaultValue(term)

    fun visit(term: Indicator): T = visitIndicator(term)

    fun visitIndicator(term: Indicator): T = defaultValue(term)

    fun visit(term: Clause): T =
            when (term) {
                is Directive -> visit(term)
                is Rule -> visit(term)
                else -> visitClause(term)
            }

    fun visitClause(term: Clause): T = defaultValue(term)

    fun visit(term: Rule): T =
            when (term) {
                is Fact -> visitFact(term)
                else -> visitRule(term)
            }

    fun visitRule(term: Rule): T = defaultValue(term)

    fun visit(term: Fact): T = visitFact(term)

    fun visitFact(term: Fact): T = defaultValue(term)

    fun visit(term: Directive): T = visitDirective(term)

    fun visitDirective(term: Directive): T = defaultValue(term)
}