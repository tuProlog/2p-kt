package it.unibo.tuprolog.examples

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel

class ExampleChannelsSpec :
    FunSpec({
        test("input-from-string") {
            val source = InputChannel.of("hello")
            println(source.read()) // h
            println(source.read()) // e
            println(source.read()) // l
        }

        test("input-from-function") {
            var i = 0
            val source = InputChannel.of { ('a' + i++).toString() }
            println(source.read()) // a
            println(source.read()) // b
            println(source.read()) // c
        }

        test("input-from-stdin") {
            val source = InputChannel.stdIn()
            println(source.read()) // block until a line is is prompted, then prints the first char
            // may print null in testing
        }

        test("output-to-std") {
            val sink1 = OutputChannel.stdOut<String>()
            val sink2 = OutputChannel.stdErr<String>()

            sink1.write("Printed on stdout\n")
            sink2.write("Printed on stderr\n")
        }

        test("output-to-callback") {
            val messages = mutableListOf<String>()
            val sink = OutputChannel.of<String> { messages += it }

            sink.write("Appended to messages")

            println(messages) // [Appended to messages]
        }
    })
