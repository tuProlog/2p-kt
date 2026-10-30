package it.unibo.tuprolog.examples

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.examples.solve.ThermostatAgent
import it.unibo.tuprolog.examples.solve.ThermostatAgentKt
import kotlin.test.assertEquals

class ThermostatSpec :
    FunSpec({
        test("kotlin-thermostat") {
            val agent = ThermostatAgentKt("thermostat", 20, 24, 15)
            agent.start()
            agent.join()
            assertEquals(21, agent.temperature)
        }

        test("java-thermostat") {
            val agent = ThermostatAgent("thermostat", 20, 24, 30)
            agent.start()
            agent.join()
            assertEquals(23, agent.temperature)
        }
    })
