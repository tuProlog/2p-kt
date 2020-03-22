@file:JvmName("Echo")
package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.core.format

fun main(args: Array<String>) {
    val parser = TermParser.withStandardOperators

    while(true) {
        print("> ")
        val line = readLine()
        val term = parser.parseTerm(line!!)
        println(term.format(TermFormatter.prettyExpressions()))
    }
}