package it.unibo.tuprolog.core.parsing.test

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.parsing.PrologParserTest
import it.unibo.tuprolog.core.parsing.PrologParserTest.Companion.scope
import kotlin.test.*

class TermParserTest(private val testWithSpaces: Boolean, private val toBeParsed: String,private val expected: Term):
    PrologParserTest {

        fun getTerms(): List<List<Any>> {
            return getSpaceInvariantTerms().map { s ->
                listOf(true) + s
            } + getSpaceSensibleTerms().map { s ->
                listOf(false) + s
            }
        }

        private fun getSpaceInvariantTerms(): List<List<Any>> {
            return listOf(
                listOf("a", scope.atomOf("a")),
                listOf("a(1)", scope.structOf("a", scope.numOf(1))),
                listOf(
                    "a(1, b(2), c(d(3),e))",
                    scope.structOf(
                        "a",
                        scope.numOf(1),
                        scope.structOf("b", scope.numOf(2)),
                        scope.structOf("c", scope.structOf("d", scope.numOf(3)), scope.atomOf("e"))
                    )
                ),
                listOf("X", scope.varOf("X")),
                listOf("_", scope.varOf("_")),
                listOf("[]", scope.listOf()),
                listOf("[1]", scope.consOf(scope.numOf(1), scope.listOf())),
                listOf("[2]", scope.listOf(scope.numOf(2))),
                listOf("[3]", scope.listOf(scope.numOf(3))),
                listOf("[4]", scope.listOf(scope.numOf(4))),
                listOf(
                    "[1, a, 2, b]",
                    scope.listOf(scope.numOf(1), scope.atomOf("a"), scope.numOf(2), scope.atomOf("b"))
                ),
                listOf("[H | T]", scope.consOf(scope.varOf("H"), scope.varOf("T"))),
                listOf("1", scope.numOf(1))
            )
        }

        private fun getSpaceSensibleTerms(): List<List<Any>> {
            return listOf(
                listOf(Long.MAX_VALUE.toString(), Long.MAX_VALUE),
                listOf("1.1", scope.numOf(1.1)),
                listOf("true", scope.atomOf("true")),
                listOf("fail", scope.atomOf("fail"))
            )
        }

    @Test
    fun testTermIsParserCorrectly(){
        println("Parsing $toBeParsed equals $expected ?")
        assertEquals(expected,parseTerm(toBeParsed))
        println(" yes.")
        val withoutSpaces = toBeParsed.replace("\\s+","")
        println("Parsing $withoutSpaces equals $expected ?")
        assertEquals(expected,parseTerm(withoutSpaces))
        println(" yes.")
        if(testWithSpaces){
            val withSpaces = IntRange(0,toBeParsed.length).map{
                    i -> toBeParsed[i] +""
            }.filter {
                it.isNotEmpty()
            }.joinToString("    ", "    ", "    ")
            println("Parsing $withSpaces equals $expected ?")
            assertEquals(expected,parseTerm(withSpaces))
            println(" yes.")
        }
    }

}