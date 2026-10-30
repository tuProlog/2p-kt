package it.unibo.tuprolog.utils

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.utils.impl.SimpleLRUCache

class SimpleLRUCacheSpec :
    FunSpec({
        include(cacheTests(::SimpleLRUCache))
    })
