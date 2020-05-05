import it.unibo.tuprolog.utils.Cache
import org.junit.Test

class LRUCacheTest {
    @Test
    fun test() {
        val cache = Cache.lru<String, Int>(4)
        println(cache)
        cache["A"] = 1
        println(cache)
        cache["B"] = 2
        println(cache)
        cache["C"] = 3
        println(cache)
        cache["D"] = 4
        println(cache)
        cache["E"] = 5
        println(cache)
        cache["F"] = 5
        println(cache)
        cache["G"] = 6
        println(cache)
        cache["H"] = 7
        println(cache)
    }
}