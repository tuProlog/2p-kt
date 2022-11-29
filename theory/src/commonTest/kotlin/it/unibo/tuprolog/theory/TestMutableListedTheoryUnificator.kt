package it.unibo.tuprolog.theory

import kotlin.test.BeforeTest
import kotlin.test.Test

class TestMutableListedTheoryUnificator {
    private val prototype = PrototypeTheoryUnificator(MutableTheory.Companion::listedOf)

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
