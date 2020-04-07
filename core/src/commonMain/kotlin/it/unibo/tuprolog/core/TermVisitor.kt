package it.unibo.tuprolog.core

import kotlin.js.JsName

interface TermVisitor<T> {

    @JsName("defaultValue")
    fun defaultValue(term: Term): T

    @JsName("visitToTerm")
    fun visit(term: Term): T =
        when (term) {
            is Var -> visit(term)
            is Constant -> visit(term)
            is Struct -> visit(term)
            else -> visitTerm(term)
        }

    @JsName("visitTerm")
    fun visitTerm(term: Term): T = defaultValue(term)

    @JsName("visitToVar")
    fun visit(term: Var): T = visitVar(term)

    @JsName("visitVar")
    fun visitVar(term: Var): T = defaultValue(term)

    @JsName("visitToConstant")
    fun visit(term: Constant): T =
        when (term) {
            is Numeric -> visit(term)
            is Atom -> visit(term)
            else -> visitConstant(term)
        }

    @JsName("visitConstant")
    fun visitConstant(term: Constant): T = defaultValue(term)

    @JsName("visitToStruct")
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

    @JsName("visitStruct")
    fun visitStruct(term: Struct): T = defaultValue(term)

    @JsName("visitToAtom")
    fun visit(term: Atom): T =
        when (term) {
            is Empty -> visit(term)
            is Truth -> visit(term)
            else -> visitAtom(term)
        }

    @JsName("visitAtom")
    fun visitAtom(term: Atom): T = defaultValue(term)

    @JsName("visitToTruth")
    fun visit(term: Truth): T = visitTruth(term)

    @JsName("visitTruth")
    fun visitTruth(term: Truth): T = defaultValue(term)

    @JsName("visitToNumeric")
    fun visit(term: Numeric): T =
        when (term) {
            is Real -> visit(term)
            is Integer -> visit(term)
            else -> visitNumeric(term)
        }

    @JsName("visitNumeric")
    fun visitNumeric(term: Numeric): T = defaultValue(term)

    @JsName("visitToInteger")
    fun visit(term: Integer): T = visitInteger(term)

    @JsName("visitInteger")
    fun visitInteger(term: Integer): T = defaultValue(term)

    @JsName("visitToReal")
    fun visit(term: Real): T = visitReal(term)

    @JsName("visitReal")
    fun visitReal(term: Real): T = defaultValue(term)

    @JsName("visitToSet")
    fun visit(term: Set): T =
        when (term) {
            is EmptySet -> visit(term)
            else -> visitSet(term)
        }

    @JsName("visitSet")
    fun visitSet(term: Set): T = defaultValue(term)

    @JsName("visitToEmpty")
    fun visit(term: Empty): T =
        when (term) {
            is EmptySet -> visit(term)
            is EmptyList -> visit(term)
            else -> visitEmpty(term)
        }

    @JsName("visitEmpty")
    fun visitEmpty(term: Empty): T = defaultValue(term)

    @JsName("visitToEmptySet")
    fun visit(term: EmptySet): T = visitEmptySet(term)

    @JsName("visitEmptySet")
    fun visitEmptySet(term: EmptySet): T = defaultValue(term)

    @JsName("visitToList")
    fun visit(term: List): T =
        when (term) {
            is Cons -> visit(term)
            is EmptyList -> visit(term)
            else -> visitList(term)
        }

    @JsName("visitList")
    fun visitList(term: List): T = defaultValue(term)

    @JsName("visitToCons")
    fun visit(term: Cons): T = visitCons(term)

    @JsName("visitCons")
    fun visitCons(term: Cons): T = defaultValue(term)

    @JsName("visitToEmptyList")
    fun visit(term: EmptyList): T = visitEmptyList(term)

    @JsName("visitEmptyList")
    fun visitEmptyList(term: EmptyList): T = defaultValue(term)

    @JsName("visitToTuple")
    fun visit(term: Tuple): T = visitTuple(term)

    @JsName("visitTuple")
    fun visitTuple(term: Tuple): T = defaultValue(term)

    @JsName("visitToIndicator")
    fun visit(term: Indicator): T = visitIndicator(term)

    @JsName("visitIndicator")
    fun visitIndicator(term: Indicator): T = defaultValue(term)

    @JsName("visitToClause")
    fun visit(term: Clause): T =
        when (term) {
            is Directive -> visit(term)
            is Rule -> visit(term)
            else -> visitClause(term)
        }

    @JsName("visitClause")
    fun visitClause(term: Clause): T = defaultValue(term)

    @JsName("visitToRule")
    fun visit(term: Rule): T =
        when (term) {
            is Fact -> visitFact(term)
            else -> visitRule(term)
        }

    @JsName("visitRule")
    fun visitRule(term: Rule): T = defaultValue(term)

    @JsName("visitToFact")
    fun visit(term: Fact): T = visitFact(term)

    @JsName("visitFact")
    fun visitFact(term: Fact): T = defaultValue(term)

    @JsName("visitToDirective")
    fun visit(term: Directive): T = visitDirective(term)

    @JsName("visitDirective")
    fun visitDirective(term: Directive): T = defaultValue(term)
}