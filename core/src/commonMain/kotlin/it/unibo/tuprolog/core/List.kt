package it.unibo.tuprolog.core

import kotlin.collections.List as KtList

interface List : Struct {

    override val isList: Boolean
        get() = true

    fun toArray(): Array<Term>

    fun toList(): KtList<Term>

    fun toSequence(): Sequence<Term>

    override fun freshCopy(): List = super.freshCopy() as List

    override fun freshCopy(scope: Scope): List =
            if (isGround) {
                this
            } else {
                scope.listOf(argsSequence.map { it.freshCopy(scope) }.takeWhile { it !is EmptyList }.asIterable())
            }

    companion object {

        fun from(items: Iterable<Term>, last: Term? = null): List = from(items.toList(), last)

        fun from(items: Sequence<Term>, last: Term? = null): List = from(items.toList(), last)

        fun from(items: KtList<Term>, last: Term? = null): List {
            require(items.isNotEmpty()) {
                "Input list for method ${List::class.qualifiedName}.from(${KtList::class.qualifiedName}, ${Term::class.qualifiedName}) cannot be empty"
            }

            val finalItem = if (last === null) Empty.list() else last
            return items.foldRight(finalItem) { head, tail -> Couple.of(head, tail) } as List
        }

        fun of(vararg items: Term): List = from(items.toList(), Empty.list())

        fun of(items: Iterable<Term>): List = from(items.toList(), Empty.list())

        fun empty(): List = Empty.list()
    }
}
