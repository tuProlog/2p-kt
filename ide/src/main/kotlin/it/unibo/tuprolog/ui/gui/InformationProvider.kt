package it.unibo.tuprolog.ui.gui

class InformationProvider {
    val javaVersion: String
        get() = System.getProperty("java.version")

    val javaFxVersion: String
        get() = System.getProperty("javafx.version")

    val windowDefaultSize: Pair<Double, Double> = 700.0 to 480.0
}