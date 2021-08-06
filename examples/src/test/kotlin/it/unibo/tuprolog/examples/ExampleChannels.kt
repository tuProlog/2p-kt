package it.unibo.tuprolog.examples

import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import org.junit.Test

class ExampleChannels {
    @Test
    fun testInputFromString() {
        val source = InputChannel.of("hello")
        println(source.read()) // h
        println(source.read()) // e
        println(source.read()) // l
    }

    @Test
    fun testInputFromFunction() {
        var i = 0
        val source = InputChannel.of { ('a' + i++).toString() }
        println(source.read()) // a
        println(source.read()) // b
        println(source.read()) // c
    }

    @Test
    fun testInputFromStdin() {
        val source = InputChannel.stdIn()
        println(source.read()) // block until a line is is prompted, then prints the first char
        // may print null in testing
    }

    @Test
    fun testOutputToStd() {
        val sink1 = OutputChannel.stdOut<String>()
        val sink2 = OutputChannel.stdErr<String>()

        sink1.write("Printed on stdout\n")
        sink2.write("Printed on stderr\n")
    }

    @Test
    fun testOutputToCallback() {
        val messages = mutableListOf<String>()
        val sink = OutputChannel.of<String> { messages += it }

        sink.write("Appended to messages")

        println(messages) // [Appended to messages]
    }
}
