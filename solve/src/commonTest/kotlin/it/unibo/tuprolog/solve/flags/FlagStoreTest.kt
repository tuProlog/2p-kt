package it.unibo.tuprolog.solve.flags

import kotlin.test.Test
import kotlin.test.assertEquals

class FlagStoreTest {
    @Test
    fun `of(vararg NotableFlag) keeps every distinct flag, not just the last one`() {
        val store = FlagStore.of(Unknown, MaxArity, DoubleQuotes, LastCallOptimization, TrackVariables)
        assertEquals(5, store.size)
        assertEquals(Unknown.defaultValue, store[Unknown.name])
        assertEquals(MaxArity.defaultValue, store[MaxArity.name])
        assertEquals(DoubleQuotes.defaultValue, store[DoubleQuotes.name])
        assertEquals(LastCallOptimization.defaultValue, store[LastCallOptimization.name])
        assertEquals(TrackVariables.defaultValue, store[TrackVariables.name])
    }

    @Test
    fun `DEFAULT contains every notable flag at its own default value`() {
        assertEquals(5, FlagStore.DEFAULT.size)
        assertEquals(Unknown.defaultValue, FlagStore.DEFAULT[Unknown.name])
        assertEquals(TrackVariables.defaultValue, FlagStore.DEFAULT[TrackVariables.name])
    }
}
