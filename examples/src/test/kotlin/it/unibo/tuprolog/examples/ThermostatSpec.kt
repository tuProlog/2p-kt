package it.unibo.tuprolog.examples

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.examples.solve.ThermostatAgent
import it.unibo.tuprolog.examples.solve.ThermostatAgentKt

class ThermostatSpec :
    FunSpec({
        test("kotlin-thermostat") {
            val agent = ThermostatAgentKt("thermostat", 20, 24, 15)
            agent.start()
            agent.join()
            agent.temperature shouldBe 21
        }

        test("java-thermostat") {
            val agent = ThermostatAgent("thermostat", 20, 24, 30)
            agent.start()
            agent.join()
            agent.temperature shouldBe 23
        }
    })
