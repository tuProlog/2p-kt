package it.unibo.tuprolog.theory

import kotlin.test.BeforeTest
import kotlin.test.Test

class TestMutableIndexedTheoryUnificator {
    private val prototype = PrototypeTheoryUnificator(MutableTheory.Companion::indexedOf)

    @BeforeTest
    fun initialize() {
        prototype.initialize()
    }

    @Test
    fun testUnificatorAffectsGet() {
        prototype.testUnificatorAffectsGet()
    }

    @Test
    fun testInizializationSetsUnificator() {
        prototype.testInizializationSetsUnificator()
    }

    @Test
    fun testEqualityAmongDifferentUnificators() {
        prototype.testEqualityAmongDifferentUnificators()
    }

    @Test
    fun testChangeUnificator() {
        prototype.testChangeUnificator()
    }
}
