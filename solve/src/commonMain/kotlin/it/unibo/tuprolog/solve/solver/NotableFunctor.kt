package it.unibo.tuprolog.solve.solver

import it.unibo.tuprolog.core.Clause

/**
 * A class attaching semantics to notable functors enumerated in [Clause.notableFunctors]
 *
 * @author Enrico
 */
internal enum class NotableFunctor(val functor: String, val semantic: (Boolean, Boolean) -> Boolean) {
    /** The conjunction notable functor has symbol ',' and its semantics is logic *AND* */
    CONJUNCTION(",", Boolean::and),
    /** The disjunction notable functor has symbol ';' and its semantics is logic *OR* */
    DISJUNCTION(";", Boolean::or),
    /** The implication notable functor has symbol '->' and its semantics is logic *IMPLICATION* */
    IMPLICATION("->", { precondition, conclusion -> if (precondition) conclusion else true })
}
