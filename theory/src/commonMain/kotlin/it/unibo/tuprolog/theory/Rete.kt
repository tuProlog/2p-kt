package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.InvalidClauseException
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.Unifier

internal fun <T> List<T>.replace(selector: (T)->Boolean, mapper: (T) -> T): List<T> {
    return this.map { if (selector(it)) mapper(it) else it }
}

internal fun <K, V> MapList<K, V>.replace(selector: (K, V)->Boolean, mapper: (K, V) -> V): MapList<K, V> {
    return MapList.of(this.asSequence()
            .map { if (selector(it.key, it.value)) Pair(it.key, mapper(it.key, it.value)) else it.toPair() })
}

internal sealed class Rete  {

    companion object {
        fun Term.ensureClause() {
            if (this !is Struct || this.functor != ":-" || this.arity != 2 || this[0] is Var){
                throw InvalidClauseException(this)
            }
        }
    }

    data class Root(
            val childrenByFunctor: MapList<String, List<Functor>>
    ) : Rete() {

        constructor(clause: Struct)
                : this(MapList.of(clause[0].cast<Struct>().functor, listOf(Functor(clause)))) {
            clause.ensureClause()
        }

        override fun plus(clause: Struct): Root {
            clause.ensureClause()

            with(clause[0] as Struct) {

                return if (this.functor in childrenByFunctor) {
                    Root(childrenByFunctor.replace(
                            { k, _ -> k == this.functor },
                            { _, v -> v.map { it + clause } }
                    ))
                } else {
                    Root(childrenByFunctor + MapList.of(clause[0].cast<Struct>().functor, listOf(Functor(clause))))
                }
            }
        }

        override val children: Sequence<Rete>
            get() = childrenByFunctor.values.asSequence().flatMap { it.asSequence() }

        override fun get(term: Term): Sequence<Term> {
            return if (term is Struct && term.functor in childrenByFunctor) {
                childrenByFunctor[term.functor]!!.asSequence().flatMap { it[term] }
            } else {
                emptySequence()
            }
        }

        override fun contains(term: Term): Boolean {
            return term is Struct
                    && term.functor in childrenByFunctor
                    && childrenByFunctor[term.functor]!!.any { term in it }
        }

    }

    data class Functor(
            val functor: String,
            val childrenByArity: MapList<Int, List<Arity>>
    ) : Rete() {
        constructor(clause: Struct)
                : this(clause[0].cast<Struct>().functor, MapList.of(clause[0].cast<Struct>().arity, listOf(Arity(clause)))) {
            clause.ensureClause()
        }

        override fun plus(clause: Struct): Functor {
            clause.ensureClause()

            return with(clause[0] as Struct) {
                if (functor != this@Functor.functor) {
                    this@Functor
                } else if (this.arity in childrenByArity){
                    Functor(
                            functor,
                            childrenByArity.replace(
                                    { k, _ -> k == arity },
                                    { _, v -> v.map { it + clause }}
                            )
                    )
                } else {
                    Functor(functor, childrenByArity + MapList.of(arity, listOf(Arity(clause))))
                }
            }
        }

        override val children: Sequence<Rete>
            get() = childrenByArity.values.asSequence().flatMap { it.asSequence() }

        override fun get(term: Term): Sequence<Term> {
            return if (term is Struct && term.functor == functor && term.arity in childrenByArity) {
                childrenByArity[term.arity]!!.asSequence().flatMap { it[term] }
            } else {
                emptySequence()
            }
        }

        override fun contains(term: Term): Boolean {
            return term is Struct
                    && term.functor == functor
                    && term.arity in childrenByArity
                    && childrenByArity[term.arity]!!.any { term in it }
        }
    }

    data class Arity(
            val arity: Int,
            val arguments: List<Term>,
            override val clauses: List<Term> = emptyList()
    ) : Rete() {

        constructor(clause: Struct) : this(clause[0].cast<Struct>().arity, listOf(clause[0]), listOf(clause)) {
            clause.ensureClause()
        }

        override fun plus(clause: Struct): Arity {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun contains(term: Term): Boolean {
            return term is Struct
                    && term.arity == arity
                    && (0 until arity).all { Unifier.default.unify(term[it], arguments[it]) }
        }
    }

    data class Value(
            val value: Term,
            override val clauses: List<Term> = emptyList()
    ) : Rete() {

        constructor(clause: Struct) : this(clause[0], listOf(clause)) {
            clause.ensureClause()
        }

        override fun plus(clause: Struct): Rete {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun contains(term: Term): Boolean {
            return Unifier.default.unify(value, term)
        }
    }

//    data class Whatever(
//            override val clauses: List<Term> = emptyList()
//    ) : Rete() {
//
//        constructor(term: Term) : this(listOf(term))
//
//        override fun plus(clause: Struct): Whatever {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//
//        override fun contains(term: Term): Boolean {
//            return true
//        }
//    }

    //    open val childList: List<Rete> = emptyList()
    open val clauses: List<Term> = emptyList()

    open val children: Sequence<Rete>
        get() = emptySequence()


    abstract operator fun plus(clause: Struct): Rete

    abstract operator fun contains(term: Term): Boolean

    open operator fun get(term: Term): Sequence<Term> {
        return if (term in this) {
            clauses.asSequence()
        } else {
            emptySequence()
        }
    }

    val allTerms: Sequence<Term>
        get() {
            return children.flatMap { it.children }
                    .flatMap { it.clauses.asSequence() }
        }
}