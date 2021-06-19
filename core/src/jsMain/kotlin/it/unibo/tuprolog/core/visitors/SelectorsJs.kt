package it.unibo.tuprolog.core.visitors

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Collection
import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.Constant
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Empty
import it.unibo.tuprolog.core.EmptyList
import it.unibo.tuprolog.core.EmptySet
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Set
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var

actual fun <T> whenVar(
    term: Term,
    ifVar: (Var) -> T,
    otherwise: (Term) -> T
): T = term.accept(WhenVar(ifVar, otherwise))

actual fun <T> whenConstant(term: Term, ifConstant: (Constant) -> T, otherwise: (Term) -> T): T = TODO()

actual fun <T> whenStruct(term: Term, ifStruct: (Struct) -> T, otherwise: (Term) -> T): T = TODO()

actual fun <T> whenCollection(term: Term, ifCollection: (Collection) -> T, otherwise: (Term) -> T): T = TODO()

actual fun <T> whenAtom(term: Term, ifAtom: (Atom) -> T, otherwise: (Term) -> T): T = TODO()

actual fun <T> whenTruth(term: Term, ifTruth: (Truth) -> T, otherwise: (Term) -> T): T = TODO()

actual fun <T> whenNumeric(
    term: Term,
    ifNumeric: (Numeric) -> T,
    ifInteger: (Integer) -> T,
    ifReal: (Real) -> T,
    otherwise: (Term) -> T
): T = term.accept(WhenNumeric(ifNumeric, ifInteger, ifReal, otherwise))

actual fun <T> whenInteger(term: Term, ifInteger: (Integer) -> T, otherwise: (Term) -> T): T =
    whenNumeric(term, otherwise, ifInteger, otherwise, otherwise)

actual fun <T> whenReal(term: Term, ifReal: (Real) -> T, otherwise: (Term) -> T): T =
    whenNumeric(term, otherwise, otherwise, ifReal, otherwise)

actual fun <T> whenSet(term: Term, ifSet: (Set) -> T, otherwise: (Term) -> T): T = TODO()

actual fun <T> whenEmpty(term: Term, ifEmpty: (Empty) -> T, otherwise: (Term) -> T): T = TODO()

actual fun <T> whenEmptySet(term: Term, ifEmptySet: (EmptySet) -> T, otherwise: (Term) -> T): T = TODO()

actual fun <T> whenList(
    term: Term,
    ifList: (List) -> T,
    ifCons: (Cons) -> T,
    ifEmptyList: (EmptyList) -> T,
    otherwise: (Term) -> T
): T = TODO()

actual fun <T> whenCons(term: Term, ifCons: (Cons) -> T, otherwise: (Term) -> T): T = TODO()

actual fun <T> whenEmptyList(term: Term, ifEmptyList: (EmptyList) -> T, otherwise: (Term) -> T): T = TODO()

actual fun <T> whenTuple(term: Term, ifTuple: (Tuple) -> T, otherwise: (Term) -> T): T =
    term.accept(WhenTuple(ifTuple, otherwise))

actual fun <T> whenIndicator(term: Term, ifIndicator: (Indicator) -> T, otherwise: (Term) -> T): T = TODO()

actual fun <T> whenClause(term: Term, ifClause: (Clause) -> T, otherwise: (Term) -> T): T = TODO()

actual fun <T> whenRule(term: Term, ifRule: (Rule) -> T, otherwise: (Term) -> T): T = TODO()

actual fun <T> whenFact(term: Term, ifFact: (Fact) -> T, otherwise: (Term) -> T): T = TODO()

actual fun <T> whenDirective(term: Term, ifDirective: (Directive) -> T, otherwise: (Term) -> T): T = TODO()
