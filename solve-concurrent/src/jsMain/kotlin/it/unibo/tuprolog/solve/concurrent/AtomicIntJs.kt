package it.unibo.tuprolog.solve.concurrent

class AtomicIntJs(
    override var value: Int,
) : AtomicInt {
    override fun plusAssign(delta: Int) {
        value += delta
    }

    override fun minusAssign(delta: Int) {
        value -= delta
    }

    override fun incAndGet(delta: Int): Int {
        value += delta
        return value
    }

    override fun decAndGet(delta: Int): Int {
        value -= delta
        return value
    }

    override fun toString(): String = "AtomicIntJs(value=$value)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class.js != other::class.js) return false

        other as AtomicIntJs

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int = value
}

internal actual fun atomicInt(value: Int): AtomicInt = AtomicIntJs(value)
