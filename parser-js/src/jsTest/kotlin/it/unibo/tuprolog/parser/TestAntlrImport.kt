package it.unibo.tuprolog.parser

import kotlin.test.Test

class TestAntlrImport {
    @Test
    fun antlr4() {
        js("require('antlr4')")
    }

    @Test
    fun antlr4Error() {
        js("require('antlr4/src/antlr4/error')")
    }

    @Test
    fun antlr4Atn() {
        js("require('antlr4/src/antlr4/atn')")
    }
}
