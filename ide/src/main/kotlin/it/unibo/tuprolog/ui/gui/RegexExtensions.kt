package it.unibo.tuprolog.ui.gui

/**
 * Small regex-composition helpers used by [SyntaxColoring] to build its combined token-matching pattern out
 * of smaller, independently readable regexes.
 *
 * An alternation (`this|other`) of the two patterns; equivalent to `anyOf(this, other)`.
 */
infix fun Regex.or(other: Regex): Regex = anyOf(this, other)

/** The concatenation of the two patterns (`this` immediately followed by `other`). */
infix fun Regex.and(other: Regex): Regex = Regex(pattern + other.pattern)

/** A regex matching whatever any of [regexps] matches (their patterns joined by `|`). */
fun anyOf(vararg regexps: Regex): Regex = anyOf(sequenceOf(*regexps))

/** See [anyOf]. */
fun anyOf(regexps: Iterable<Regex>): Regex = anyOf(regexps.asSequence())

/** See [anyOf]. */
fun anyOf(regexps: Sequence<Regex>): Regex = regexps.map { it.pattern }.joinToString("|").toRegex()

/**
 * Wraps this pattern in a capturing group, named [name] if given (as a Java `(?<name>...)` named group),
 * otherwise anonymous. Named groups are how [SyntaxColoring] tells which token kind matched.
 */
fun Regex.asGroup(name: String? = null): Regex =
    if (name == null) {
        "($pattern)"
    } else {
        "(?<$name>$pattern)"
    }.toRegex()

/** A regex matching [pattern] only when it occurs as a whole word (bounded by `\b` or string start/end); see [wordify]. */
fun wordOf(pattern: String): Regex = wordify(pattern).toRegex()

/** Wraps [pattern] so it only matches at word boundaries, e.g. so `"is"` does not match inside `"this"`. */
fun wordify(pattern: String): String = "(^|\\b)($pattern)(\\b|$)"

/** Shorthand for `wordOf(pattern)`. */
fun Regex.asWord(): Regex = wordOf(pattern)
