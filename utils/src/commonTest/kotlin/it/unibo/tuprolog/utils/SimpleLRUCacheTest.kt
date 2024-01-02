package it.unibo.tuprolog.utils

import it.unibo.tuprolog.utils.impl.SimpleLRUCache
import kotlin.test.Test

class SimpleLRUCacheTest : CacheTest by CacheTest.prototype(::SimpleLRUCache) {
    @Test
    override fun testKeysCaching() {
        super.testKeysCaching()
    }
}
