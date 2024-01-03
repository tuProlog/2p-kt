package it.unibo.tuprolog.core

import java.text.Collator

actual fun compareStringsLocaleIndependently(
    string1: String,
    string2: String,
): Int = Collator.getInstance().compare(string1, string2)
