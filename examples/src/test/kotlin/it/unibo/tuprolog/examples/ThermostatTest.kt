package it.unibo.tuprolog.examples

import it.unibo.tuprolog.examples.solve.ThermostatAgent
import it.unibo.tuprolog.examples.solve.ThermostatAgentKt
import kotlin.test.Test
import kotlin.test.assertEquals

class ThermostatTest {
    @Test
    fun testKotlinThermostat() {
        val agent = ThermostatAgentKt("thermostat", 20, 24, 15)
        agent.start()
        agent.join()
        assertEquals(21, agent.temperature)
    }

    @Test
    fun testJavaThermostat() {
        val agent = ThermostatAgent("thermostat", 20, 24, 30)
        agent.start()
        agent.join()
        assertEquals(23, agent.temperature)
    }
}
