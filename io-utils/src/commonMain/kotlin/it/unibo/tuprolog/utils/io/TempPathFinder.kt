package it.unibo.tuprolog.utils.io

internal expect object TempPathFinder {

    val tempDirectory: String

    fun file(name: String, extension: String): String

    fun directory(name: String): String
}
