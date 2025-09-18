package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.EmptyList
import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor

internal class ListUnfolder(
    list: List,
) : Iterator<Term> {
    private var current: Term? = list

    override fun hasNext(): Boolean = current != null

    override fun next(): Term = current?.accept(listUnfolderVisitor) ?: throw NoSuchElementException()

    private val listUnfolderVisitor =
        object : TermVisitor<Term> {
            override fun visitCons(term: Cons): Term {
                current = term.tail
                return term
            }

            override fun visitEmptyList(term: EmptyList): Term {
                current = null
                return term
            }

            override fun defaultValue(term: Term): Term {
                current = null
                return term
            }
        }
}
