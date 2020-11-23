package it.unibo.tuprolog.core

actual fun compareStringsLocaleIndependently(string1: String, string2: String): Int =
    string1.compareTo(string2)
