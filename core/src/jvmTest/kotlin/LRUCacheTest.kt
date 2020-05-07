import it.unibo.tuprolog.utils.Cache
import org.junit.Test

class LRUCacheTest {
    @Test
    fun test() {
        val cache = Cache.simpleLru<String, Int>(4)
        println(cache)
        println(cache.toMap())
        cache["A"] = 1
        println(cache)
        println(cache.toMap())
        cache["B"] = 2
        println(cache)
        println(cache.toMap())
        cache["C"] = 3
        println(cache)
        println(cache.toMap())
        cache["D"] = 4
        println(cache)
        println(cache.toMap())
        cache["E"] = 5
        println(cache)
        println(cache.toMap())
        cache["F"] = 6
        println(cache)
        println(cache.toMap())
        cache["G"] = 7
        println(cache)
        println(cache.toMap())
        cache["H"] = 8
        println(cache)
        println(cache.toMap())
    }
}