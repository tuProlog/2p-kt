package it.unibo.tuprolog.core

import kotlin.js.JsName

interface TermVisitor<T> {

    @JsName("defaultValue")
    fun defaultValue(term: Term): T

    @JsName("visitTerm")
    fun visitTerm(term: Term): T = defaultValue(term)

    @JsName("visitVar")
    fun visitVar(term: Var): T = visitTerm(term)

    @JsName("visitConstant")
    fun visitConstant(term: Constant): T = visitTerm(term)

    @JsName("visitStruct")
    fun visitStruct(term: Struct): T = visitTerm(term)

    @JsName("visitCollection")
    fun visitCollection(term: Recursive): T = visitStruct(term)

    @JsName("visitAtom")
    fun visitAtom(term: Atom): T = visitStruct(term)

    @JsName("visitTruth")
    fun visitTruth(term: Truth): T = visitAtom(term)

    @JsName("visitNumeric")
    fun visitNumeric(term: Numeric): T = visitConstant(term)

    @JsName("visitInteger")
    fun visitInteger(term: Integer): T = visitNumeric(term)

    @JsName("visitReal")
    fun visitReal(term: Real): T = visitNumeric(term)

    @JsName("visitBlock")
    fun visitBlock(term: Block): T = visitCollection(term)

    @JsName("visitEmpty")
    fun visitEmpty(term: Empty): T = visitAtom(term)

    @JsName("visitEmptyBlock")
    fun visitEmptyBlock(term: EmptyBlock): T = visitBlock(term)

    @JsName("visitList")
    fun visitList(term: List): T = visitCollection(term)

    @JsName("visitCons")
    fun visitCons(term: Cons): T = visitList(term)

    @JsName("visitEmptyList")
    fun visitEmptyList(term: EmptyList): T = visitList(term)

    @JsName("visitTuple")
    fun visitTuple(term: Tuple): T = visitCollection(term)

    @JsName("visitIndicator")
    fun visitIndicator(term: Indicator): T = visitStruct(term)

    @JsName("visitClause")
    fun visitClause(term: Clause): T = visitStruct(term)

    @JsName("visitRule")
    fun visitRule(term: Rule): T = visitClause(term)

    @JsName("visitFact")
    fun visitFact(term: Fact): T = visitRule(term)

    @JsName("visitDirective")
    fun visitDirective(term: Directive): T = visitClause(term)
}
