package it.unibo.tuprolog.utils

class Cached1<T, R>(private val f: (T) -> R) : (T) -> R {

    private val cache: MutableMap<T, R> = mutableMapOf()

    override fun invoke(arg1: T): R {
        return if (arg1 in cache) {
            cache[arg1]!!
        } else {
            val x = f(arg1)
            cache[arg1] = x
            x
        }
    }
}

fun <T, R> cached(f: (T) -> R): (T) -> R {
    return Cached1(f)
}