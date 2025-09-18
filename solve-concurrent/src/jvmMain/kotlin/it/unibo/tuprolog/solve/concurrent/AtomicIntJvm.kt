package it.unibo.tuprolog.solve.concurrent

import java.util.concurrent.atomic.AtomicInteger

class AtomicIntJvm private constructor(
    private val atomicValue: AtomicInteger,
) : AtomicInt {
    constructor(value: Int) : this(AtomicInteger(value))

    override var value: Int
        get() = atomicValue.get()
        set(value) = atomicValue.set(value)

    override fun plusAssign(delta: Int) {
        atomicValue.addAndGet(delta)
    }

    override fun minusAssign(delta: Int) {
        this += (-delta)
    }

    override fun incAndGet(delta: Int): Int = atomicValue.addAndGet(delta)

    override fun decAndGet(delta: Int): Int = atomicValue.addAndGet(-delta)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AtomicIntJvm

        if (atomicValue != other.atomicValue) return false

        return true
    }

    override fun hashCode(): Int = atomicValue.hashCode()

    override fun toString(): String = "AtomicIntJvm(value=$value)"
}

internal actual fun atomicInt(value: Int): AtomicInt = AtomicIntJvm(value)
