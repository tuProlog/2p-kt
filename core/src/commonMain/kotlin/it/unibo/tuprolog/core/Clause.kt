package it.unibo.tuprolog.core

interface Clause : Struct {

    override val functor: String
        get() = FUNCTOR

    val head: Struct?

    val body: Term

    override val args: Array<Term>
        get() {
            return if (head === null) {
                arrayOf(body)
            } else {
                arrayOf(head!!, body)
            }
        }

    override val arity: Int
        get() {
            return if (head === null) {
                1
            } else {
                2
            }
        }

    companion object {
        const val FUNCTOR = ":-"
    }
}