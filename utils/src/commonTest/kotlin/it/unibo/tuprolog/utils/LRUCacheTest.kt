package it.unibo.tuprolog.utils

import it.unibo.tuprolog.utils.impl.LRUCache
import kotlin.test.Test

class LRUCacheTest : CacheTest by CacheTest.prototype(::LRUCache) {
    @Test
    override fun testKeysCaching() {
        super.testKeysCaching()
    }
}
