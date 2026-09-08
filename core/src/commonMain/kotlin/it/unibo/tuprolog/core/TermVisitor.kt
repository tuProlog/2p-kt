package it.unibo.tuprolog.core

import kotlin.js.JsName

/**
 * The Visitor-pattern counterpart of the [Term] hierarchy (see [Term.accept]). Each `visitX` method has a
 * default implementation delegating to the `visitY` method of its immediate supertype `Y` in the hierarchy
 * (e.g. [visitAtom] delegates to [visitStruct], which delegates to [visitTerm], which delegates to
 * [defaultValue]), so implementers only need to override the methods for the specific sub-types they care
 * about, letting everything else fall back sensibly. [defaultValue] is the only method that must be
 * implemented.
 *
 * Overriding [visitTerm] intercepts every kind of term uniformly (unless a more specific override exists);
 * this is how, e.g., [ListIterator] and [TupleIterator] are implemented, by overriding only the couple of
 * `visitX` methods relevant to unfolding one step of a list/tuple, and letting [defaultValue] signal "end of
 * sequence" for everything else.
 *
 * @param T the type of the value produced by visiting a [Term]
 * @see Term.accept
 * @see it.unibo.tuprolog.core.visitors.AbstractTermVisitor
 */
interface TermVisitor<T> {
    /** The value produced for any [Term] whose specific type has no dedicated, overridden `visitX` method. */
    @JsName("defaultValue")
    fun defaultValue(term: Term): T

    /** Visits a generic [Term], regardless of its specific sub-type. Falls back to [defaultValue]. */
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
