package it.unibo.tuprolog.ui.gui

infix fun Regex.or(other: Regex): Regex =
    anyOf(this, other)

infix fun Regex.and(other: Regex): Regex =
    Regex(pattern + other.pattern)

fun anyOf(vararg regexps: Regex): Regex =
    anyOf(sequenceOf(*regexps))

fun anyOf(regexps: Iterable<Regex>): Regex =
    anyOf(regexps.asSequence())

fun anyOf(regexps: Sequence<Regex>): Regex =
    regexps.map { it.pattern }.joinToString("|").toRegex()

fun Regex.asGroup(name: String? = null): Regex =
    if (name == null) {
        "($pattern)"
    } else {
        "(?<$name>$pattern)"
    }.toRegex()

fun wordOf(pattern: String): Regex =
    wordify(pattern).toRegex()

fun wordify(pattern: String): String =
    "(^|\\b)($pattern)(\\b|$)"

fun Regex.asWord(): Regex =
    wordOf(pattern)
