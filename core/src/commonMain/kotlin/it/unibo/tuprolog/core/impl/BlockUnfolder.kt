package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Block
import it.unibo.tuprolog.core.EmptyBlock
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor
import it.unibo.tuprolog.core.Tuple

internal class BlockUnfolder(
    block: Block,
) : Iterator<Term> {
    private var current: Term? = block

    private var setUnfolded = false

    override fun hasNext(): Boolean = current != null

    private val tupleUnfolderVisitor =
        object : TermVisitor<Term> {
            override fun visitTuple(term: Tuple): Term {
                current = term.right
                return term
            }

            override fun defaultValue(term: Term): Term {
                current = null
                return term
            }
        }

    private val blockUnfolderVisitor =
        object : TermVisitor<Term> {
            override fun visitEmptyBlock(term: EmptyBlock): Term {
                current = null
                return term
            }

            override fun visitBlock(term: Block): Term {
                current = term[0]
                setUnfolded = true
                return term
            }

            override fun defaultValue(term: Term): Term {
                current = null
                return term
            }
        }

    override fun next(): Term =
        current?.accept(if (setUnfolded) tupleUnfolderVisitor else blockUnfolderVisitor)
            ?: throw NoSuchElementException()
}
