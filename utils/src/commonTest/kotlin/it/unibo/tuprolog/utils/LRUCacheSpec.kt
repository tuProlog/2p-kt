package it.unibo.tuprolog.utils

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.utils.impl.LRUCache

class LRUCacheSpec :
    FunSpec({
        include(cacheTests(::LRUCache))
    })
